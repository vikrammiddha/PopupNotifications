package com.bun.popupnotificationsfree;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FontSelectionPreference extends DialogPreference{
	
	Context ctx;
	RadioButton radioButton1;
	RadioButton radioButton2;
	RadioButton radioButton3;
	RadioButton radioButton4;
	RadioButton radioButton5;
	RadioButton radioButton6;
	RadioButton radioButton7;
	RadioButton radioButton8;
	RadioButton radioButton9;
	RadioButton radioButton10;
	RadioButton radioButton11;
	
	RadioGroup rg;

	public FontSelectionPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDialogLayoutResource(R.layout.font_selection);
		ctx = context;
	}
	
	@Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            builder.setTitle(R.string.font_title);
            builder.setPositiveButton(null, null);
            //builder.setNegativeButton(null, null);
            super.onPrepareDialogBuilder(builder);  
    }
	
	@Override
    public void onBindDialogView(View view){
		
			radioButton1 = (RadioButton) view.findViewById(R.id.fontaaargh);
			radioButton2 = (RadioButton) view.findViewById(R.id.fontarchitect);
			radioButton3 = (RadioButton) view.findViewById(R.id.fontarial);
			radioButton4 = (RadioButton) view.findViewById(R.id.fontcalligraffiti);
			radioButton5 = (RadioButton) view.findViewById(R.id.fontfingerpaint);
			radioButton6 = (RadioButton) view.findViewById(R.id.fontgeorgia);
			radioButton7 = (RadioButton) view.findViewById(R.id.fontruach);
			radioButton8 = (RadioButton) view.findViewById(R.id.fontshoni);
			radioButton9 = (RadioButton) view.findViewById(R.id.fontsofadi);
			radioButton10 = (RadioButton) view.findViewById(R.id.fonttypewriter);
			radioButton11 = (RadioButton) view.findViewById(R.id.fontnormal);
			
			radioButton1.setTypeface(FontLoader.getTypeFace(ctx,"aaargh"));
			radioButton2.setTypeface(FontLoader.getTypeFace(ctx,"architect"));
			radioButton3.setTypeface(FontLoader.getTypeFace(ctx,"arial"));
			radioButton4.setTypeface(FontLoader.getTypeFace(ctx,"calligraffiti"));
			radioButton5.setTypeface(FontLoader.getTypeFace(ctx,"fingerpaint"));
			radioButton6.setTypeface(FontLoader.getTypeFace(ctx,"georgia"));
			radioButton7.setTypeface(FontLoader.getTypeFace(ctx,"ruach"));
			radioButton8.setTypeface(FontLoader.getTypeFace(ctx,"shoni"));
			radioButton9.setTypeface(FontLoader.getTypeFace(ctx,"sofadi"));
			radioButton10.setTypeface(FontLoader.getTypeFace(ctx,"typewriter"));
			
			String app = Utils.tempApp;
			
			String font = "";
			if("".equals(app)){
				font = SharedPreferenceUtils.getFont(ctx);
			}else{
				font = SharedPreferenceUtils.getAppFont(ctx, app);
			}
			
			if(font == null)
				font = "";
			
			if(font.equals("aaargh")){
				radioButton1.setChecked(true);
			}else if(font.equals("architect")){
				radioButton2.setChecked(true);
			}else if(font.equals("arial")){
				radioButton3.setChecked(true);
			}else if(font.equals("calligraffiti")){
				radioButton4.setChecked(true);
			}else if(font.equals("fingerpaint")){
				radioButton5.setChecked(true);
			}else if(font.equals("georgia")){
				radioButton6.setChecked(true);
			}else if(font.equals("ruach")){
				radioButton7.setChecked(true);
			}else if(font.equals("shoni")){
				radioButton8.setChecked(true);
			}else if(font.equals("sofadi")){
				radioButton9.setChecked(true);
			}else if(font.equals("aaatypewriterrgh")){
				radioButton10.setChecked(true);
			}else{
				radioButton11.setChecked(true);
			}
			rg = (RadioGroup) view.findViewById(R.id.fontTypeId);
            
            super.onBindDialogView(view);
    }
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
	    switch (which) {
	    case DialogInterface.BUTTON_POSITIVE:
	    	 
			int selectedId = rg.getCheckedRadioButtonId();				
			
			if(selectedId == radioButton1.getId()){
				SharedPreferenceUtils.setFont(ctx, "aaargh");
			}else if(selectedId == radioButton2.getId()){
				SharedPreferenceUtils.setFont(ctx, "architect");
			}else if(selectedId == radioButton3.getId()){
				SharedPreferenceUtils.setFont(ctx, "arial");
			}else if(selectedId == radioButton4.getId()){
				SharedPreferenceUtils.setFont(ctx, "calligraffiti");
			}else if(selectedId == radioButton5.getId()){
				SharedPreferenceUtils.setFont(ctx, "fingerpaint");
			}else if(selectedId == radioButton6.getId()){
				SharedPreferenceUtils.setFont(ctx, "georgia");
			}else if(selectedId == radioButton7.getId()){
				SharedPreferenceUtils.setFont(ctx, "ruach");
			}else if(selectedId == radioButton8.getId()){
				SharedPreferenceUtils.setFont(ctx, "shoni");
			}else if(selectedId == radioButton9.getId()){
				SharedPreferenceUtils.setFont(ctx, "sofadi");
			}else if(selectedId == radioButton10.getId()){
				SharedPreferenceUtils.setFont(ctx, "typewriter");
			}
			
	        // save your new password here
	        break;
	    default:
	        // do something else...
	        break;
	    }
	}

}