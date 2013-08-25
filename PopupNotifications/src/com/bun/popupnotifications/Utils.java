package com.bun.popupnotifications;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.TextView;

public class Utils {

	public static ArrayList<NotificationBean> notList = new ArrayList<NotificationBean>();

	private Context ctx;

	HashMap<String, String> mInstalledApplications = new HashMap<String, String>();

	public Utils(Context ctx){
		this.ctx = ctx;
	}

	@SuppressLint("NewApi")
	public static String getAppSpecificMessage(String packageName, Notification notification){

		LinkedHashSet<String> valueSet = new LinkedHashSet<String>();
		ArrayList<String> raw = new ArrayList<String>();		

		if(notification == null){
			return "";
		}

		RemoteViews views = notification.bigContentView;
		if(views == null){
			views = notification.contentView;
		}

		if(views == null){
			return "" ;
		}

		Class<?> secretClass = views.getClass();



		Field outerFields[] = secretClass.getDeclaredFields();
		for (int i = 0; i < outerFields.length; i++) {
			if (outerFields[i].getName().equals("mActions")) {
				outerFields[i].setAccessible(true);

				ArrayList<Object> actions = null;
				try {
					actions = (ArrayList<Object>) outerFields[i].get(views);

					for (Object action : actions) {
						Field innerFields[] = action.getClass()
								.getDeclaredFields();

						Object value = null;
						Integer type = null;
						for (Field field : innerFields) {
							//Log.d("Field Name =======" , field.getName() );
							try {
								field.setAccessible(true);
								if (field.getName().equals("type")) {
									type = field.getInt(action);
								} else if (field.getName().equals("value")) {
									value = field.get(action);
								}

							} catch (IllegalArgumentException e) {
							} catch (IllegalAccessException e) {
							}
						}

						if (type != null && type == 10 && value != null) {

							Log.d("Value =======" , value.toString() );
							raw.add(value.toString());
						}
					}
				} catch (IllegalArgumentException e1) {
				} catch (IllegalAccessException e1) {
				}
			}
		}

		String message = "";

		if(packageName.equals("com.whatsapp")){
			if(raw.get(1).contains("message") && raw.get(2).contains("@")){
				message = raw.get(2).split(":") [0] + ":\n\n" + raw.get(2).split(":") [1];
						//raw.get(2).split("@")[1].split(":")[0] + ":\n\n" +  raw.get(2).split("@")[0];
			}
			else if(raw.get(2).contains(":")){
				String groupName = "";
				if(!raw.get(0).toLowerCase().equals("whatsapp")){
					groupName = raw.get(0) + " @ ";
				}
				message = groupName + raw.get(2).split(":")[0] + ":\n\n" +  raw.get(2).split(":")[1];
			}else{
				message = raw.get(0) + ":\n\n" + raw.get(2);
			}

		}else if(packageName.equals("com.google.android.gm")){
			message = raw.get(0) + ":\n\n" +  raw.get(4); 
		}else if(packageName.equals("com.android.email")){
			message = raw.get(0) + ":\n\n" +  raw.get(4);
		}else if(packageName.equals("com.android.mms")){
			message = raw.get(1) + ":\n\n" +  raw.get(1);
		}else if(packageName.equals("com.facebook.katana")){
			message = " " + ":\n\n" +  raw.get(1);
		}else if(packageName.equals("com.android.phone")){
			message = raw.get(2) + ":\n\n" +  raw.get(0);
		}


		return message;

	}

	public void populateBeanDetails(AccessibilityEvent event, NotificationBean bean){

		bean.setAppName(getMeaningFullAppName(getApplicationName(event.getPackageName().toString())));

		bean.setPackageName(event.getPackageName().toString());	

		setNotificationMessageAndSender(event, bean);

		Drawable icon;
		try{
			if(bean.getAppName().toUpperCase().equals("GOOGLE TALK")){
				icon = ctx.getResources().getDrawable( R.drawable.googletalk );
			}else{
				icon = ctx.getPackageManager().getApplicationIcon(bean.getPackageName());
			}

			bean.setIcon(icon); 
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@SuppressLint("NewApi")
	private void setNotificationMessageAndSender(AccessibilityEvent event, NotificationBean bean){

		StringBuffer retMessage = new StringBuffer();

		String message = "";

		String addInfo = "";

		try{

			try{
				Notification notification = (Notification) event.getParcelableData();
				LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ViewGroup localView = (ViewGroup) inflater.inflate(notification.contentView.getLayoutId(), null);
				notification.contentView.reapply(ctx.getApplicationContext(), localView);

				ArrayList<TextView> views = new ArrayList<TextView>();

				getAllTextView(views, localView);

				for (TextView v: views) {
					String text = v.getText().toString();
					if (!text.isEmpty()) {						
						message += text + "\n";
					}
				}

				Log.d("Utils", "messageText-----" + message);
				Log.d("Utils", "eventText-----" + event.getText().toString());
				if(event.getPackageName().toString().equals("com.whatsapp")){
					addInfo = Utils.getAppSpecificMessage(event.getPackageName().toString(), notification); 
				}else if(event.getPackageName().toString().equals("com.google.android.gm")){
					addInfo = Utils.getAppSpecificMessage(event.getPackageName().toString(), notification); 
				}else if(event.getPackageName().toString().equals("com.android.email")){
					addInfo = Utils.getAppSpecificMessage(event.getPackageName().toString(), notification);
				}else if(event.getPackageName().toString().equals("com.android.mms")){
					addInfo = event.getText().toString().split(":")[0] + ":\n\n" + event.getText().toString().split(":")[1];					
				}else if(event.getPackageName().toString().equals("com.facebook.katana")){
					addInfo = Utils.getAppSpecificMessage(event.getPackageName().toString(), notification);
				} else if(event.getPackageName().toString().equals("com.android.phone")){
					addInfo = Utils.getAppSpecificMessage(event.getPackageName().toString(), notification);
				} else if(event.getPackageName().toString().equals("com.tencent.mm")){
					addInfo = " " + ":\n\n" + event.getText().toString().replaceAll("\\[", "").replaceAll("\\]", "");
				}             

				if(!addInfo.equals("")){
					message = addInfo;					
				}else{
					addInfo = message;
					message = event.getText().toString();					
				}				

				String[] strArr = addInfo.split("\n");
				
				if(strArr.length == 1){
					retMessage.append(strArr[0]).append(" ");
				}else{

					for(Integer i = 0; i< strArr.length ;i++){
						if(i == 2){
							retMessage.append(strArr[i]).append(" ");
						}
					}		
				}


			}catch(Exception e){
				message = String.valueOf(event.getText());
				e.printStackTrace();				
			}

		}catch(Exception e){
			Log.e("NotificationHistory", "Exception in Handlingthe Event : " + e);
			message = String.valueOf(event.getText());

		}
		
		if(!addInfo.equals("")){
			bean.setSender(getSender(addInfo, addInfo));
		}else{
			bean.setSender(getSender(message, addInfo));
		}
		
		bean.setMessage(retMessage.toString());
		

	}

	private String getSender(String message, String addInfo){

		StringBuilder retMessage = new StringBuilder("");

		String[] strArr = message.split(":");

		for(Integer i = 0; i< strArr.length ;i++){
			if(i == 0){
				retMessage.append(strArr[i]).append(" ");
			}			
		}

		if(retMessage.toString().equals("")){
			strArr = addInfo.split("\n");

			for(Integer i = 0; i< strArr.length ;i++){
				if(i == 0){
					retMessage.append(strArr[i]).append(" ");
				}			
			}
		}

		String retString = retMessage.toString().replaceAll("\\[", "").replaceAll("\\]", "");

		return retString.split("\n")[0];
	}


	public Boolean performValidation(AccessibilityEvent event){
		
		if(isForgroundApp(ctx, event.getPackageName().toString())){
			return false;
		}

		String appMuteDate = SharedPreferenceUtils.getAppData(ctx, event.getPackageName().toString());
		
		Log.d("Utils", "appMuteDate--" + appMuteDate);

		if(appMuteDate != null && appMuteDate.equals("--")){
			return false;
		}
		
		if(appMuteDate == null || "".equals(appMuteDate)){
			return true;
		}else if(HelperUtils.getCurrentTime() < HelperUtils.getDateTimeValue(appMuteDate)){
			return false;
		}

		return true;

	}

	private String getApplicationName(String packageName) {
		String name = packageName;

		if (mInstalledApplications.containsKey(packageName)) {
			name = mInstalledApplications.get(packageName);
		} else {
			refreshApplicationList();
			if (mInstalledApplications.containsKey(packageName)) {
				name = mInstalledApplications.get(packageName);
			}
		}

		return name;
	}

	private void refreshApplicationList() {
		final PackageManager pm = ctx.getApplicationContext().getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo packageInfo : packages) {
			mInstalledApplications.put(packageInfo.packageName, packageInfo.loadLabel(pm).toString());
		}
	} 

	private void getAllTextView(ArrayList<TextView> views, ViewGroup v)
	{
		if (null == views) {
			return;
		}
		for (int i = 0; i < v.getChildCount(); i++)
		{
			Object child = v.getChildAt(i); 
			if (child instanceof TextView)
			{
				views.add((TextView)child);
			}
			else if(child instanceof ViewGroup)
			{
				getAllTextView(views, (ViewGroup)child);  // Recursive call.
			}
		}
	}
	
	private String getMeaningFullAppName(String appName){
		
		if(appName.toUpperCase().equals("GOOGLE SERVICES FRAMEWORK")){
			return "Google Talk";
		}
		
		return appName;
	}
	
	public static Boolean isForgroundApp(Context ctx, String packageName){
		
		ActivityManager am = (ActivityManager) ctx.getSystemService("activity");
    	// The first in the list of RunningTasks is always the foreground task.
    	RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
    	
    	String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
    	
    	if(foregroundTaskPackageName.equals("com.google.android.talk")){
    		foregroundTaskPackageName = "com.google.android.gsf";
    	}
    	
    	if(foregroundTaskPackageName.equals(packageName)){
    		return true;
    	}
    	
    	return false;
	}

}
