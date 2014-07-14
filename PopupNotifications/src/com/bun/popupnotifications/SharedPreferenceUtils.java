package com.bun.popupnotifications;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPreferenceUtils {

	private static SharedPreferences appsPref;
	private static SharedPreferences blockedAppsPref;
	private static SharedPreferences.Editor appPref_editor;
	private static SharedPreferences.Editor blockedPref_editor;
	private static String APP_LIST = "APP_LIST";
	private static String BLOCKED_LIST = "BLOCKED_LIST";

	public static String getAppData(Context ctx, String packageName){


		String retVal = null;

		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);

		retVal = (String) appsPref.getString(packageName, "--" );

		return retVal;	

	}

	public static String getSleepTime(Context ctx, String time){
		if("start_time".equals(time)){
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
			String startTime = sp.getString("start_sleep_time", "00:00");
			return startTime;
		}else{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
			String endTime = sp.getString("end_sleep_time", "00:00");
			return endTime;
		}
	}

	public static void setAllowedApps(Context ctx, String packageName, String muteDate){

		Log.d("SP", "============" + packageName);

		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);

		appPref_editor = appsPref.edit();
		appPref_editor.putString(packageName, muteDate);
		appPref_editor.commit();

	}

	public static void setBlockedApps(Context ctx, String packageName, String muteDate){

		blockedAppsPref = ctx.getSharedPreferences(
				BLOCKED_LIST, Context.MODE_PRIVATE);

		blockedPref_editor = blockedAppsPref.edit();
		blockedPref_editor.putString(packageName, "--");
		blockedPref_editor.commit();

	}

	public static void removeApp(Context ctx, String packageName){
		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);

		appPref_editor = appsPref.edit();
		appPref_editor.remove(packageName);
		appPref_editor.commit();
	}

	public static void removeBlockedApp(Context ctx, String packageName){
		blockedAppsPref = ctx.getSharedPreferences(
				BLOCKED_LIST, Context.MODE_PRIVATE);

		blockedPref_editor = blockedAppsPref.edit();
		blockedPref_editor.remove(packageName);
		blockedPref_editor.commit();
	}

	public static void populateDefaultApps(Context ctx){


	}

	public static TreeSet<String> getAllAlowedApps(Context ctx){

		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);
		TreeSet<String> retSet = new TreeSet<String>();

		Map<String,?> keys = appsPref.getAll();

		for(Map.Entry<String,?> entry : keys.entrySet()){
			retSet.add(entry.getKey());
		}

		return retSet;
	}

	public static TreeSet<String> getAllBlockedApps(Context ctx){

		blockedAppsPref = ctx.getSharedPreferences(
				BLOCKED_LIST, Context.MODE_PRIVATE);
		TreeSet<String> retSet = new TreeSet<String>();

		Map<String,?> keys = blockedAppsPref.getAll();

		for(Map.Entry<String,?> entry : keys.entrySet()){
			retSet.add(entry.getKey());
		}

		return retSet;
	}

	public static Object getGenericPreferenceValue(Context ctx, String key, String type){

		try{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
			Object val = null;
			if(type.toUpperCase().equals("BOOLEAN")){
				val = sp.getBoolean(key, false);
			}else if(type.toUpperCase().equals("STRING")){
				val = sp.getString(key, "");
			}else if(type.toUpperCase().equals("INTEGER")){
				val = sp.getInt(key, 0);
			}
			return val;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static void resetAllPreferenceSettings(Context ctx){
		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);
		appPref_editor = appsPref.edit();
		appPref_editor.clear();
		appPref_editor.commit();

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		Editor editor = sharedPrefs.edit();
		editor.clear();
		editor.commit();

		loadDefaultSettings(ctx);

	}

	public static void setFirstTimeRun(Context ctx, Boolean bool){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putBoolean("FirstTimeRun", bool).commit();
	}

	public static void setNotType(Context ctx, String notType){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("notification_type_preference", notType).commit();

	}

	public static String getNotType(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("notification_type_preference", "");
	}
	
	public static void setBanLoc(Context ctx, String banLoc){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("banner_location_preference", banLoc).commit();

	}
	
	public static String getTimeType(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		return sharedPrefs.getString("timetype_preference", "standard");
	}
	
	public static String getScreenTimeOut(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("screen_timeout", "10");
	}
	
	public static String getBanLoc(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("banner_location_preference", "");
	}

	public static String getBannerTime(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("banner_time_pref", "5");
	}

	public static String getSyncType(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("sync_preference", "");
	}

	public static Boolean getDismissAll(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("dismiss_all_left", false);
	}
	
	/*public static Boolean getCircularImages(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("show_circular_images", false);
	}*/
	
	public static Boolean getCreateLogs(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("create_logs", false);
	}

	public static Boolean getFirstTimeRun(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("FirstTimeRun", false);
	}
	
	public static void setShowTutorial(Context ctx, Boolean bool){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putBoolean("showTutorial", bool).commit();
	}

	public static Boolean getShowTutorial(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("showTutorial", true);
	}

	public static void setShowFeedback(Context ctx, Boolean bool){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putBoolean("show_feedback", bool).commit();
	}

	public static Boolean getShowFeedback(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("show_feedback", true);
	}


	public static Integer getNotCount(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getInt("not_Count", 0);
	}

	public static void setNotCount(Context ctx, Integer count){        

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("not_Count", count).commit();

	}

		
	public static void setFontSize(Context ctx, Integer size){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("font_size1", size).commit();

	}	
	
	
	public static String getTheme(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		return sharedPrefs.getString("theme", "");
	}

	public static void setTheme(Context ctx, String theme){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("theme", theme).commit();

	}
	
	public static Integer getAppFontColor(Context ctx, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getInt("app_font_color" + app, -101);
	}

	public static void setAppFontColor(Context ctx, String color, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_font_color" + app, Integer.valueOf(color)).commit();
	}

	public static void setAppFontColorDefault(Context ctx, String color){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_font_color", Integer.valueOf(color)).commit();
	}


	public static Integer getAppBGColor(Context ctx, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getInt("app_background_color_not" + app, -101);
	}

	public static void setAppBGColor(Context ctx, String color, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_background_color_not" + app, Integer.valueOf(color)).commit();
	}

	public static void setAppBGColorDefault(Context ctx, String color){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_background_color_not", Integer.valueOf(color)).commit();
	}

	public static Integer getAppBorderColor(Context ctx, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getInt("app_border_color_not" + app, -101);
	}

	public static void setAppBorderColor(Context ctx, String color, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_border_color_not" + app, Integer.valueOf(color)).commit();
	}

	public static void setAppBorderColorDefault(Context ctx, String color){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_border_color_not", Integer.valueOf(color)).commit();
	}

	public static String getAppFont(Context ctx, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("app_font" + app, "");
	}

	public static void setAppFont(Context ctx, String font, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("app_font" + app, font).commit();
	}

	public static void setAppFontDefault(Context ctx, String font){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("app_font", font).commit();
	}

	public static String getAppShowCircularImages(Context ctx, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("app_show_circular_images" + app, "");
	}

	public static void setAppShowCircularImages(Context ctx, String val, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("app_show_circular_images" + app, val).commit();
	}

	public static void setAppShowCircularImagesDefault(Context ctx, Boolean val){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putBoolean("app_show_circular_images", val).commit();
	}

	public static String getAppWakeup(Context ctx, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getString("app_wake_up" + app, "");
	}

	public static void setAppWakeup(Context ctx, String val, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("app_wake_up" + app, val).commit();
	}

	public static void setAppWakeupDefault(Context ctx, Boolean val){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putBoolean("app_wake_up", val).commit();
	}

	public static Integer getAppFontSize(Context ctx, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getInt("app_font_size1" + app, -101);
	}

	public static void setAppFontSize(Context ctx, Integer fontSize, String app){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_font_size1" + app, fontSize).commit();
	}

	public static void setAppFontSizeDefault(Context ctx, Integer fontSize){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putInt("app_font_size1", fontSize).commit();
	}

	
	public static String getFont(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		String retVal = sharedPrefs.getString("font", null);
		if("normal".equals(retVal))
				retVal = null;
		return retVal;
	}

	public static void setFont(Context ctx, String font){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		String app = Utils.tempApp;
		if(!"".equals(app)){
			sharedPrefs.edit().putString("app_font" + app, font).commit();
		}else{
			sharedPrefs.edit().putString("font", font).commit();
		}

	}
	
	public static String getAppVersion(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		return sharedPrefs.getString("app_version", null);
	}

	public static void setAppVersion(Context ctx, String version){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("app_version", version).commit();
	}

	public static Boolean isBlockedApp(Context ctx, String packageName){
		String retVal = null;

		blockedAppsPref = ctx.getSharedPreferences(
				BLOCKED_LIST, Context.MODE_PRIVATE);

		retVal = (String) blockedAppsPref.getString(packageName, null );

		return retVal == null ? false : true;	
	}
	
	public static void setAppSpecificSettings(String app, Context ctx){

		if(!"".equals(app)){
			setAppSpecificSettingsValues(ctx, app);
		}else{
			for(String packageName : getAllAlowedApps(ctx)){
				setAppSpecificSettingsValues(ctx, packageName);
			}

		}

	}

	private static void setAppSpecificSettingsValues(Context ctx, String packageName){
		SharedPreferenceUtils.setAppFontColor(ctx, String.valueOf(Color.WHITE) , packageName);
		SharedPreferenceUtils.setAppBGColor(ctx,String.valueOf(Color.BLACK) , packageName);
		SharedPreferenceUtils.setAppShowCircularImages(ctx, "true", packageName);
		SharedPreferenceUtils.setAppBorderColor(ctx,String.valueOf(Color.WHITE) , packageName);
		SharedPreferenceUtils.setAppFont(ctx,"normal" , packageName);
		SharedPreferenceUtils.setAppFontSize(ctx, HelperUtils.getFontSize(ctx, "") , packageName);
		SharedPreferenceUtils.setAppWakeup(ctx,"true" , packageName);

	}

	public static void loadDefaultSettings(Context ctx){

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

		if(!getFirstTimeRun(ctx)){
			setAllowedApps(ctx,"com.google.android.gsf", "");
			setAllowedApps(ctx,"com.whatsapp", "");
			setAllowedApps(ctx,"com.android.email", "");
			setAllowedApps(ctx,"com.google.android.gm", "");
			setAllowedApps(ctx,"com.android.mms", "");
			setAllowedApps(ctx,"com.android.phone", "");
			setAllowedApps(ctx,"com.facebook.katana", "");
			setAllowedApps(ctx,"com.tencent.mm", "");
			setAllowedApps(ctx,"com.linkedin.android", "");
			setAllowedApps(ctx,"com.google.android.talk", "");		
			setAllowedApps(ctx,"com.sonyericsson.conversations", "");

			setBlockedApps(ctx,"com.google.android.youtube", "");
			setBlockedApps(ctx,"com.google.android.videos", "");




			sp.edit().putBoolean("expanded_notification", true).commit();
			sp.edit().putBoolean("full_screen_notification", false).commit();
			sp.edit().putInt("transparent_background1", 200).commit();
			sp.edit().putBoolean("wake_up", true).commit();
			sp.edit().putBoolean("mute_sleep_hours", false).commit();

			sp.edit().putString("start_sleep_time", "23:00").commit();
			sp.edit().putString("end_sleep_time", "07:00").commit();
			sp.edit().putInt("font_color", Color.WHITE).commit();
			sp.edit().putInt("background_color_not", Color.BLACK).commit();
			sp.edit().putInt("border_color_not", Color.WHITE).commit();
			sp.edit().putString("notification_type_preference", "lockscreen_banners").commit();
			sp.edit().putString("sync_preference", "two_way").commit();
			sp.edit().putBoolean("vibrate", true).commit();
			sp.edit().putBoolean("dismiss_all_left", false).commit();
			sp.edit().putBoolean("disable_animations", false).commit();
			sp.edit().putBoolean("disable_unlock", false).commit();
			sp.edit().putString("theme", ctx.getString(R.string.cards)).commit();
			sp.edit().putInt("border_size_pref1", 3).commit();
			sp.edit().putBoolean("show_circular_images", true).commit();
			sp.edit().putString("timetype_preference", "13:00").commit();
			setFirstTimeRun(ctx, true);
			setAppSpecificSettings("", ctx);

		}

		String notTypeValue = sp.getString("notification_type_preference", "");

		if(notTypeValue.equals("") || (!notTypeValue.equals("lockscreen") && !notTypeValue.equals("lockscreen_popup") 
				&& !notTypeValue.equals("lockscreen_banners") && !notTypeValue.equals("popup") && !notTypeValue.equals("banners"))){
			sp.edit().putString("notification_type_preference", "lockscreen_banners").commit();
		}

		String syncTypeValue = sp.getString("sync_preference", "");

		if(syncTypeValue.equals("") || (!syncTypeValue.equals("none") && !syncTypeValue.equals("one_way") 
				&& !syncTypeValue.equals("two_way"))){
			sp.edit().putString("sync_preference", "two_way").commit();
		}
		
		String banLocValue = sp.getString("banner_location_preference", "");

		if(banLocValue.equals("") || (!banLocValue.equals(ctx.getString(R.string.top)) && !banLocValue.equals(ctx.getString(R.string.middle)) 
				&& !banLocValue.equals(ctx.getString(R.string.bottom)))){
			sp.edit().putString("banner_location_preference", ctx.getString(R.string.top)).commit();
		}
		
		String themeValue = sp.getString("theme", "");

		if(themeValue.equals("") || (!themeValue.equals(ctx.getString(R.string.cards)) && !themeValue.equals(ctx.getString(R.string.bubbles)))){
			sp.edit().putString("theme", ctx.getString(R.string.cards)).commit();
		}
		
		
						
		if(HelperUtils.isAppUpgrade(ctx)){
			
		}

	}


}
