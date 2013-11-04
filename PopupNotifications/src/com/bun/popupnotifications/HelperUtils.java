package com.bun.popupnotifications;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class HelperUtils {

	public static Boolean isBlockedTime(String s, Context ctx, String packageName){

		if(s.toUpperCase().equals(ctx.getString(R.string.for_ever))){
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
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		//get current date time with Date()
		Date date = new Date();
		String tempCurrentDate = dateFormat.format(date);	
		tempCurrentDate = tempCurrentDate.split(" ")[1];

		Date currentDateTime = null;

		Date startDateTime = null;
		Date endDateTime = null;
		try {
			startDateTime = formatter.parse(startTime);
			endDateTime = formatter.parse(endTime);
			currentDateTime = formatter.parse(tempCurrentDate);

			if(startDateTime.after(endDateTime)){

				Date now = new Date();

				startDateTime.setYear(now.getYear());
				startDateTime.setMonth(now.getMonth());
				startDateTime.setDate(now.getDay());

				endDateTime.setYear(now.getYear());
				endDateTime.setMonth(now.getMonth());
				endDateTime.setDate(now.getDay());

				Calendar cal = Calendar.getInstance();
				cal.setTime(endDateTime);
				cal.add(Calendar.DATE, 1);

				endDateTime = cal.getTime();

				currentDateTime.setYear(now.getYear());
				currentDateTime.setMonth(now.getMonth());
				currentDateTime.setDate(now.getDay());
				
				if(!currentDateTime.after(startDateTime)){
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(currentDateTime);				
					cal1.add(Calendar.DATE, 1);
					currentDateTime = cal1.getTime();
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(currentDateTime.after(startDateTime) && currentDateTime.before(endDateTime)){
			return true;
		}

		/*long startInt = startDateTime.getTime();
		long endInt = endDateTime.getTime();
		long currentInt = currentDateTime.getTime();

		if(currentInt > startInt && currentInt < endInt){
			return true;
		}*/

		return false;
	}

	public static Boolean isExpandedNotifications(Context ctx){
		try{
			if((Boolean)SharedPreferenceUtils.getGenericPreferenceValue(ctx, "expanded_notification" ,"Boolean")){
				return true;
			}
		}catch(Exception e){
			return false;
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

	public static Boolean isBlockedApp(Context ctx, String packageName){
		try{
			ActivityManager am = (ActivityManager) ctx.getSystemService("activity");
			// The first in the list of RunningTasks is always the foreground task.
			RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

			packageName = foregroundTaskInfo.topActivity.getPackageName();

			if((Boolean)SharedPreferenceUtils.isBlockedApp(ctx, packageName)){
				return true;
			}

		}catch(Exception e){
			return false;
		}

		return false;
	}

}
