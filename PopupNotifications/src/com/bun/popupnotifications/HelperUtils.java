package com.bun.popupnotifications;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

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
		
		if(!(Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "mute_sleep_hours")){
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
		if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "expanded_notification")){
			return true;
		}
		
		return false;
	}
	
}
