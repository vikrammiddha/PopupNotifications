package com.bun.popupnotifications;

import android.os.Bundle;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.WallpaperManager;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;


public class NotificationActivity extends Activity {

	NotificationsAdapter adapter;
	ListView layout;
	KeyguardManager  myKeyGuard ; 
	KeyguardLock myLock ; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Debug.startMethodTracing("popup");
		Window window = getWindow();
		super.onCreate(savedInstanceState);	
		
		myKeyGuard = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
				
		myLock = myKeyGuard.newKeyguardLock(KEYGUARD_SERVICE);

		window.addFlags(//WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON 
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				); 

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.notification_main);
		
		//Utils.tf = Typeface.createFromAsset(this.getAssets(),"fonts/robotomedium.ttf");

		adapter = new NotificationsAdapter(this);
		layout = (ListView) findViewById(R.id.notificationsListViewId);	
		layout.setScrollingCacheEnabled(false);
		//layout.setBackgroundColor(Color.TRANSPARENT);
		populateAdapter(true);
		setLayoutBackground();

		layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				try {					
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
					Utils.notList.get(position).getPendingIntent().send();
					Utils.notList.clear();
				} catch (CanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}


	private NotificationReceiver mReceiver = new NotificationReceiver() {
		public void onReceive(Context context, Intent intent) {
			populateAdapter(true);
		}

	};

	public void clearNotifications(View view){		
		Utils.notList.clear();
		adapter.clearNotifications();
		//Utils.tf = null;
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

		//for (int i = Utils.notList.size()-1; i >=0; i--) {
		//adapter.addNotification(Utils.notList.get(i));
		//}
		for(NotificationBean n : Utils.notList){
			adapter.addNotification(n);
		}

		adapter.notifyDataSetChanged();
		layout.setAdapter(adapter);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.expandingLayoutId);	
		
		LayoutParams params = ll.getLayoutParams();
				
		// Changes the height and width to the specified *pixels*
		if(ll.getHeight() > 300){
			params.height = 600;
		}else{
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		}
		
		params.width = LinearLayout.LayoutParams.FILL_PARENT;
		
		ll.setLayoutParams(params);
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
		LinearLayout ll = (LinearLayout) findViewById(R.id.mainLayoutId);	
		ll.setBackgroundColor(Color.parseColor("#90FFFFFF"));

	}

}
