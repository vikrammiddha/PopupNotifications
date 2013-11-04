package com.bun.popupnotifications;


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

		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setMuteAllAppsPreferenceData();

		setSelectedAppListListener();

		setBlockedAppListener();

		setTimerPreference();	

		//setFontColorListener();



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

		setNotTypePreferenceData();
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

				Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
				startActivityForResult(intent, 0);

				return true;
			}
		});
	}


	private void setTimerPreference(){
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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

		boolean muteAllApps = false;

		final Preference customPref = (Preference) findPreference("mute_all_apps");

		String muteAllAppsPref = SharedPreferenceUtils.getAppData(this, "com.AA");

		if(!"--".equals(muteAllAppsPref)){
			SharedPreferences.Editor p = PreferenceManager.getDefaultSharedPreferences(this).edit();
			p.putBoolean("mute_all_apps", true);
			p.commit();
			CheckBoxPreference cbp = (CheckBoxPreference)customPref;
			cbp.setChecked(true);
			muteAllApps = true;
		}


		if(muteAllApps){
			muteAllAppsSummary = getString(R.string.all_apps_muted_till)+ muteAllAppsPref;
			getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(false);

		}else{
			muteAllAppsSummary = "";
			getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(true);			
		}


		customPref.setSummary(muteAllAppsSummary);

		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				if(Boolean.valueOf(newValue.toString())){
					customPref.setSummary(getString(R.string.all_apps_muted));
					getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(false);
					showMuteOptions(customPref);
				}else{
					customPref.setSummary("");
					getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(true);
					SharedPreferenceUtils.removeApp(ctx, "com.AA");
				}
				return true;
			}

		});
	}

	private void setNotTypePreferenceData(){


		final Preference customPref = (Preference) findPreference("notification_type_preference");

		String notTypePref = SharedPreferenceUtils.getNotType(this);

		if("".equals(notTypePref)){
			ListPreference lp = (ListPreference)customPref;
			lp.setValue("both");
			customPref.setSummary(getString(R.string.both));
		}else{
            if("both".equals(notTypePref)){
                customPref.setSummary(getString(R.string.both));
        }else if("lockscreen".equals(notTypePref)){
                customPref.setSummary(getString(R.string.lockscreen_only));
        }else if("banner".equals(notTypePref)){
                customPref.setSummary(getString(R.string.banners_only));
        }
}


		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				if("lockscreen".equals(newValue.toString())){
					customPref.setSummary(getString(R.string.lockscreen_only));
				}else if("banner".equals(newValue.toString())){
					customPref.setSummary(getString(R.string.banners_only));
				}else{
					customPref.setSummary(getString(R.string.both));
				}
				
				return true;
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
