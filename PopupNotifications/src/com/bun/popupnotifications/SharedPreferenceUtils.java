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
	
	public static Boolean getCreateLogs(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("create_logs", false);
	}

	public static Boolean getFirstTimeRun(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("FirstTimeRun", false);
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

	public static String getMaxLines(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		return sharedPrefs.getString("no_of_lines_pref", "10");
	}

	public static void setMaxLines(Context ctx, String count){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("no_of_lines_pref", count).commit();

	}
	
	public static String getFontSize(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		return sharedPrefs.getString("font_size", "-1");
	}

	public static void setFontSize(Context ctx, String size){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("font_size", size).commit();

	}
	
		
	public static String getBorderSize(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		return sharedPrefs.getString("border_size_pref", "3");
	}

	public static void setBorderSize(Context ctx, String count){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("border_size_pref", count).commit();

	}
	
	public static String getTheme(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());                
		return sharedPrefs.getString("theme", "");
	}

	public static void setTheme(Context ctx, String theme){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		sharedPrefs.edit().putString("theme", theme).commit();

	}

	public static Boolean isBlockedApp(Context ctx, String packageName){
		String retVal = null;

		blockedAppsPref = ctx.getSharedPreferences(
				BLOCKED_LIST, Context.MODE_PRIVATE);

		retVal = (String) blockedAppsPref.getString(packageName, null );

		return retVal == null ? false : true;	
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
			sp.edit().putBoolean("transparent_background", true).commit();
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
			sp.edit().putString("border_size_pref", "3").commit();

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
		
		int borderColor = sp.getInt("border_color_not", -1);
		if(borderColor == -1){
			sp.edit().putInt("border_color_not", Color.WHITE).commit();
		}
		
		String borderSize = sp.getString("border_size_pref", "");

		if(borderSize.equals("")){
			sp.edit().putString("border_size_pref", "3").commit();
		}

	}


}
