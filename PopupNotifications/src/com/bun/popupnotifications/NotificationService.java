package com.bun.popupnotifications;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;




import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class NotificationService extends AccessibilityService { 

		
	private Context ctx;
	public Utils utils;
	BroadcastReceiver mScreenReceiver = new ScreenReceiver();
	Boolean isScreenOn = true;
	public int big_notification_content_text = 0;
	public int big_notification_content_title = 0;
	public int big_notification_summary_id = 0;
	public int big_notification_title_id = 0;

	public int inbox_notification_event_10_id = 0;
	public int inbox_notification_event_1_id = 0;
	public int inbox_notification_event_2_id = 0;
	public int inbox_notification_event_3_id = 0;
	public int inbox_notification_event_4_id = 0;
	public int inbox_notification_event_5_id = 0;
	public int inbox_notification_event_6_id = 0;
	public int inbox_notification_event_7_id = 0;
	public int inbox_notification_event_8_id = 0;
	public int inbox_notification_event_9_id = 0;
	public int inbox_notification_title_id = 0;
	public int notification_image_id = 0;
	public int notification_info_id = 0;
	public int notification_subtext_id = 0;
	public int notification_text_id = 0;
	public int notification_title_id = 0;
	private final Context context = null;
	private boolean deviceCovered = false;

	@SuppressLint("NewApi")
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		Log.d("Notification Service", "Entered the accessibility method==" + event.getPackageName().toString());

		if(utils.performValidation(event)){
			Notification nnn = (Notification) event.getParcelableData();


			NotificationBean bean = new NotificationBean();

			String packageName = event.getPackageName().toString();
			// extract notification & app icons
			Resources res;
			PackageInfo info;
			ApplicationInfo ai;
			try 
			{
				res = getPackageManager().getResourcesForApplication(packageName);
				info = getPackageManager().getPackageInfo(packageName,0);
				ai = getPackageManager().getApplicationInfo(packageName,0);
			}
			catch(NameNotFoundException e)
			{
				info = null;
				res = null;
				ai = null;
			}

			if (res != null && info != null)
			{
				bean.setNotIcon(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(res, nnn.icon)));
				bean.setIcon(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(res, info.applicationInfo.icon)));

				if (bean.getIcon() == null)
				{
					bean.setIcon(bean.getNotIcon());    

				}                                                       
			}                                               
			if (nnn.largeIcon != null)
			{
				bean.setIcon(new BitmapDrawable(getResources(),nnn.largeIcon));
			}


			try{
				RemoteViews rv = null;
				try{
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						rv = nnn.bigContentView != null ? nnn.bigContentView : nnn.contentView;
					}else{
						rv = nnn.contentView;
					}
					
				}catch(Exception e){
					rv = nnn.contentView;
				}
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ViewGroup localView = (ViewGroup) inflater.inflate(rv.getLayoutId(), null);
				rv.reapply(getApplicationContext(), localView);



				//recursiveDetectNotificationsIds(localView);
				extractTextFromView(rv, true, bean);

				if (bean.getMessage() == null || bean.getMessage().equals("") &&
						bean.getContent() != null && !bean.getContent().equals(""))
				{
					bean.setMessage(bean.getContent());
					bean.setContent(null);
				}

				if (bean.getMessage() == null)
				{                                                       
					bean.setMessage(nnn.tickerText.toString());                                                
				}
				if (bean.getSender() == null)
				{
					if (info != null)
						bean.setSender(getPackageManager().getApplicationLabel(ai).toString());
					else
						bean.setSender(packageName);

				}
				bean.setAppName(getPackageManager().getApplicationLabel(ai).toString());
				if(bean.getSender() != null && !"".equals(bean.getSender())){
					String tempSender = bean.getSender();
					if(tempSender.contains(bean.getAppName())){
						tempSender = null;
					}

					if(tempSender != null && (tempSender.toLowerCase().contains("new message") || tempSender.toLowerCase().contains("new email message"))){
						tempSender = null;
					}

					bean.setSender(tempSender);
				}
			}catch(Exception e){
				bean.setMessage(nnn.tickerText.toString());  
				bean.setAppName(getPackageManager().getApplicationLabel(ai).toString());
			}


			/*Log.d("Notification Service", "title  -----" + bean.getSender());
			Log.d("Notification Service", "text  -----" + bean.getMessage());
			Log.d("Notification Service", "content  -----" + bean.getContent());*/



			bean.setPackageName(event.getPackageName().toString());
			//utils.populateBeanDetails(event, bean);

			if(bean.getSender() != null &&bean.getSender().trim().equals("") && bean.getMessage().trim().equals("")){
				return;
			}

			bean.setPendingIntent(nnn.contentIntent);

			DateFormat formatter = new SimpleDateFormat("HH:mm");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			String formattedDate = formatter.format(calendar.getTime());

			bean.setNotTime(formattedDate);

			bean.setWhen(nnn.when);
						
			bean.setUniqueValue(bean.getPackageName() + bean.getSender() + bean.getMessage());
			
			if(Utils.checkForDuplicates(bean)){
				//return;
			}

			//bean.setNotCount(1);

			//bean.setTickerText(nnn.tickerText.toString());

			Utils.intentMap.put(bean.getPackageName(), nnn.contentIntent);

			Log.d("Notification Service", "App-----" + bean.getAppName());
			Log.d("Notification Service", "package-----" + bean.getPackageName());
			Log.d("Notification Service", "Message-----" + bean.getMessage());
			Log.d("Notification Service", "Sender-----" + bean.getSender());
			Log.d("Notification Service", "uniqueValue-----" + bean.getUniqueValue());
			Log.d("Notification Service", "tickertext-----" + event.getText().toString());
			
			

			if(Utils.isServiceRunning || Utils.isForgroundApp(this, getString(R.string.package_name))){
				Utils.getNotList().add(0,bean);
				Log.d("Notification Service", "Broadcast---");
				this.sendBroadcast(new Intent(NotificationReceiver.ACTION_NOTIFICATION_CHANGED));

			}else{
				if(Utils.notList != null){
					for(NotificationBean n : Utils.notList){
						n = null;
					}		

					Utils.notList.clear();
					Utils.notList = null;
				}
				Utils.getNotList().add(0,bean);
				Log.d("Notification Service", "New Intent----");
				Intent dialogIntent;
				if((Utils.isScreenLocked(ctx) && (HelperUtils.getNotType(ctx) == Constants.NOT_LOCKSCREEN 
						|| HelperUtils.getNotType(ctx) == Constants.LOCKSCREEN_POPUP || HelperUtils.getNotType(ctx) == Constants.LOCKSCREEN_BANNER)) 
						){
					dialogIntent = new Intent(getBaseContext(), NotificationActivity.class);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplication().startActivity(dialogIntent);
				}else if(!Utils.isScreenLocked(ctx) && (HelperUtils.getNotType(ctx) == Constants.NOT_POPUP  || HelperUtils.getNotType(ctx) == Constants.LOCKSCREEN_POPUP ) ){
					dialogIntent = new Intent(getBaseContext(), PopupActivity.class);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplication().startActivity(dialogIntent);
				}
				else if(HelperUtils.getNotType(ctx) == Constants.NOT_BANNERS || HelperUtils.getNotType(ctx) == Constants.LOCKSCREEN_BANNER){
					Log.d("Notification Service", "Starting new service.");
					//dialogIntent = new Intent(getBaseContext(), BannerActivity.class);
					Utils.isServiceRunning = true;
					stopService(new Intent(ctx, BannerService.class));
					startService(new Intent(ctx, BannerService.class));
					
				}				
				
			}

			if(HelperUtils.wakeOnNotification(ctx)){
				turnScreenOn();
			}

		}
		
		event = null;

	}



	private void turnScreenOn() 
	{
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// turn the screen on only if it was off
		if (!pm.isScreenOn())
		{
			@SuppressWarnings("deprecation")
			final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Notification");
			wl.acquire();   

			// release after 5 seconds
			final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
			Runnable task = new Runnable() 
			{
				public void run() 
				{
					wl.release();
				}
			};
			worker.schedule(task, 15, TimeUnit.SECONDS);
		}                     
	}


	private void recursiveDetectNotificationsIds(ViewGroup v)
	{
		for(int i=0; i<v.getChildCount(); i++)
		{
			View child = v.getChildAt(i);
			if (child instanceof ViewGroup)
				recursiveDetectNotificationsIds((ViewGroup)child);
			else if (child instanceof TextView)
			{
				String text = ((TextView)child).getText().toString();
				int id = child.getId();
				if (text.equals("1")) notification_title_id = id;
				else if (text.equals("2")) notification_text_id = id;
				else if (text.equals("3")) notification_info_id = id;
				else if (text.equals("4")) notification_subtext_id = id;
				else if (text.equals("5")) big_notification_summary_id = id;
				else if (text.equals("6")) big_notification_content_title = id;
				else if (text.equals("7")) big_notification_content_text = id;
				else if (text.equals("8")) big_notification_title_id = id;
				else if (text.equals("9")) inbox_notification_title_id = id;
				else if (text.equals("10")) inbox_notification_event_1_id = id;
				else if (text.equals("11")) inbox_notification_event_2_id = id;
				else if (text.equals("12")) inbox_notification_event_3_id = id;                         
				else if (text.equals("13")) inbox_notification_event_4_id = id;
				else if (text.equals("14")) inbox_notification_event_5_id = id;
				else if (text.equals("15")) inbox_notification_event_6_id = id;                         
				else if (text.equals("16")) inbox_notification_event_7_id = id;
				else if (text.equals("17")) inbox_notification_event_8_id = id;
				else if (text.equals("18")) inbox_notification_event_9_id = id;                         
				else if (text.equals("19")) inbox_notification_event_10_id = id;
			}
			else if (child instanceof ImageView)
			{
				Drawable d = ((ImageView)child).getDrawable();
				if (d!=null)
				{
					this.notification_image_id = child.getId();
				}
			}       
		}
	}   


	private void extractTextFromView(RemoteViews view, boolean useFirstEvent, NotificationBean bean) 
	{
		CharSequence title = null;
		CharSequence text = null;
		CharSequence content = null;
		boolean hasParsableContent = true;
		ViewGroup localView = null;
		try
		{
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			localView = (ViewGroup) inflater.inflate(view.getLayoutId(), null);
			view.reapply(getApplicationContext(), localView);
		}
		catch (Exception exp)
		{
			hasParsableContent = false;                             
		}
		if (hasParsableContent)
		{
			View v;                                         
			// try to get big text                          
			v = localView.findViewById(big_notification_content_text);
			if (v != null && v instanceof TextView)
			{
				text = ((TextView)v).getText();
			}

			// get title string if available
			View titleView = localView.findViewById(notification_title_id );
			View bigTitleView = localView.findViewById(big_notification_title_id );
			View inboxTitleView = localView.findViewById(inbox_notification_title_id );
			if (titleView  != null && titleView  instanceof TextView)
			{
				title = ((TextView)titleView).getText();
			} else if (bigTitleView != null && bigTitleView instanceof TextView)
			{
				title = ((TextView)bigTitleView).getText();
			} else if  (inboxTitleView != null && inboxTitleView instanceof TextView)
			{
				title = ((TextView)inboxTitleView).getText();
			}

			// try to extract details lines 
			content = null;
			v = localView.findViewById(inbox_notification_event_1_id);
			if (v != null && v instanceof TextView) 
			{
				CharSequence s = ((TextView)v).getText();
				if (!s.equals(""))
					content = s;
			}

			if (!useFirstEvent)
			{
				v = localView.findViewById(inbox_notification_event_2_id);
				if (v != null && v instanceof TextView) 
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_3_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_4_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_5_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_6_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_7_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_8_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_9_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}

				v = localView.findViewById(inbox_notification_event_10_id);
				if (v != null && v instanceof TextView)  
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals("")) 
						content = TextUtils.concat(content,"\n",s);
				}
			}

			// if no content lines, try to get subtext
			if (content == null)
			{
				v = localView.findViewById(notification_subtext_id);
				if (v != null && v instanceof TextView)
				{
					CharSequence s = ((TextView)v).getText();
					if (!s.equals(""))
					{
						content = s;
					}
				}
			}       
		}

		if (title!=null)
		{
			bean.setSender(title.toString());
		}
		if (text != null)
		{
			bean.setMessage(text.toString());
		}
		if (content != null)
		{
			bean.setContent(content.toString());
		}
	}


	@Override
	public void onInterrupt() {


	}

	public void onDestroy(){
		unregisterReceiver(mScreenReceiver);
	}

	private void detectNotificationIds()
	{               
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("1")
		.setContentText("2")
		.setContentInfo("3")
		.setSubText("4");

		Notification n = mBuilder.build();

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup localView;

		// detect id's from normal view
		localView = (ViewGroup) inflater.inflate(n.contentView.getLayoutId(), null);
		n.contentView.reapply(getApplicationContext(), localView);
		recursiveDetectNotificationsIds(localView);

		// detect id's from expanded views              
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) 
		{
			NotificationCompat.BigTextStyle bigtextstyle = new NotificationCompat.BigTextStyle();
			bigtextstyle.setSummaryText("5");
			bigtextstyle.setBigContentTitle("6");
			bigtextstyle.bigText("7");                                                              
			mBuilder.setContentTitle("8");
			mBuilder.setStyle(bigtextstyle);
			detectExpandedNotificationsIds(mBuilder.build());

			NotificationCompat.InboxStyle inboxStyle =
					new NotificationCompat.InboxStyle();
			String[] events = {"10","11","12","13","14","15","16","17","18","19"};
			inboxStyle.setBigContentTitle("6");
			mBuilder.setContentTitle("9");
			inboxStyle.setSummaryText("5");

			for (int i=0; i < events.length; i++) 
			{       
				inboxStyle.addLine(events[i]);
			}
			mBuilder.setStyle(inboxStyle);

			detectExpandedNotificationsIds(mBuilder.build());                       
		}
	}


	private void detectExpandedNotificationsIds(Notification n)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup localView = (ViewGroup) inflater.inflate(n.bigContentView.getLayoutId(), null);
		n.bigContentView.reapply(getApplicationContext(), localView);
		recursiveDetectNotificationsIds(localView);
	}
	
	SensorManager sensorManager = null;
	Sensor proximitySensor = null;
	SensorEventListener sensorListener = null;

	@Override
	protected void onServiceConnected() {
		try{			
			SharedPreferenceUtils.loadDefaultSettings(this);
			Log.d("NotificationHistory", "notification service started.");
			ctx = this;	      
			utils = new Utils(ctx);
			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			mScreenReceiver = new ScreenReceiver();
			registerReceiver(mScreenReceiver, filter);
			detectNotificationIds();
			if (Build.MODEL.toLowerCase().contains("NEXUS") && SharedPreferenceUtils.getFirstTimeRun(ctx))
			{
				//registerProximitySensor();
			}

		}catch(Exception e){
			Log.e("NotificationHistory", "Failed to configure accessibility service", e);
		}

	}
	
	private void registerProximitySensor(){
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorListener = new SensorEventListener()
		{
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) 
			{
			}

			@Override
			public void onSensorChanged(SensorEvent event) 
			{
				if (event.values[0] == 0)
				{
					deviceCovered = true;
				}
				else
				{
					if (deviceCovered)
					{
						deviceCovered = false;
						SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(NotificationService.this);
						if(HelperUtils.wakeOnNotification(ctx)){
							turnScreenOn();
						}
					}
				}
			}				
		};
		startProximityMontior();
	}
	
	public void startProximityMontior()
	{	
		if (proximitySensor != null)
		{
			sensorManager.registerListener(sensorListener, proximitySensor, SensorManager.SENSOR_DELAY_UI);
		}
		else registerProximitySensor();
			
	}
	
	

	public class ScreenReceiver extends BroadcastReceiver {



		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				isScreenOn = false;
				Utils.isScreenOn = false;
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					Utils.reenableKeyguard(ctx, true);					
				}
				stopService(new Intent(ctx, BannerService.class));
				Utils.isServiceRunning = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				isScreenOn = true;
				Utils.isScreenOn = true;
			}
		}

	}
	
	


}
