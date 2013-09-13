package com.bun.popupnotifications;

import java.util.HashMap;
import java.util.HashSet;




import android.net.Uri;
import android.os.Bundle;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.WallpaperManager;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class NotificationActivity extends Activity {

	NotificationsAdapter adapter;
	ListView layout;
	KeyguardManager  myKeyGuard ; 
	KeyguardLock myLock ; 
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	enum Direction {LEFT, RIGHT;}
	public Context ctx;


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
		
		ctx = this;

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
					Utils.intentMap.get(Utils.notList.get(position).getPackageName()).send();					
					Utils.notList.clear();
				} catch (CanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		
		registerForContextMenu(layout);	

	}


	private NotificationReceiver mReceiver = new NotificationReceiver() {
		public void onReceive(Context context, Intent intent) {
			populateAdapter(true);
		}

	};

	public void clearNotifications(View view){	
		clearData();

	}

	private void clearData(){
		for(NotificationBean n : Utils.notList){
			n = null;
		}		

		Utils.notList.clear();
		Utils.notList = null;
		
		Utils.intentMap.clear();

		adapter.clearNotifications();
		//Utils.tf = null;
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.notification, menu);

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
		NotificationBean n = (NotificationBean)adapter.getItem(contextMenuInfo.position);
		if(n.getAppName() != null){
			menu.setHeaderTitle("");  
			menu.add(0, v.getId(), 0, "Mute App");

		}
		//menu.add(0, v.getId(), 0, "Delete the Contact"); 
	}



	@Override  
	public boolean onContextItemSelected(MenuItem item)
	{  


		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();		 

		//  info.position will give the index of selected item
		int intIndexSelected = info.position; 


		if(item.getTitle()=="Mute App")
		{
			NotificationBean n = (NotificationBean)adapter.getItem(intIndexSelected);
			showMuteOptions(n);

		}

		return true;  


	} 

	RadioGroup radioGroup1;
	RadioButton radioButton1;
	  
	RadioGroup radioGroup2;
	RadioButton radioButton2;
	
	private void showMuteOptions(final NotificationBean n){
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.mute_app_dialogue, (ViewGroup) findViewById(R.id.cpRoot));
		
		

		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				NotificationActivity.this);

		// Setting Dialog Title
		alertDialog2.setTitle("");

		// Setting Dialog Message
		alertDialog2.setView(layout);

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog2.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				radioGroup1 = (RadioGroup) layout.findViewById(R.id.muteOptions1);
				int selectedId = radioGroup1.getCheckedRadioButtonId();				
				radioButton1 = (RadioButton) layout.findViewById(selectedId);
				
				
				radioGroup2 = (RadioGroup) layout.findViewById(R.id.muteOptions2);
				selectedId = radioGroup2.getCheckedRadioButtonId();				
				radioButton2 = (RadioButton) layout.findViewById(selectedId);
				int opt1 = R.string.mute_this_apps;
				int opt2 = R.string.mute_all_apps;				
				
				
				if(radioButton1.getText().toString().equals(getString(opt1))){
					SharedPreferenceUtils.setAllowedApps(ctx, n.getPackageName(), Utils.getMuteTime(ctx,radioButton2.getText().toString()));
					
				}else if(radioButton1.getText().toString().equals(getString(opt2))){
					SharedPreferenceUtils.setAllowedApps(ctx, "com.AA", Utils.getMuteTime(ctx,radioButton2.getText().toString()));
				} 
				
				
				
				Toast.makeText(NotificationActivity.this,
						Utils.getMuteToastText(ctx, radioButton1.getText().toString(), radioButton2.getText().toString(), n.getAppName()), Toast.LENGTH_SHORT).show();
			}
		});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				dialog.cancel();
			}
		});

		alertDialog2.show();
	}


	private void populateAdapter(Boolean clearData){
		if(clearData){
			adapter.clearNotifications();
		}

		HashSet<String> alreadyEnteredValues = new HashSet<String>();

		for(NotificationBean n : Utils.notList){
			
			//if(!alreadyEnteredValues.contains(n.getUniqueValue()))
				adapter.addNotification(n);
			//alreadyEnteredValues.add(n.getUniqueValue());
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

		//PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		//if(pm.isScreenOn()){
		//clearData();
		//}

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
