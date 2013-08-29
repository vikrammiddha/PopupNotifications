package com.bun.popupnotifications;



import android.os.Bundle;
import android.os.Debug;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;


public class NotificationActivity extends Activity {

	NotificationsAdapter adapter;
	ListView layout;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Debug.startMethodTracing("popup");
		Window window = getWindow();
		super.onCreate(savedInstanceState);		

		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				+ WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				); 

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.notification_main);

		adapter = new NotificationsAdapter(this);
		layout = (ListView) findViewById(R.id.notificationsListViewId);	
		layout.setScrollingCacheEnabled(false);
		//layout.setBackgroundColor(Color.TRANSPARENT);
		populateAdapter(true);
		setLayoutBackground();
		
	}

	private NotificationReceiver mReceiver = new NotificationReceiver() {
		public void onReceive(Context context, Intent intent) {
			populateAdapter(true);
		}

	};

	public void clearNotifications(View view){
		Utils.notList.clear();
		finish();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification, menu);

		return true;
	}

	private void populateAdapter(Boolean clearData){
		if(clearData){
			adapter.clearNotifications();
		}
		
		for (int i = Utils.notList.size()-1; i >=0; i--) {
			adapter.addNotification(Utils.notList.get(i));
		}
		//for(NotificationBean n : Utils.notList){
			// 
		//}

		adapter.notifyDataSetChanged();
		layout.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//Utils.notList.clear();
		unregisterReceiver(mReceiver);
		
		//Utils.notList.clear();
		//Debug.stopMethodTracing();
		//finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		IntentFilter intentFilter = new IntentFilter(NotificationReceiver.ACTION_NOTIFICATION_CHANGED);
		registerReceiver(mReceiver, intentFilter);

		populateAdapter(true);

	}

	private void setLayoutBackground(){
		WallpaperManager wallpaperManager1 = WallpaperManager
				.getInstance(getApplicationContext());
		final Drawable wallpaperDrawable1 = wallpaperManager1.peekDrawable();
		
		KeyguardManager kgMgr = 
			    (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
			boolean showing = kgMgr.inKeyguardRestrictedInputMode();
		
		if (wallpaperDrawable1!=null && showing)
		{                       
			getWindow().setBackgroundDrawable(wallpaperDrawable1);

		}else if(!showing){
			LinearLayout ll = (LinearLayout) findViewById(R.id.mainLayoutId);	
			ll.setBackgroundColor(Color.TRANSPARENT);
		}
		
	}

}
