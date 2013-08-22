package com.bun.popupnotifications;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {

	private static SharedPreferences appsPref;
	private static SharedPreferences.Editor appPref_editor;
	private static String APP_LIST = "APP_LIST";

	public static String getAppData(Context ctx, String packageName){


		String retVal = null;

		appsPref = ctx.getSharedPreferences(
				APP_LIST, Context.MODE_PRIVATE);

		retVal = (String) appsPref.getString(packageName, "--" );

		return retVal;	

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

}
