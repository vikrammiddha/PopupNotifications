package com.bun.popupnotifications;

import java.util.Iterator;

import com.bun.popupnotifications.NotificationService.ScreenReceiver;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@TargetApi(18)
public class NewNotificationService extends NotificationListenerService {
	
	Context ctx;
	NotificationParser np;
	BroadcastReceiver mScreenReceiver = new ScreenReceiver();
	Boolean isScreenOn = true;
	private static NewNotificationService thisObj;
	

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		// TODO Auto-generated method stub
		//Log.d("NotificationListener", "Entered onNotificationPosted");
		//Log.d("NotificationListener", "tickertext-----" + sbn.getNotification().tickerText.toString());
		try{
			HelperUtils.writeLogs("Journey started for Popup notifications --" + sbn.getNotification().tickerText.toString(), ctx, true);
		}catch(Exception e){
			
		}
		HelperUtils.writeLogs("Entered the onNotificationPosted method--" + sbn.getPackageName(), ctx, true);
		np.processNotification(sbn.getNotification(), sbn.getPackageName(), sbn.getId(), sbn.getTag());
		
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		// TODO Auto-generated method stub
		Log.d("NotificationListener", "Entered onNotificationRemoved");
		
		if("two_way".equals(SharedPreferenceUtils.getSyncType(ctx))){
		
			Iterator<NotificationBean> iter = Utils.getNotList().iterator();
			
			while(iter.hasNext()){
				
				NotificationBean nb = iter.next();
				Log.d("NotListener", "checking app ============ +" + nb.getPackageName());
				if(nb.getId() == sbn.getId() && nb.getPackageName().equals(sbn.getPackageName())){
					Log.d("NotListener", "Removing app============ +" + nb.getPackageName());
					iter.remove();
				}
			}
			
			ctx.sendBroadcast(new Intent(NotificationReceiver.ACTION_NOTIFICATION_CHANGED));
		}
		
	}
	
	@Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("NotificationListener", "Entered onCreate");
        try{			
			SharedPreferenceUtils.loadDefaultSettings(this);
			Log.d("NotificationHistory", "notification service started.");
			ctx = this;		
			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			mScreenReceiver = new ScreenReceiver();
			registerReceiver(mScreenReceiver, filter);
			np = new NotificationParser(getApplicationContext(), getBaseContext(), getApplication());
			np.detectNotificationIds();
			thisObj = this;
			if (Build.MODEL.toLowerCase().contains("NEXUS") && SharedPreferenceUtils.getFirstTimeRun(ctx))
			{
				//registerProximitySensor();
			}

		}catch(Exception e){
			Log.e("NotificationHistory", "Failed to configure accessibility service", e);
		}
    }
	
	public static NewNotificationService getInstance(){
		return thisObj;
		
		
	}

    @Override
    public void onDestroy()
    {        
        super.onDestroy();
        Log.d("NotificationListener", "Entered onDestroy");
        unregisterReceiver(mScreenReceiver);
        thisObj=null;
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
