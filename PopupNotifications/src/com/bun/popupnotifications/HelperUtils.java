package com.bun.popupnotifications;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class HelperUtils {

	public static Boolean isBlockedTime(String s, Context ctx, String packageName){

		if(s.toUpperCase().equals("FOR EVER")){
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
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());			
		String currentTime = formatter.format(calendar.getTime());

		int startInt = Integer.valueOf(startTime.replaceAll(":", ""));
		int endInt = Integer.valueOf(endTime.replaceAll(":", ""));
		int currentInt = Integer.valueOf(currentTime.replaceAll(":", ""));

		if(currentInt > startInt && currentInt < endInt){
			return true;
		}

		return false;
	}

	public static Boolean isExpandedNotifications(Context ctx){
		if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "expanded_notification" ,"Boolean")){
			return true;
		}

		return false;
	}

	public static Boolean isFullScreenNotifications(Context ctx){
		if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "full_screen_notification", "Boolean")){
			return true;
		}

		return false;

	}

	public static Boolean isTransparentBackround(Context ctx){

		if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "transparent_background", "Boolean")){
			return true;
		}

		return false;

	}

	public static int getFontColor(Context ctx){

		try{
			return (Integer)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "font_color", "Integer");
		}catch(Exception e){
			e.printStackTrace();
			return Color.BLACK;
		}

	}

	public static Integer getBackgroundColor(Context ctx){ 

		try{
			return (Integer)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "background_color_not", "Integer");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static Integer getTextSize(Context ctx){

		try{
			return (Integer)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "background_color_not", "Integer");
		}catch(Exception e){
			e.printStackTrace();
			return 10;
		}
	}

	public static Boolean wakeOnNotification(Context ctx){

		try{

			if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "wake_up", "Boolean")){
				return true;
			}
		}catch(Exception e){
			return false;
		}		
		
		return false;

	}
	
	public static Boolean isLockscreenOnly(Context ctx){

		try{

			if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "lockscreen_only", "Boolean")){
				return true;
			}
		}catch(Exception e){
			return false;
		}		
		
		return false;

	}

}
