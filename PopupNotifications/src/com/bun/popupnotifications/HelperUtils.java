package com.bun.popupnotifications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class HelperUtils {

	public static Boolean isBlockedTime(String s, Context ctx, String packageName){

		if(s.toUpperCase().equals(ctx.getString(R.string.for_ever))){
			return true;
		}
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());		
		long dateNow = calendar.getTimeInMillis();		


		Date date = null;
		try {
			date = formatter.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long modifiedDate= date.getTime();

		if(dateNow < modifiedDate){
			return true;
		}else{
			SharedPreferenceUtils.setAllowedApps(ctx, packageName, "");
		}

		return false;
	}

	public static Boolean isSleepTime(Context ctx){

		if(!(Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "mute_sleep_hours", "Boolean")){
			return false;
		}

		String startTime = SharedPreferenceUtils.getSleepTime(ctx, "start_time");

		String endTime = SharedPreferenceUtils.getSleepTime(ctx, "end_time");


		DateFormat formatter = new SimpleDateFormat("HH:mm");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		//get current date time with Date()
		Date date = new Date();
		String tempCurrentDate = dateFormat.format(date);	
		tempCurrentDate = tempCurrentDate.split(" ")[1];

		Date currentDateTime = null;

		Date startDateTime = null;
		Date endDateTime = null;
		try {
			startDateTime = formatter.parse(startTime);
			endDateTime = formatter.parse(endTime);
			currentDateTime = formatter.parse(tempCurrentDate);

			if(startDateTime.after(endDateTime)){

				Date now = new Date();

				startDateTime.setYear(now.getYear());
				startDateTime.setMonth(now.getMonth());
				startDateTime.setDate(now.getDay());

				endDateTime.setYear(now.getYear());
				endDateTime.setMonth(now.getMonth());
				endDateTime.setDate(now.getDay());

				Calendar cal = Calendar.getInstance();
				cal.setTime(endDateTime);
				cal.add(Calendar.DATE, 1);

				endDateTime = cal.getTime();

				currentDateTime.setYear(now.getYear());
				currentDateTime.setMonth(now.getMonth());
				currentDateTime.setDate(now.getDay());

				if(!currentDateTime.after(startDateTime)){
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(currentDateTime);				
					cal1.add(Calendar.DATE, 1);
					currentDateTime = cal1.getTime();
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(currentDateTime.after(startDateTime) && currentDateTime.before(endDateTime)){
			return true;
		}

		/*long startInt = startDateTime.getTime();
		long endInt = endDateTime.getTime();
		long currentInt = currentDateTime.getTime();

		if(currentInt > startInt && currentInt < endInt){
			return true;
		}*/

		return false;
	}

	public static Boolean isExpandedNotifications(Context ctx){
		try{
			if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "expanded_notification" ,"Boolean")){
				return true;
			}
		}catch(Exception e){
			return false;
		}

		return false;
	}
	
	public static Boolean isDisableAnimations(Context ctx){
		try{
			if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "disable_animations" ,"Boolean")){
				return true;
			}
		}catch(Exception e){
			return false;
		}

		return false;
	}
	
	public static Boolean isDisableUnlock(Context ctx){
		try{
			if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "disable_unlock" ,"Boolean")){
				return true;
			}
		}catch(Exception e){
			return false;
		}

		return false;
	}

	public static Boolean isFullScreenNotifications(Context ctx){
		if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "full_screen_notification", "Boolean")){
			return true;
		}

		return false;

	}

	public static Integer getTransparentBackround(Context ctx){

		Object val = SharedPreferenceUtils.getGenericPreferenceValue(ctx, "transparent_background1", "Integer");
		if(val == null || "".equals(val)){
			val = 200;
		}
		
		return (Integer)val;
	}
	
	public static Integer getBorderSize(Context ctx){

		Object val = SharedPreferenceUtils.getGenericPreferenceValue(ctx, "border_size_pref1", "Integer");
		if(val == null || "".equals(val)){
			val = 3;
		}
		
		return (Integer)val;
	}
	
	public static Integer getFontSize(Context ctx, String app){

		try{
			if(SharedPreferenceUtils.getAppFontSize(ctx, app) != -101){
				return SharedPreferenceUtils.getAppFontSize(ctx, app);
			}else{
				Object val = SharedPreferenceUtils.getGenericPreferenceValue(ctx, "font_size1", "Integer");
				if(val == null || ((Integer)val)  == 0){
					val = -101;
				}

				return (Integer)val;
			}
		}catch(Exception e){
			e.printStackTrace();
			return -101;
		}


	}
	
	public static Boolean isCircularImage(Context ctx, String app){

		try{
			if(SharedPreferenceUtils.getAppShowCircularImages(ctx, app) != ""){
				return Boolean.valueOf(SharedPreferenceUtils.getAppShowCircularImages(ctx, app));
			}else{
				Object val = SharedPreferenceUtils.getGenericPreferenceValue(ctx, "show_circular_images", "Boolean");

				return (Boolean)val;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}


	}
	
	public static Integer getMaxLines(Context ctx){

		Object val = SharedPreferenceUtils.getGenericPreferenceValue(ctx, "no_of_lines_pref1", "Integer");
		if(val == null || ((Integer)val)  == 0){
			val = 10;
		}
		
		return (Integer)val;
	}
	
	
	public static int getFontColor(Context ctx, String app){

		try{
			if(SharedPreferenceUtils.getAppFontColor(ctx, app) != -101){
				return SharedPreferenceUtils.getAppFontColor(ctx, app);
			}else{
				Integer retVal = (Integer)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "font_color", "Integer");
				if(retVal == 0)
					return Color.WHITE;
				else
					return retVal;
			}
		}catch(Exception e){
			e.printStackTrace();
			return Color.WHITE;
		}

		//return Color.WHITE;
	}
	
	public static int getBorderColor(Context ctx, String app){

		try{
			if(SharedPreferenceUtils.getAppBorderColor(ctx, app) != -101){
				return SharedPreferenceUtils.getAppBorderColor(ctx, app);
			}else{
				Integer retVal = (Integer)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "border_color_not", "Integer");
				if(retVal == 0)
					return Color.WHITE;
				else
					return retVal;
			}
		}catch(Exception e){
			e.printStackTrace();
			return Color.WHITE;
		}

		//return Color.WHITE;

	}

	public static Integer getBackgroundColor(Context ctx, String app){ 

		try{
			if(SharedPreferenceUtils.getAppBGColor(ctx, app) != -101){
				return SharedPreferenceUtils.getAppBGColor(ctx, app);
			}else{
				Integer retVal = (Integer)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "background_color_not", "Integer");
				if(retVal == 0)
					return Color.BLACK;
				else
					return retVal;
			}
		}catch(Exception e){
			e.printStackTrace();
			return Color.BLACK;
		}

		//return Color.BLACK;
	}
	public static Integer getTextSize(Context ctx){

		try{
			return (Integer)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "background_color_not", "Integer");
		}catch(Exception e){
			e.printStackTrace();
			return 10;
		}
	}

	public static Boolean wakeOnNotification(Context ctx, String app){

		try{
			if(SharedPreferenceUtils.getAppWakeup(ctx, app) != ""){
				return Boolean.valueOf(SharedPreferenceUtils.getAppWakeup(ctx, app));
			}else{
				Object val = SharedPreferenceUtils.getGenericPreferenceValue(ctx, "wake_up", "Boolean");

				return (Boolean)val;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}



	}


	public static Boolean isLockscreenOnly(Context ctx){

		try{

			String notType = (String)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "notification_type_preference", "STRING");
			KeyguardManager myKM = (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);

			if(myKM.inKeyguardRestrictedInputMode()){
				if(notType.equals("lockscreen") || notType.equals("lockscreen_popup") || notType.equals("lockscreen_banners")){
					return true;
				}
			}else{
				if(!notType.equals("lockscreen")){
					return true;
				}
			}

		}catch(Exception e){
			return false;
		}                

		return false;

	}

	public static Integer getNotType(Context ctx){
		try{
			String notType = (String)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "notification_type_preference", "STRING");

			if(notType.equals("lockscreen")){
				return Constants.NOT_LOCKSCREEN;
			}else if(notType.equals("lockscreen_popup")){
				return Constants.LOCKSCREEN_POPUP;
			}else if(notType.equals("lockscreen_banners")){
				return Constants.LOCKSCREEN_BANNER;
			}else if(notType.equals("popup")){
				return Constants.NOT_POPUP;
			}else if(notType.equals("banners")){
				return Constants.NOT_BANNERS;
			}


		}catch(Exception e){
			return -1;
		}

		return -1;
	}

	public static Boolean isBlockedApp(Context ctx, String packageName){
		try{
			ActivityManager am = (ActivityManager) ctx.getSystemService("activity");
			// The first in the list of RunningTasks is always the foreground task.
			RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

			packageName = foregroundTaskInfo.topActivity.getPackageName();

			if((Boolean)SharedPreferenceUtils.isBlockedApp(ctx, packageName)){
				return true;
			}

		}catch(Exception e){
			return false;
		}

		return false;
	}

	public static Drawable getAppIcon(String packageName, Context ctx){


		Drawable icon = null;
		try{
			icon = ctx.getPackageManager().getApplicationIcon(packageName);
		}catch(Exception e){
			e.printStackTrace();
		}

		if(icon == null){
			icon = ctx.getResources().getDrawable( R.drawable.ic_launcher );
		}

		return icon;
	}

	public static Boolean isVibrate(Context ctx){

		Boolean val = (Boolean) SharedPreferenceUtils.getGenericPreferenceValue(ctx, "vibrate", "BOOLEAN");

		return val;
	}

	public static Boolean dismissAllNotifications(String packageName, Context ctx){

		if(SharedPreferenceUtils.getDismissAll(ctx))
		{
			return true;
		}

		int count = 0;

		for(NotificationBean n : Utils.getNotList()){
			if(n.getPackageName().equals(packageName) && !n.getIsOddRow()){
				count++;
			}
		}

		if(count == 1){
			return true;
		}

		return false;
	}

	public static Boolean showFeedback(Context ctx, Integer cnt){

		Integer count = SharedPreferenceUtils.getNotCount(ctx);

		if(cnt > 0)
			count = cnt;

		if((count == 50 || count == 100 || count == 150 || (count > 150 && count%50 == 0)) && SharedPreferenceUtils.getShowFeedback(ctx)){
			return true;
		}
		return false;

	}
	
	public static void writeLogs(String s, Context ctx, Boolean append){
		
		if(!SharedPreferenceUtils.getCreateLogs(ctx) && append == true){
			return;
		}
		if(s.toLowerCase().contains("whatsapp")){
			return;
		}
		
		String filenName = new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "-PopupNotifications.Log";
		
		//s = s+ "\n";
		
		BufferedWriter bufferedWriter;
		try {
			String filePath = ctx.getFilesDir()+File.separator+filenName;
			bufferedWriter = new BufferedWriter(new FileWriter(new 
			File(filePath), append));
			bufferedWriter.write(s);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static String readLogs(Context ctx){
		String filenName = new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "-PopupNotifications.Log";
		BufferedReader bufferedReader;
		StringBuilder builder = new StringBuilder("");
		try {
			bufferedReader = new BufferedReader(new FileReader(new 
			        File(ctx.getFilesDir()+File.separator+filenName)));
			
			String read;			
			
			while((read = bufferedReader.readLine()) != null){
				builder.append(read);
				builder.append("\n");
			}
			//Log.d("Output", builder.toString());
			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return builder.toString();
		
	}
	
	public static NotificationBean getTestNotification(Context ctx, String packageName){

		NotificationBean nb = new NotificationBean();
		nb.setAppName("Popup Notifications");
		if(!"".equals(packageName))
			nb.setPackageName(packageName);
		else{
			nb.setPackageName("com.bun.popupnotifications");
		}
		nb.setId(0);
		nb.setIsOddRow(false);
		nb.setMessage("This is a test message");
		nb.setSender("Bunny Decoder");
		nb.setIcon(HelperUtils.getAppIcon(nb.getPackageName(), ctx));

		DateFormat formatter;
		String timeType = SharedPreferenceUtils.getTimeType(ctx);
		if(timeType.equals("13:00")){
			formatter = new SimpleDateFormat("HH:mm"); 
		}else{
			formatter = new SimpleDateFormat("hh:mm a"); 
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String formattedDate = formatter.format(calendar.getTime()).replaceAll("am","AM").replaceAll("pm", "PM");
		nb.setNotTime(formattedDate);

		return nb;

	}
	
	public static Boolean isAppUpgrade(Context ctx){
		PackageInfo pInfo;
		try {
			pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			String version = pInfo.versionName;
			
			String prevVersion = SharedPreferenceUtils.getAppVersion(ctx);
			
			if(prevVersion == null || !prevVersion.equals(version)){
				return true;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
		
		
	}
	
	public static void installScreenLockMessage(final Context ctx){


		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				ctx);

		// Setting Dialog Title
		alertDialog2.setTitle("");

		// Setting Dialog Message
		//alertDialog2.setView(layout);
		alertDialog2.setMessage(ctx.getString(R.string.install_screen_timeout_message));

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog2.setPositiveButton(ctx.getString(R.string.install),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(ctx.getString(R.string.market_url_screenurl)));
				ctx.startActivity(intent);
			}
		});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton(ctx.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {                                
				dialog.cancel();
			}
		});

		alertDialog2.show();

	}

}
