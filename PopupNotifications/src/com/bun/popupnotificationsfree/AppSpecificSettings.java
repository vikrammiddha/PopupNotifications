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

public class AppSpecificSettings  extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	SharedPreferences prefs;

	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.app_specific);

		ctx = this;

		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setNotFontColor();
		setNotBackgroundColor();
		setBorderColor();
		setShowCircularImages();
		setFont();
		setFontSize();
		setTestLockscreenListener();
		setTestBannersListener();
		setWakeup();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

	}

	private void setNotFontColor(){


		final yuku.ambilwarna.widget.AmbilWarnaPreference customPref = (yuku.ambilwarna.widget.AmbilWarnaPreference) findPreference("app_font_color");


		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");

		Integer appFontColor = SharedPreferenceUtils.getAppFontColor(ctx, app);
		String fColor = "";

		if(appFontColor != -101){
			fColor = String.valueOf(appFontColor);
			SharedPreferenceUtils.setAppFontColorDefault(ctx, fColor);
		}else{
			fColor = String.valueOf(HelperUtils.getFontColor(ctx, app));
			SharedPreferenceUtils.setAppFontColorDefault(ctx, fColor);
		}
		
		customPref.forceSetValue(Integer.valueOf(fColor));
		//customPref.setSummary(app);
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String val = String.valueOf(newValue);
				SharedPreferenceUtils.setAppFontColor(ctx,val , app);
				//String vv = SharedPreferenceUtils.getAppFontColor(ctx, app);

				return true;
			}

		});
	}

	private void setNotBackgroundColor(){


		final yuku.ambilwarna.widget.AmbilWarnaPreference customPref = (yuku.ambilwarna.widget.AmbilWarnaPreference) findPreference("app_background_color_not");


		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");

		Integer appFontColor = SharedPreferenceUtils.getAppBGColor(ctx, app);
		String fColor = "";

		if(appFontColor != -101){
			fColor = String.valueOf(appFontColor);
			SharedPreferenceUtils.setAppBGColorDefault(ctx, fColor);
		}else{
			fColor = String.valueOf(HelperUtils.getBackgroundColor(ctx, app));
			SharedPreferenceUtils.setAppBGColorDefault(ctx, fColor);
		}

		customPref.forceSetValue(Integer.valueOf(fColor));
		//customPref.setSummary(app);
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String val = String.valueOf(newValue);
				SharedPreferenceUtils.setAppBGColor(ctx,val , app);
				//String vv = SharedPreferenceUtils.getAppFontColor(ctx, app);

				return true;
			}

		});
	}

	private void setShowCircularImages(){


		final CheckBoxPreference customPref = (CheckBoxPreference) findPreference("app_show_circular_images");


		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");

		String showCircularImages = SharedPreferenceUtils.getAppShowCircularImages(ctx, app);
		String scImages = "";

		if(showCircularImages != ""){
			scImages =showCircularImages;
			SharedPreferenceUtils.setAppShowCircularImagesDefault(ctx, Boolean.valueOf(scImages));
		}else{
			scImages = String.valueOf(HelperUtils.isCircularImage(ctx, app));
			SharedPreferenceUtils.setAppShowCircularImagesDefault(ctx, Boolean.valueOf(scImages));
		}

		customPref.setChecked(Boolean.parseBoolean(scImages));
		//customPref.setSummary(app);
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String val = String.valueOf(newValue);
				SharedPreferenceUtils.setAppShowCircularImages(ctx,val , app);
				//String vv = SharedPreferenceUtils.getAppFontColor(ctx, app);

				return true;
			}

		});
	}

	private void setBorderColor(){


		final yuku.ambilwarna.widget.AmbilWarnaPreference customPref = (yuku.ambilwarna.widget.AmbilWarnaPreference) findPreference("app_border_color_not");


		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");

		Integer appFontColor = SharedPreferenceUtils.getAppBorderColor(ctx, app);
		String fColor = "";

		if(appFontColor != -101){
			fColor = String.valueOf(appFontColor);
			SharedPreferenceUtils.setAppBorderColorDefault(ctx, fColor);
		}else{
			fColor = String.valueOf(HelperUtils.getBorderColor(ctx, app));
			SharedPreferenceUtils.setAppBorderColorDefault(ctx, fColor);
		}

		customPref.forceSetValue(Integer.valueOf(fColor));
		//customPref.setSummary(app);
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String val = String.valueOf(newValue);
				SharedPreferenceUtils.setAppBorderColor(ctx,val , app);
				//String vv = SharedPreferenceUtils.getAppFontColor(ctx, app);

				return true;
			}

		});
	}	

	private void setFont(){


		final com.bun.popupnotificationsfree.FontSelectionPreference customPref = (com.bun.popupnotificationsfree.FontSelectionPreference) findPreference("app_font");

		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");
		Utils.tempApp = app;

		String appFont = SharedPreferenceUtils.getAppFont(ctx, app);
		String font = "";

		if(appFont != ""){
			font = appFont;
			SharedPreferenceUtils.setAppFontDefault(ctx, font);
		}else{
			font = SharedPreferenceUtils.getFont(ctx); 
			SharedPreferenceUtils.setAppFontDefault(ctx, font);
		}

		customPref.setDefaultValue(font);
		//customPref.setSummary(app);
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String val = String.valueOf(newValue);
				SharedPreferenceUtils.setAppFont(ctx,val , app);
				//String vv = SharedPreferenceUtils.getAppFontColor(ctx, app);

				return true;
			}

		});
	}

	private void setFontSize(){


		final com.bun.popupnotificationsfree.SeekBarPreference customPref = (com.bun.popupnotificationsfree.SeekBarPreference) findPreference("app_font_size1");


		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");

		Integer appFontSize = SharedPreferenceUtils.getAppFontSize(ctx, app);
		Integer fSize = 0;

		if(appFontSize != -101){
			fSize = appFontSize;
			SharedPreferenceUtils.setAppFontSizeDefault(ctx, fSize);
		}else{
			fSize = HelperUtils.getFontSize(ctx, app);
			SharedPreferenceUtils.setAppFontSizeDefault(ctx, fSize);
		}

		//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		//sharedPrefs.edit().putInt("app_font_size1", fSize).commit();
		customPref.forceSetValue(Integer.valueOf(fSize));
		//customPref.setSummary(app);
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Integer val = (Integer)newValue;
				SharedPreferenceUtils.setAppFontSize(ctx,val , app);
				//String vv = SharedPreferenceUtils.getAppFontColor(ctx, app);

				return true;
			}

		});
	}

	private void setWakeup(){


		final CheckBoxPreference customPref = (CheckBoxPreference) findPreference("app_wake_up");


		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");

		String wakeUp = SharedPreferenceUtils.getAppWakeup(ctx, app);
		String scWakeup = "";

		if(wakeUp != ""){
			scWakeup =wakeUp;
			SharedPreferenceUtils.setAppWakeupDefault(ctx, Boolean.valueOf(scWakeup));
		}else{
			scWakeup = String.valueOf(HelperUtils.wakeOnNotification(ctx, app));
			SharedPreferenceUtils.setAppWakeupDefault(ctx, Boolean.valueOf(scWakeup));
		}

		customPref.setChecked(Boolean.parseBoolean(scWakeup));
		//customPref.setSummary(app);
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				//String val = String.valueOf(newValue);
				//SharedPreferenceUtils.setAppWakeup(ctx,val , app);
				HelperUtils.upgradeNowDialogue(ctx);

				return false;
			}

		});
	}

	private void setTestLockscreenListener(){
		Preference pref = findPreference("app_test_lockscreen");

		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");

		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				NotificationBean testBean = HelperUtils.getTestNotification(ctx, app);

				Utils.getNotList().add(testBean);

				Intent dialogIntent = new Intent(ctx, NotificationActivity.class);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(dialogIntent);

				return true;
			}
		});
	}

	private void setTestBannersListener(){
		Preference pref = findPreference("app_test_banner");

		Intent intent = getIntent();

		final String app = intent.getStringExtra("appName");


		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				NotificationBean testBean = HelperUtils.getTestNotification(ctx, app);
				Utils.getNotList().add(testBean);

				Utils.isServiceRunning = true;
				ctx.stopService(new Intent(ctx, BannerService.class));
				ctx.startService(new Intent(ctx, BannerService.class));

				return true;
			}
		});
	}

}
