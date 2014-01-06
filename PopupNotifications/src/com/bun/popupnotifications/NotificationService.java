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
	
	private final Context context = null;
	private boolean deviceCovered = false;
	
	NotificationParser np;

	@SuppressLint("NewApi")
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		Log.d("Notification Service", "Entered the accessibility method==" + event.getPackageName().toString());
		
		
		
		Notification n = (Notification) event.getParcelableData();	
		
		np.processNotification(n, event.getPackageName().toString(), null, null);
		
		event = null;

	}



	

	@Override
	public void onInterrupt() {


	}

	public void onDestroy(){
		unregisterReceiver(mScreenReceiver);
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
			np = new NotificationParser(this, getBaseContext(), getApplication());
			np.detectNotificationIds();
			if (Build.MODEL.toLowerCase().contains("NEXUS") && SharedPreferenceUtils.getFirstTimeRun(ctx))
			{
				//registerProximitySensor();
			}

		}catch(Exception e){
			Log.e("NotificationHistory", "Failed to configure accessibility service", e);
		}

	}
	
	/*private void registerProximitySensor(){
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
			
	}*/
	
	

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
				Utils.isScreenOnFromResume = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				isScreenOn = true;
				Utils.isScreenOn = true;
				Utils.isScreenOnFromResume = false;
			}
		}

	}
	
	


}
