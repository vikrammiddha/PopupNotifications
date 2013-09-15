package com.bun.popupnotifications;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceUtils {

	private static SharedPreferences appsPref;
	private static SharedPreferences.Editor appPref_editor;
	private static String APP_LIST = "APP_LIST";
	private static SharedPreferences generalPref;
	private static SharedPreferences.Editor genPref_editor;
	private static String GENERAL_LIST = "GENERAL_LIST";

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

		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);

		appPref_editor = appsPref.edit();
		appPref_editor.putString(packageName, muteDate);
		appPref_editor.commit();

	}

	public static void removeApp(Context ctx, String packageName){
		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);

		appPref_editor = appsPref.edit();
		appPref_editor.remove(packageName);
		appPref_editor.commit();
	}

	public static void populateDefaultApps(Context ctx){

		setAllowedApps(ctx,"com.google.android.gsf", "");
		setAllowedApps(ctx,"com.whatsapp", "");
		setAllowedApps(ctx,"com.android.email", "");
		setAllowedApps(ctx,"com.google.android.gm", "");
		setAllowedApps(ctx,"com.android.mms", "");
		setAllowedApps(ctx,"com.android.phone", "");
		setAllowedApps(ctx,"com.facebook.katana", "");
		setAllowedApps(ctx,"com.tencent.mm", "");
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

}
