package com.bun.popupnotificationsfree;


import java.util.Locale;

import com.bun.popupnotificationsfree.R;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class NotificationPreferenceActivity  extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	SharedPreferences prefs;

	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_preference);

		ctx = this;
		
		Utils.tempApp = "";

		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setMuteAllAppsPreferenceData();

		setSelectedAppListListener();

		setBlockedAppListener();

		setTimerPreference();	

		setSleepPreference();

		//setDisplayContactPreference();

		
		setFullScreenPreference();

		//setFontColorListener();

		setNotTypePreferenceData();

		Preference pref = findPreference("start_sleep_time");
		pref.setSummary(prefs.getString("start_sleep_time", "23:00"));

		Preference pref1 = findPreference("end_sleep_time");
		pref1.setSummary(prefs.getString("end_sleep_time", "07:00"));

		Preference pref2 = findPreference("settings_service_enable");
		Boolean isAccServiceRunning = Utils.isAccessibilityEnabled(this);
		if(isAccServiceRunning){
			pref2.setTitle(getString(R.string.service_active));
		}else{
			pref2.setTitle(getString(R.string.service_inactive));
		}

		setaccServiceListener();

		setTalkBackFix();

		setResetSettingsListener();

		setVibratePreference();

		setSyncPreferenceData();

		setDismissAllPreferenceData();

		setBannerTimePreferenceData();

		setEmailLogsListener();

		setContactDeveloperListener();

		setCreateLogsPreferenceData();

		
		setBannerLocationPreference();

		setThemePreference();
		
		setScreenTimeOutPreferenceData();

	}

	private void setBlockedAppListener(){
		Preference pref = findPreference("blocked_apps");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				Intent testIntent = new Intent(getApplicationContext(), BlockedAppsActivity.class);
				startActivity(testIntent);

				return true;
			}
		});
	}
	
	private void setCreateLogsPreferenceData(){
		PreferenceScreen screen = getPreferenceScreen();		

		final Preference customPref = (Preference) findPreference("create_logs");

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				if((Boolean)newValue == true){
					HelperUtils.writeLogs("---", ctx, false);
				}


				return true;
			}

		});
	}

	private void setEmailLogsListener(){
		Preference pref = findPreference("email_logs");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				String subject = "Popup Notifications Free Logs";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"bunny.decoder@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, subject);
                i.putExtra(Intent.EXTRA_TEXT   , HelperUtils.readLogs(ctx));
                try {
                        ctx.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                        //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

				return true;
			}
		});
	}

	private void setContactDeveloperListener(){
		Preference pref = findPreference("contact_developer");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				String emailBody = "";
				emailBody +=  "\n\n" +"Android Version : " + android.os.Build.VERSION.RELEASE + "\n";
                emailBody += "Phone Model : " + Feedback.getDeviceName() + "\n";
                emailBody += "Accessibility Service : " + Utils.isAccessibilityEnabled(ctx) + "\n";
                emailBody += "App Version : " + Feedback.getAppVersion(ctx) + "\n";
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

				return true;
			}
		});
	}

	
	private void setResetSettingsListener(){
		Preference pref = findPreference("reset_settings");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				alertForResetSettings();

				return true;
			}
		});
	}

	private void setTalkBackFix(){
		if(isSamsungPhoneWithTTS(ctx)){
			Preference pref = findPreference("talkback_fix");
			pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {

					alertForSamsungTTS();

					return true;
				}
			});

			Preference pref1 = findPreference("samsung_tts");
			pref1.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {

					Intent intent1 = new Intent();
					intent1.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri uri1 = Uri.fromParts("package", "com.samsung.SMT",
							null);
					intent1.setData(uri1);
					startActivity(intent1);

					Toast.makeText(getApplicationContext(),R.string.click_disable, 
							Toast.LENGTH_SHORT).show();

					return true;
				}
			});

			Preference pref2 = findPreference("google_tts");
			pref2.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {

					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri uri = Uri.fromParts("package", "com.google.android.tts",
							null);
					intent.setData(uri);
					startActivity(intent);

					Toast.makeText(getApplicationContext(),R.string.click_disable, 
							Toast.LENGTH_SHORT).show();

					return true;
				}
			});


		}
	}

	private void alertForSamsungTTS(){
		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				NotificationPreferenceActivity.this);

		// Setting Dialog Title
		alertDialog2.setTitle(getString(R.string.warning));

		// Setting Dialog Message
		alertDialog2.setMessage(R.string.tts_warning);

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog2.setPositiveButton(getString(R.string.close),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();


			}
		});

		// Showing Alert Dialog
		alertDialog2.show();

	}



	private void alertForResetSettings(){
		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				NotificationPreferenceActivity.this);

		// Setting Dialog Title
		alertDialog2.setTitle(getString(R.string.warning));

		// Setting Dialog Message
		alertDialog2.setMessage(R.string.reset_message);

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog2.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				SharedPreferenceUtils.resetAllPreferenceSettings(ctx);

				Toast.makeText(getApplicationContext(),R.string.reset_completed, 
						Toast.LENGTH_SHORT).show();

			}
		});

		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				dialog.cancel();
			}
		});

		// Showing Alert Dialog
		alertDialog2.show();

	}

	public boolean isSamsungPhoneWithTTS(Context context) {

		boolean retour = false;

		try {
			@SuppressWarnings("unused")
			ApplicationInfo info = context.getPackageManager()
			.getApplicationInfo("com.samsung.SMT", 0);
			retour = true;
		} catch (PackageManager.NameNotFoundException e) {
			retour = false;
		}



		return retour;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Preference pref2 = findPreference("settings_service_enable");
		Boolean isAccServiceRunning = Utils.isAccessibilityEnabled(this);
		if(isAccServiceRunning){
			pref2.setTitle(getString(R.string.service_active));
		}else{
			pref2.setTitle(getString(R.string.service_inactive));
		}
	}

	private void setaccServiceListener(){
		Preference pref = findPreference("settings_service_enable");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				if(ctx.getResources().getBoolean(R.bool.is_service_enabled)){
					Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
					startActivityForResult(intent, 0);
				}else{
					Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"); 
					startActivityForResult(intent, 0);
				}

				return true;
			}
		});
	}


	private void setTimerPreference(){
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}


	private void setNotTypePreferenceData(){


		final Preference customPref = (Preference) findPreference("notification_type_preference");
		((Preference) findPreference("banner_time_pref")).setEnabled(false);
		((Preference) findPreference("banner_location_preference")).setEnabled(false);

		String notTypePref = SharedPreferenceUtils.getNotType(this);

		if("".equals(notTypePref)){
			ListPreference lp = (ListPreference)customPref;
			lp.setValue("lockscreen_banners");
			customPref.setSummary(getString(R.string.lockscreen_and_banners_summary));
			((Preference) findPreference("banner_time_pref")).setEnabled(true);
			((Preference) findPreference("banner_location_preference")).setEnabled(true);
		}else{
			if("lockscreen".equals(notTypePref)){
				customPref.setSummary(getString(R.string.lock_screen_only_summary));
			}else if("lockscreen_popup".equals(notTypePref)){
				customPref.setSummary(getString(R.string.lockscreen_and_popup_summary));
			}else if("lockscreen_banners".equals(notTypePref)){
				customPref.setSummary(getString(R.string.lockscreen_and_banners_summary));
				((Preference) findPreference("banner_time_pref")).setEnabled(true);
				((Preference) findPreference("banner_location_preference")).setEnabled(true);
			}else if("popup".equals(notTypePref)){
				customPref.setSummary(getString(R.string.popup_only_summary));
			}else if("banners".equals(notTypePref)){
				customPref.setSummary(getString(R.string.banners_only_summary));
				((Preference) findPreference("banner_time_pref")).setEnabled(true);
				((Preference) findPreference("banner_location_preference")).setEnabled(true);
			}
		}




		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				HelperUtils.upgradeNowDialogue(ctx);


				return false;
			}

		});
	}

	private void setBannerLocationPreference(){


		final Preference customPref = (Preference) findPreference("banner_location_preference");

		String banLocPref = SharedPreferenceUtils.getBanLoc(ctx);        


		if("".equals(banLocPref)){
			ListPreference lp = (ListPreference)customPref;
			lp.setValue(getString(R.string.top));
			SharedPreferenceUtils.setBanLoc(ctx, getString(R.string.top));
			banLocPref = getString(R.string.top);                        
		}
		customPref.setSummary(getString(R.string.ban_loc_desc) + " : " + banLocPref );

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				customPref.setSummary(getString(R.string.ban_loc_desc) + " : " + newValue.toString() );

				return true;
			}

		});
	}

	private void setThemePreference(){


		final Preference customPref = (Preference) findPreference("theme");

		String themePref = SharedPreferenceUtils.getTheme(ctx);        


		if("".equals(themePref)){
			ListPreference lp = (ListPreference)customPref;
			lp.setValue("Cards");
			SharedPreferenceUtils.setTheme(ctx, getString(R.string.cards));
			themePref = getString(R.string.cards);                        
		}

		customPref.setSummary(getString(R.string.selected_theme) + " : " + themePref );

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				//customPref.setSummary(getString(R.string.selected_theme) + " : " + newValue.toString() );
				HelperUtils.upgradeNowDialogue(ctx);

				return false;
			}

		});
	}



	private void setBannerTimePreferenceData(){


		final Preference customPref = (Preference) findPreference("banner_time_pref");

		String bannerTimePref = SharedPreferenceUtils.getBannerTime(this);

		customPref.setSummary(getString(R.string.banner_time_summary) + " " + bannerTimePref + " " + getString(R.string.seconds));


		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				String time = (String)newValue;
				Boolean falseValue = false;

				if("".equals(time.trim())){
					time = "5"; 

				}else if(Integer.valueOf(time) > 9999){
					time = "5";
					falseValue = true;
				}

				if(falseValue){
					return false;
				}else{
					customPref.setSummary(getString(R.string.banner_time_summary) + " " + time + " " + getString(R.string.seconds));
				}

				return true;
			}

		});
	}
	
	private void setScreenTimeOutPreferenceData(){


		final Preference customPref = (Preference) findPreference("screen_timeout");

		String bannerTimePref = SharedPreferenceUtils.getScreenTimeOut(this);

		customPref.setSummary(getString(R.string.screen_timeout_summary) + " " + bannerTimePref + " " + getString(R.string.seconds));

		
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				
				Intent screenoffApp = ctx.getPackageManager().getLaunchIntentForPackage("com.katecca.screenofflock");
				
				if(screenoffApp == null){
					HelperUtils.installScreenLockMessage(ctx);
					return false;
				}
				
				String time = (String)newValue;
				Boolean falseValue = false;

				if("".equals(time.trim())){
					time = "10"; 

				}else if(Integer.valueOf(time) > 9999){
					time = "10";
					falseValue = true;
				}

				if(falseValue){
					return false;
				}else{
					customPref.setSummary(getString(R.string.screen_timeout_summary) + " " + time + " " + getString(R.string.seconds));
				}

				return true;
			}

		});
	}



	private void setDismissAllPreferenceData(){


		final Preference customPref = (Preference) findPreference("dismiss_all_left");

		Boolean dismissAllPref = SharedPreferenceUtils.getDismissAll(this);

		if(dismissAllPref == null){
			CheckBoxPreference cbp = (CheckBoxPreference)customPref;
			cbp.setChecked(false);
			customPref.setSummary(getString(R.string.dismiss_no_all_left_summary));
		}else{
			if(dismissAllPref){
				customPref.setSummary(getString(R.string.dismiss_all_left_summary));
			}else {
				customPref.setSummary(getString(R.string.dismiss_no_all_left_summary));
			}                
		}


		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				if((Boolean)newValue){
					customPref.setSummary(getString(R.string.dismiss_all_left_summary));
				}else {
					customPref.setSummary(getString(R.string.dismiss_no_all_left_summary));
				}
				return true;
			}

		});
	}

	private void setSyncPreferenceData(){
		PreferenceScreen screen = getPreferenceScreen();                

		final Preference customPref = (Preference) findPreference("sync_preference");

		if(ctx.getResources().getBoolean(R.bool.is_service_enabled)){
			customPref.setEnabled(false);
		}


		String notTypePref = SharedPreferenceUtils.getSyncType(this);

		if("".equals(notTypePref)){
			ListPreference lp = (ListPreference)customPref;
			lp.setValue("two_way");
			customPref.setSummary(getString(R.string.two_way_summary));
		}else{
			if("none".equals(notTypePref)){
				customPref.setSummary(getString(R.string.none_s_1_summary));
			}else if("one_way".equals(notTypePref)){
				customPref.setSummary(getString(R.string.one_way_summary));
			}else if("two_way".equals(notTypePref)){
				customPref.setSummary(getString(R.string.two_way_summary));
			}
		}


		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				if("none".equals(newValue.toString())){
					customPref.setSummary(getString(R.string.none_s_1_summary));
				}else if("one_way".equals(newValue.toString())){
					customPref.setSummary(getString(R.string.one_way_summary));
				}else if("two_way".equals(newValue.toString())){
					customPref.setSummary(getString(R.string.two_way_summary));
				}

				return true;
			}

		});
	}

	private void setSelectedAppListListener(){
		Preference pref = findPreference("mute_selected_screen");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				Intent testIntent = new Intent(getApplicationContext(), MuteSelectedAppActivity.class);
				startActivity(testIntent);

				return true;
			}
		});
	}

	String muteAllAppsSummary = "";

	private void setMuteAllAppsPreferenceData(){


		final Preference customPref = (Preference) findPreference("mute_all_apps");

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				HelperUtils.upgradeNowDialogue(ctx);

				return false;
			}

		});
	}

	private void setSleepPreference(){



		final Preference customPref = (Preference) findPreference("mute_sleep_hours");

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				HelperUtils.upgradeNowDialogue(ctx);


				return false;
			}

		});
	}

	private void setVibratePreference(){

		final Preference customPref = (Preference) findPreference("vibrate");

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				HelperUtils.upgradeNowDialogue(ctx);


				return false;
			}

		});
	}

	private void setFullScreenPreference(){

		final Preference customPref = (Preference) findPreference("full_screen_notification");

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				HelperUtils.upgradeNowDialogue(ctx);


				return false;
			}

		});
	}





	private void setTransparentBackgroundPreference(){

		final Preference customPref = (Preference) findPreference("transparent_background");

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				HelperUtils.upgradeNowDialogue(ctx);


				return false;
			}

		});
	}

	RadioGroup radioGroup1;

	RadioGroup radioGroup2;
	RadioButton radioButton2;

	private void showMuteOptions(final Preference customPref){
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.mute_app_dialogue, (ViewGroup) findViewById(R.id.cpRoot));



		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				NotificationPreferenceActivity.this);

		// Setting Dialog Title
		alertDialog2.setTitle("");

		// Setting Dialog Message
		alertDialog2.setView(layout);

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn

		radioGroup1 = (RadioGroup) layout.findViewById(R.id.muteOptions1);
		radioGroup1.setVisibility(View.GONE);
		alertDialog2.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {							

				radioGroup2 = (RadioGroup) layout.findViewById(R.id.muteOptions2);
				int selectedId = radioGroup2.getCheckedRadioButtonId();				
				radioButton2 = (RadioButton) layout.findViewById(selectedId);

				SharedPreferenceUtils.setAllowedApps(ctx, "com.AA", Utils.getMuteTime(ctx,radioButton2.getText().toString()));

				customPref.setSummary(getString(R.string.all_apps_muted_till)+ Utils.getMuteTime(ctx,radioButton2.getText().toString()));

				Toast.makeText(NotificationPreferenceActivity.this,
						Utils.getMuteToastText(ctx, ctx.getString(R.string.mute_all_apps), radioButton2.getText().toString(), ""), Toast.LENGTH_SHORT).show();
			}
		});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				dialog.cancel();
				customPref.setSummary("");
				Editor editor = prefs.edit();
				editor.putBoolean("mute_all_apps", false);
				editor.commit();

				CheckBoxPreference cbp = (CheckBoxPreference)customPref;
				cbp.setChecked(false);

				getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(true);

			}
		});

		alertDialog2.show();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		//Log.d("pref", "Value Changed---" + sp.getInt(key, 0));
		// TODO Auto-generated method stub
		if(key.equals("start_sleep_time")){
			Log.d("pref", "Value Changed---" + sp.getString(key, "23:00"));
			Preference pref = findPreference("start_sleep_time");
			pref.setSummary(sp.getString(key, "23:00"));
		}else if(key.equals("end_sleep_time")){
			Log.d("pref", "Value Changed---" + sp.getString(key, "07:00"));
			Preference pref = findPreference("end_sleep_time");
			pref.setSummary(sp.getString(key, "07:00"));
		}

	}


}
