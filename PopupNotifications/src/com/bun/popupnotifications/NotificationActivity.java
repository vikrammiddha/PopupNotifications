package com.bun.popupnotifications;



import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;


public class NotificationActivity extends Activity {

	NotificationsAdapter adapter;
	ListView layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window window = getWindow();
		super.onCreate(savedInstanceState);		

		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				+ WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				); 

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.notification_main);

		adapter = new NotificationsAdapter(this);
		layout = (ListView) findViewById(R.id.notificationsListViewId);	
		//layout.setBackgroundColor(Color.TRANSPARENT);
		populateAdapter();


	}

	private NotificationReceiver mReceiver = new NotificationReceiver() {
		public void onReceive(Context context, Intent intent) {
			adapter.addNotification(Utils.notList.get(0));
			adapter.notifyDataSetChanged();
			layout.setAdapter(adapter);		  

		}

	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification, menu);

		return true;
	}

	private void populateAdapter(){
		for(NotificationBean n : Utils.notList){
			adapter.addNotification(n);
		}

		adapter.notifyDataSetChanged();
		layout.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Utils.notList.clear();
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter(NotificationReceiver.ACTION_NOTIFICATION_CHANGED);
        registerReceiver(mReceiver, intentFilter);

	}

}
