package com.bun.popupnotifications;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

public class Feedback {        


        public static void initiateFeedback(final Context ctx,final Activity parent){


                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.feedback_dialog, (ViewGroup) parent.findViewById(R.id.fbRootId));


                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                                ctx,AlertDialog.THEME_HOLO_LIGHT);



                alertDialog2.setTitle("");

                alertDialog2.setView(layout);

                alertDialog2.setPositiveButton(ctx.getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                yesFeedback(ctx,parent);
                        }
                });

                alertDialog2.setNegativeButton(ctx.getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {                                
                                noFeedback(ctx,parent);
                        }
                });

                alertDialog2.setNegativeButton(ctx.getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {                                
                                noFeedback(ctx,parent);
                        }
                });

                alertDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                                parent.finish();
                        }

                });

                alertDialog2.show();

        }

        public static void yesFeedback(final Context ctx, final Activity parent){


                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.yes_feedback, (ViewGroup) parent.findViewById(R.id.yesFBId));


                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                                ctx,AlertDialog.THEME_HOLO_LIGHT);

                alertDialog2.setTitle("");

                alertDialog2.setView(layout);

                alertDialog2.setPositiveButton(ctx.getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                CheckBox cb = (CheckBox) layout.findViewById(R.id.yesDoNotShow);
                                SharedPreferenceUtils.setShowFeedback(ctx, false);

                                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(ctx.getString(R.string.market_url)));
                                ctx.startActivity(intent);
                                //Utils.getNotList().clear();
                                //Utils.intentMap.clear();
                                parent.finish();
                        }
                });

                alertDialog2.setNegativeButton(ctx.getString(R.string.later),
                                new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {        
                                CheckBox cb = (CheckBox) layout.findViewById(R.id.yesDoNotShow);
                                if(cb != null && cb.isChecked()){
                                        SharedPreferenceUtils.setShowFeedback(ctx, false);
                                }
                                dialog.cancel();
                                //Utils.getNotList().clear();
                                //Utils.intentMap.clear();
                                parent.finish();
                        }
                });

                alertDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                                parent.finish();
                        }

                });



                alertDialog2.show(); 

        }

        public static void noFeedback(final Context ctx, final Activity parent){


                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.no_feedback, (ViewGroup) parent.findViewById(R.id.noFBId));




                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                                ctx,AlertDialog.THEME_HOLO_LIGHT);

                alertDialog2.setTitle("");

                alertDialog2.setView(layout);

                alertDialog2.setPositiveButton(ctx.getString(R.string.send),
                                new DialogInterface.OnClickListener() {
                        String emailBody = "";
                        public void onClick(DialogInterface dialog, int which) {

                                emailBody += "\n\n";

                                CheckBox cb = (CheckBox) layout.findViewById(R.id.fbCB1);
                                if(cb.isChecked()){
                                        emailBody += cb.getText().toString() + "\n";
                                }

                                cb = (CheckBox) layout.findViewById(R.id.fbCB2);
                                if(cb.isChecked()){
                                        emailBody += cb.getText().toString() + "\n";
                                }

                                cb = (CheckBox) layout.findViewById(R.id.fbCB3);
                                if(cb.isChecked()){
                                        emailBody += cb.getText().toString() + "\n";
                                }

                                cb = (CheckBox) layout.findViewById(R.id.fbCB4);
                                if(cb.isChecked()){
                                        emailBody += cb.getText().toString() + "\n";
                                }

                                EditText et = (EditText) layout.findViewById(R.id.othersId);
                                if(!"".equals(et.getText().toString())){
                                        emailBody += et.getText().toString() + "\n";;
                                }


                                emailBody +=  "\n\n" +"Android Version : " + android.os.Build.VERSION.RELEASE + "\n";
                                emailBody += "Phone Model : " + Feedback.getDeviceName() + "\n";
                                emailBody += "Accessibility Service : " + Utils.isAccessibilityEnabled(ctx) + "\n";
                                emailBody += "App Version : " + getAppVersion(ctx) + "\n";
                                emailBody += "Device Language : " + Locale.getDefault().getDisplayLanguage() + "\n";

                                String subject = ctx.getString(R.string.email_subject);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"bunny.decoder@gmail.com"});
                                i.putExtra(Intent.EXTRA_SUBJECT, subject);
                                i.putExtra(Intent.EXTRA_TEXT   , emailBody);
                                try {
                                        ctx.startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                        //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                                SharedPreferenceUtils.setShowFeedback(ctx, false);
                                //Utils.getNotList().clear();
                                //Utils.intentMap.clear();
                                parent.finish();
                        }
                });

                alertDialog2.setNegativeButton(ctx.getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                CheckBox cb = (CheckBox) layout.findViewById(R.id.noDoNotShow);
                                if(cb != null && cb.isChecked()){
                                        SharedPreferenceUtils.setShowFeedback(ctx, false);
                                }
                                dialog.cancel();
                                //Utils.getNotList().clear();
                                //Utils.intentMap.clear();
                                parent.finish();
                        }
                });

                alertDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                                parent.finish();
                        }

                });        


                alertDialog2.show();

        }

        public static String getDeviceName() {
                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
                if (model.startsWith(manufacturer)) {
                        return capitalize(model);
                } else {
                        return capitalize(manufacturer) + " " + model;
                }
        }

        private static String capitalize(String s) {
                if (s == null || s.length() == 0) {
                        return "";
                }
                char first = s.charAt(0);
                if (Character.isUpperCase(first)) {
                        return s;
                } else {
                        return Character.toUpperCase(first) + s.substring(1);
                }
        } 

        public static String getAppVersion(Context ctx){
                PackageInfo pInfo;
                try {
                        pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
                        return pInfo.versionName;
                } catch (NameNotFoundException e) {
                        return "";
                }

        }

}