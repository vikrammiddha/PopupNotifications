package com.bun.popupnotificationsfree;


import java.util.HashSet;
import java.util.LinkedHashMap;


import com.bun.popupnotificationsfree.R;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseView.OnShowcaseEventListener;

//import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
//import com.fortysevendeg.android.swipelistview.SwipeListView;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.app.PendingIntent.CanceledException;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class NotificationActivity extends Activity  implements View.OnClickListener,
ShowcaseView.OnShowcaseEventListener{

	NotificationsAdapter adapter;
	SwipeListView  layout;
	KeyguardManager  myKeyGuard ; 
	KeyguardLock myLock ; 
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	enum Direction {LEFT, RIGHT;}
	public Context ctx;
	Window window;

	public Boolean unlockLockScreen = false;

	int screenWidth;
	int screenHeight;

	int rowPos = -1;

	Activity act;

	Boolean isItemClicked = false;

	ShowcaseView sv;
	ShowcaseView sv1;
	ShowcaseView sv2;
	ShowcaseView sv3;
	ShowcaseView sv4;

	ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Debug.startMethodTracing("popup");
		window = getWindow();
		super.onCreate(savedInstanceState);	

		myKeyGuard = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);

		myLock = myKeyGuard.newKeyguardLock(KEYGUARD_SERVICE);

		window.addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				); 

		Display display = getWindowManager().getDefaultDisplay(); 
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();



		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.notification_main);

		ctx = this;

		act = this;


		adapter = new NotificationsAdapter(this);
		layout = (SwipeListView ) findViewById(R.id.notificationsListViewId);	
		layout.setScrollingCacheEnabled(false);
		
		populateAdapter(true);
		setLayoutBackground();

		registerForContextMenu(layout);	

		layout.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {              
				isItemClicked = true;

			} 
		});

		layout.setSwipeListViewListener(new BaseSwipeListViewListener() {

			@Override
			public void onOpened(int position, boolean toRight) {
				//Log.d("swipe", "onOpened----------" + toRight);

			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				//Log.d("swipe", "onClosed----------" + fromRight);
			}

			@Override
			public void onListChanged() {
				//Log.d("swipe", "onListChanged----------");
			}

			@Override
			public void onMove(int position, float x) {
				//Log.d("swipe", "onMove----------" + x);
				//if((screenWidth*.40 < x)){

				//}

			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				if(right){
					rowPos = position;
				}else{
					rowPos = -1;
				}
				//Log.d("swipe", "onStartOpen----------" + right + "===" + action);
			}

			@Override
			public void onStartClose(int position, boolean right) {
				rowPos = -1;
				//Log.d("swipe", "onStartClose----------");
			}

			@Override
			public void onClickFrontView(int position) {
				isItemClicked = true;
				//Log.d("swipe", "onClickFrontView----------");				
			}

			@Override
			public void onClickBackView(int position) {
				Log.d("swipe", "onClickBackView----------");
				isItemClicked = true;
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {	
				Log.d("swipe", "onDismiss----------" + rowPos); 
				PendingIntent p = null;
				if(rowPos >= 0){
					try {
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
							getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
						}else{
							if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
								Utils.reenableKeyguard(ctx, false);					
							}
						}

						//Log.d("not activity", "intent----------" + Utils.notList.get(position).getPackageName());
						//Utils.intentMap.get(Utils.getNotList().get(rowPos).getPackageName()).send();
						p=Utils.intentMap.get(adapter.getItem(rowPos).getPackageName());
						p.send();
						unlockLockScreen=true;
						Utils.getNotList().clear();
						Utils.intentMap.clear();
						adapter.removeAllNotifications();
						adapter.notifyDataSetChanged();
						finish();
						
						return;
						//Utils.notList.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for (int position : reverseSortedPositions) {
					Log.d("swipe", "onDismiss----------" + position);
					adapter.removeNotification(position);
					Utils.getNotList().remove(position);
				}
				
				setBackgroundHeight(true);

				if(adapter.getAdapterSize() == 0){
					finish();
					Utils.getNotList().clear();
					Utils.intentMap.clear();	
				}
				adapter.notifyDataSetChanged();
			}

		});

		if(!SharedPreferenceUtils.getFirstTimeRun(ctx)){
			co.hideOnClickOutside = false;
			co.shotType = ShowcaseView.TYPE_ONE_SHOT;
			//co.shotType = ShowcaseView.TYPE_ONE_SHOT;
			sv = ShowcaseView.insertShowcaseView(R.id.notificationsListViewId, this, "Tutorial", "Click on Next button to start Tutorial.", co);
			sv.setShowcaseIndicatorScale(1.5f);
			sv.setOnShowcaseEventListener(this);
		}	

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
		for(NotificationBean n : Utils.getNotList()){
			n = null;
		}		

		Utils.getNotList().clear();
		Utils.notList = null;

		Utils.intentMap.clear();

		adapter.clearNotifications();
		//Utils.tf = null;
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub

		if(isItemClicked){
			isItemClicked = false;
			return;
		}
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
		NotificationBean n = (NotificationBean)adapter.getItem(contextMenuInfo.position);
		if(n.getAppName() != null){
			menu.setHeaderTitle("");  
			menu.add(0, v.getId(), 0, getString(R.string.mute_app));

		}
		//menu.add(0, v.getId(), 0, "Delete the Contact"); 
	}



	@Override  
	public boolean onContextItemSelected(MenuItem item)
	{  


		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();		 

		//  info.position will give the index of selected item
		int intIndexSelected = info.position; 


		if(item.getTitle().equals(ctx.getString(R.string.mute_app)))
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
		alertDialog2.setPositiveButton(ctx.getString(R.string.upgrade),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				HelperUtils.upgradeNowDialogue(ctx);
			}
		});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton(ctx.getString(R.string.cancel),
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

		if(HelperUtils.isExpandedNotifications(ctx)){

			for(NotificationBean n : Utils.getNotList()){
				if(n.getIsOddRow())
					continue;
				adapter.addNotification(n);

			}
		}else{
			LinkedHashMap<String,NotificationBean> lhm = new LinkedHashMap<String,NotificationBean>();
			for(NotificationBean n : Utils.getNotList()){
				if(n.getIsOddRow())
					continue;
				if(lhm.get(n.getPackageName()) == null){
					n.setNotCount(1);
					lhm.put(n.getPackageName(), n);
				}else{
					lhm.get(n.getPackageName()).setNotCount(lhm.get(n.getPackageName()).getNotCount() + 1);
				}
			}

			for(NotificationBean nb : lhm.values()){
				adapter.addNotification(nb);
			}
		}
		adapter.notifyDataSetChanged();
		layout.setAdapter(adapter);

		//LinearLayout ll = (LinearLayout) findViewById(R.id.expandingLayoutId);			

		setBackgroundHeight(false);

		LinearLayout ll1 = (LinearLayout) findViewById(R.id.expandingLayoutId1);
		Button dismissButton = (Button)findViewById(R.id.CloseWindowId);

		//Button dismissButton = (Button) findViewById(R.id.CloseWindowId);
		int fontColor = HelperUtils.getFontColor(ctx);
		if(fontColor == 0){
			fontColor = Color.WHITE;
		}

		int bgColor = HelperUtils.getBackgroundColor(ctx);
		if(bgColor == 0){
			bgColor = Color.BLACK;
		}

		if(HelperUtils.getBackgroundColor(ctx) != null ){
			int strokeWidth = 3; // 3dp
			int roundRadius = 10; // 8dp
			int strokeColor = Color.parseColor("#B1BCBE");
			int fillColor = bgColor;

			GradientDrawable gd = new GradientDrawable();
			gd.setColor(fillColor);
			gd.setCornerRadius(roundRadius);
			gd.setStroke(strokeWidth, strokeColor);	

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				ll1.setBackground(gd);
			}else{
				ll1.setBackgroundDrawable(gd);
			}



			int strokeWidth1 = 3; // 3dp
			int roundRadius1 = 10; // 8dp
			int strokeColor1 = Color.parseColor("#B1BCBE");
			int fillColor1 = bgColor;

			GradientDrawable gd1 = new GradientDrawable();
			gd1.setColor(fillColor1);
			gd1.setCornerRadius(roundRadius1);
			gd1.setStroke(strokeWidth1, strokeColor1);	
			dismissButton.setBackgroundDrawable(gd1);
			
			if(HelperUtils.isTransparentBackround(ctx)){
				ll1.getBackground().setAlpha(200);
				dismissButton.getBackground().setAlpha(200);
			}
		}


		dismissButton.setTextColor(fontColor);
		
	}
	
	int notBackHeight = 0;

	private void setBackgroundHeight(Boolean isDismissed){
		LinearLayout ll1 = (LinearLayout) findViewById(R.id.expandingLayoutId1);
		
		
		LayoutParams params = ll1.getLayoutParams();

		// Changes the height and width to the specified *pixels*

		if(HelperUtils.isFullScreenNotifications(ctx)){
			Display display = getWindowManager().getDefaultDisplay(); 

			params.height = (int)(screenHeight * 0.85);
			params.width = (int)(screenWidth * 0.95);			
			ll1.setLayoutParams(params);
		}else
		{
			//Log.d("not_Activity", "ll height===" + getLayoutHeight() + "==" + (int)(screenHeight * 0.5));
			if(ll1.getHeight() >= (int)(screenHeight * 0.5) && !isDismissed){
				params.height = (int)(screenHeight * 0.5);
			}else{
				if(isDismissed && ll1.getHeight() >= (int)(screenHeight * 0.5)){
					params.height = (int)(screenHeight * 0.5);
				}else{
					params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				}
			}

			params.width = LinearLayout.LayoutParams.FILL_PARENT;
		}

		ll1.setLayoutParams(params);
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		unregisterReceiver(mReceiver);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		//Utils.notCounter = 1;

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN && unlockLockScreen == false) {

			KeyguardManager keyguardManager = (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE); 
			KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock"); 	        

			keyguardLock = null;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		IntentFilter intentFilter = new IntentFilter(NotificationReceiver.ACTION_NOTIFICATION_CHANGED);
		registerReceiver(mReceiver, intentFilter);

		populateAdapter(true);	
		unlockLockScreen = false;

	}

	private void setLayoutBackground(){


	}
	
	

	@Override
	public void onShowcaseViewHide(ShowcaseView ssv) {
		//Log.d("not", "onShowcaseViewHide ==============" + ssv.getScrollX() + "====" + ssv.getScrollY());
		SharedPreferenceUtils.setFirstTimeRun(ctx, true);
		if(ssv == sv){
			sv1 = ShowcaseView.insertShowcaseView(R.id.notificationsListViewId, this, "Tutorial", getString(R.string.right_swipe), co);
			sv1.setOnShowcaseEventListener(this);
			sv1.setShowcaseIndicatorScale(1.5f);
			sv1.animateGesture(0,(float) (sv.getBottom()/2) ,(float) (layout.getRight()), (float) (sv.getBottom()/2));
		}else if(ssv == sv1){
			sv2 = ShowcaseView.insertShowcaseView(R.id.notificationsListViewId, this, "Tutorial", getString(R.string.left_swipe), co);
			sv2.setOnShowcaseEventListener(this);
			sv2.setShowcaseIndicatorScale(1.5f);
			sv2.animateGesture((float) (layout.getRight()),(float) (sv.getBottom()/2) ,0, (float) (sv.getBottom()/2));
		}else if(ssv == sv2){
			sv3 = ShowcaseView.insertShowcaseView(R.id.notificationsListViewId, this, "Tutorial", getString(R.string.long_press), co);
			sv3.setShowcaseIndicatorScale(1.5f);
			sv3.setOnShowcaseEventListener(this);			
			sv3.animateGesture((float) (sv2.getRight()/2),(float) (sv2.getBottom()/2) ,(float) (sv2.getRight()/2), (float) (sv2.getBottom()/2));
		}else if(ssv == sv3){		
			View button = findViewById(R.id.expandingLayoutId);
			sv4 = ShowcaseView.insertShowcaseView(R.id.CloseWindowId, this, "Tutorial", getString(R.string.dismiss_all_tutorial), co);
			sv4.setOnShowcaseEventListener(this);

			sv4.animateGesture((float) (button.getRight()/2),(float) (button.getBottom()) ,(float) (button.getRight()/2), (float) (button.getBottom()));

		}else if(ssv == sv4){

		}
	}


	@Override
	public void onShowcaseViewShow(ShowcaseView arg0) {
		// TODO Auto-generated method stub
		Log.d("not", "onShowcaseViewShow ==============" + arg0);

	}

	@Override
	public void onClick(View view) {
		Log.d("not", "onClick ==============");
	}
	
	
}
