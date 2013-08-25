package com.bun.popupnotifications;

import java.util.ArrayList;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class NotificationService extends AccessibilityService { 

	DBController controller;	
	private Context ctx;
	public Utils utils;
	
	

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		
		Log.d("Notification Service", "Entered the accessibility method==" + event.getPackageName().toString());

		if(utils.performValidation(event)){
			
			NotificationBean bean = new NotificationBean();
			
			utils.populateBeanDetails(event, bean);
			
			if(bean.getSender().trim().equals("") && bean.getMessage().trim().equals("")){
				return;
			}
			
			utils.notList.add(0,bean);
			
			Log.d("Notification Service", "App-----" + bean.getAppName());
			Log.d("Notification Service", "package-----" + bean.getPackageName());
			Log.d("Notification Service", "Message-----" + bean.getMessage());
			Log.d("Notification Service", "Sender-----" + bean.getSender());
			
			if(Utils.isForgroundApp(this, getString(R.string.package_name))){
				this.sendBroadcast(new Intent(NotificationReceiver.ACTION_NOTIFICATION_CHANGED));
			}else{
				Intent dialogIntent = new Intent(getBaseContext(), NotificationActivity.class);				
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplication().startActivity(dialogIntent);
			}
			
		}
		
	}
	
	@Override
	public void onInterrupt() {


	}

	public void onDestroy(){

	}

	@Override
	protected void onServiceConnected() {
		try{			
			SharedPreferenceUtils.populateDefaultApps(this);
			Log.d("NotificationHistory", "notification service started.");
			ctx = this;	      
			utils = new Utils(ctx);

		}catch(Exception e){
			Log.e("NotificationHistory", "Failed to configure accessibility service", e);
		}

	}


}
