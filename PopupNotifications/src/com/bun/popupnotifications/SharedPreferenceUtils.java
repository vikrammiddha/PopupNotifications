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
	
	public static Boolean getFirstTimeRun(Context ctx){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		return sharedPrefs.getBoolean("FirstTimeRun", false);
	}

	public static Boolean isBlockedApp(Context ctx, String packageName){
		String retVal = null;

		blockedAppsPref = ctx.getSharedPreferences(
				BLOCKED_LIST, Context.MODE_PRIVATE);

		retVal = (String) blockedAppsPref.getString(packageName, null );

		return retVal == null ? false : true;	
	}

	public static void loadDefaultSettings(Context ctx){
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

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean("expanded_notification", true).commit();
		sp.edit().putBoolean("full_screen_notification", false).commit();
		sp.edit().putBoolean("transparent_background", true).commit();
		sp.edit().putBoolean("wake_up", true).commit();
		sp.edit().putBoolean("mute_sleep_hours", true).commit();

		sp.edit().putString("start_sleep_time", "23:00").commit();
		sp.edit().putString("end_sleep_time", "07:00").commit();
		sp.edit().putInt("font_color", Color.WHITE);
		sp.edit().putInt("background_color_not", Color.BLACK);
		sp.edit().putString("notification_type_preference", "both");

	}
	

}
