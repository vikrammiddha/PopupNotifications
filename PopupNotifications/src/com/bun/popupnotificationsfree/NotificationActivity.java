package com.bun.popupnotificationsfree;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;


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
import android.os.Handler;
import android.os.Vibrator;
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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
	ShowcaseView sv5;

	ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();

	NewNotificationService nns;

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
		adapter.textViewSize = Integer.valueOf(SharedPreferenceUtils.getMaxLines(ctx));
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

		layout.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					Utils.isScreenScrolling = false;
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					Utils.isScreenScrolling = true;
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					Utils.isScreenScrolling = true;
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

		Button b1 = (Button) findViewById(R.id.CloseWindowId);
		Button b2 = (Button) findViewById(R.id.CloseWindowId1);

		final Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

		if(!HelperUtils.isDisableUnlock(ctx)){
			b1.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					vibrator.vibrate(550);

					clearData(false);
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
					}else{
						if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							Utils.reenableKeyguard(ctx, false);                                        
						}
					}

					unlockLockScreen=true;
					return true;
				}
			});

			b2.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					vibrator.vibrate(550);

					clearData(true);
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
					}else{
						if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							Utils.reenableKeyguard(ctx, false);                                        
						}
					}

					unlockLockScreen=true;
					return true;
				}
			});
		}

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

				Utils.isAddedFirstItem = false;

				if(ctx.getResources().getBoolean(R.bool.is_new_service_enabled) && nns == null)
					nns = NewNotificationService.getInstance();

				Log.d("swipe", "onDismiss----------" + rowPos); 
				if(rowPos >= 0){
					try {
						if(HelperUtils.isVibrate(ctx)){
							Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
							v.vibrate(250);
						}
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
							getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
						}else{
							if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
								Utils.reenableKeyguard(ctx, false);                                        
							}
						}

						//Log.d("not activity", "intent----------" + Utils.notList.get(position).getPackageName());
						//Utils.intentMap.get(Utils.getNotList().get(rowPos).getPackageName()).send();
						Utils.intentMap.get(adapter.getItem(rowPos).getPackageName()).send();
						unlockLockScreen=true;
						Utils.getNotList().clear();
						Utils.intentMap.clear();
						adapter.removeAllNotifications();
						adapter.notifyDataSetChanged();
						if(ctx.getResources().getBoolean(R.bool.is_new_service_enabled) 
								&& (!"none".equals(SharedPreferenceUtils.getSyncType(ctx)))){
							nns.cancelNotification(adapter.getItem(rowPos).getPackageName(), adapter.getItem(rowPos).getTagId(), adapter.getItem(rowPos).getId());
						}

						//resetFeedbackCounter();
						finish();
						return;
						//Utils.notList.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(HelperUtils.isVibrate(ctx)){
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(250);
				}

				Set<String> packageSet = new HashSet<String>();

				for (int position : reverseSortedPositions) {
					Log.d("swipe", "onDismiss----------" + position);
					try{
						packageSet.add(adapter.getItem(position).getPackageName());
						Log.d("NotActivity", "Removing app ===========+" + adapter.getItem(position).getPackageName());
						if(ctx.getResources().getBoolean(R.bool.is_new_service_enabled) && (!"none".equals(SharedPreferenceUtils.getSyncType(ctx)))
								&& HelperUtils.dismissAllNotifications(adapter.getItem(position).getPackageName(), ctx)){
							nns.cancelNotification(adapter.getItem(position).getPackageName(), adapter.getItem(position).getTagId(), adapter.getItem(position).getId());
						}else{
							Utils.getNotList().remove(position);
						}
						adapter.removeNotification(position);

					}catch(Exception e){
						e.printStackTrace();
					}
				}

				if(ctx.getResources().getBoolean(R.bool.is_service_enabled) && SharedPreferenceUtils.getDismissAll(ctx)){

					Iterator<NotificationBean> iter = Utils.getNotList().iterator();

					while(iter.hasNext()){

						NotificationBean nb = iter.next();

						if(packageSet.contains(nb.getPackageName())){                                                        
							iter.remove();
						}
					}

					adapter.clearAppNotifications(packageSet);

				}

				setBackgroundHeight(true);

				if(adapter.getAdapterSize() == 0){
					//resetFeedbackCounter();
					finish();
					Utils.getNotList().clear();
					Utils.intentMap.clear();        
				}

				if(adapter != null)
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
			SharedPreferenceUtils.setShowTutorial(ctx, false);
		}

		//SharedPreferenceUtils.setNotCount(ctx, SharedPreferenceUtils.getNotCount(ctx) + 1);

		//if(HelperUtils.showFeedback(ctx)){
		//Feedback.initiateFeedback(ctx, this);
		//}		

	}



	private NotificationReceiver mReceiver = new NotificationReceiver() {
		public void onReceive(Context context, Intent intent) {
			populateAdapter(true);
		}

	};

	public void clearNotifications(View view){	
		clearData(true);
		//finish();


	}

	public void closeNotifications(View view){
		clearData(false);		
		//finish();


	}

	private void showDismissAnimation(){
		adapter.showDismissAnimation = true;                
		adapter.notifyDataSetChanged();
		//adapter.showDismissAnimation = false;
	}


	private void clearData(Boolean dismiss){

		final Boolean tempDismiss;

		if(ctx.getResources().getBoolean(R.bool.is_service_enabled)){
			dismiss = false;
		}

		tempDismiss = dismiss;

		showDismissAnimation();

		Handler handler = new Handler(); 

		final int waitTime;

		if(HelperUtils.isDisableAnimations(ctx)){
			waitTime = 1;
		}else{
			waitTime = 300;
		}
		handler.postDelayed(new Runnable() { 
			public void run() {                     


				if(tempDismiss){

					if(nns == null)
						nns = NewNotificationService.getInstance();

					ArrayList<NotificationBean> clonedList = (ArrayList<NotificationBean>)Utils.getNotList().clone();

					for(NotificationBean n : clonedList){

						if(ctx.getResources().getBoolean(R.bool.is_new_service_enabled) && (!"none".equals(SharedPreferenceUtils.getSyncType(ctx)))){
							nns.cancelNotification(n.getPackageName(), n.getTagId(), n.getId());
						}
						//n = null;
					}        

					clonedList.clear();

				}



				Utils.getNotList().clear();

				Utils.notList = null;

				Utils.intentMap.clear();

				adapter.clearNotifications();



				//Utils.tf = null;
				finish();
			} 
		}, waitTime); 



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

		Log.d("NotActivity","Entered Receiver=========" + Utils.getNotList().size());

		if(Utils.getNotList().size() != adapter.getCount()){
			Utils.isScreenOnFromResume = true;
		}

		if(clearData){
			adapter.clearNotifications();
		}        

		Iterator<NotificationBean> iter = Utils.getNotList().iterator();

		while(iter.hasNext()){

			NotificationBean nb = iter.next();

			if(nb.getIsOddRow() && !"com.whatsapp".equals(nb.getPackageName())){
				iter.remove();
			}
		}

		if(HelperUtils.isExpandedNotifications(ctx)){

			for(NotificationBean n : Utils.getNotList()){
				Log.d("NotActivity","Adapter Not=======" + n.getPackageName());
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
		if(adapter.getCount() == 0){
			finish();
		}
		if(Utils.isScreenOnFromResume){
			Utils.isAddedFirstItem = true;
			Utils.isScreenOnFromResume = false;
		}
		adapter.notifyDataSetChanged();
		layout.setAdapter(adapter);

		//LinearLayout ll = (LinearLayout) findViewById(R.id.expandingLayoutId);                        

		 setBackgroundHeight(false);

         FrameLayout ll1 = (FrameLayout) findViewById(R.id.mainRowId);

         Button dismissButton = (Button) findViewById(R.id.CloseWindowId);
         Button dismissButton1 = (Button) findViewById(R.id.CloseWindowId1);

         int fontColor = HelperUtils.getFontColor(ctx);
         if(fontColor == 0){
                 fontColor = Color.WHITE;
         }

         int bgColor = HelperUtils.getBackgroundColor(ctx);
         if(bgColor == 0){
                 bgColor = Color.BLACK;
         }

         if(HelperUtils.getBackgroundColor(ctx) != null ){
                 int strokeWidth = 5; // 3dp
                 int roundRadius = 20; // 8dp
                 int strokeColor = HelperUtils.getBorderColor(this);
                 int fillColor = bgColor;

                 GradientDrawable gd = new GradientDrawable();
                 gd.setColor(fillColor);
                 gd.setCornerRadius(roundRadius);
                 gd.setStroke(strokeWidth, strokeColor);        

                 if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                         //layout.setBackground(gd);
                 }else{
                         //layout.setBackgroundDrawable(gd);
                 }



                 int strokeWidth1 = Integer.valueOf(SharedPreferenceUtils.getBorderSize(ctx));
                 int roundRadius1 = 0; // 8dp

                 if(getString(R.string.bubbles).equals(SharedPreferenceUtils.getTheme(this))){
                         //strokeWidth1 = 5;
                         roundRadius1 = 25;
                 }

                 int strokeColor1 = HelperUtils.getBorderColor(this);
                 int fillColor1 = bgColor;

                 GradientDrawable gd1 = new GradientDrawable();
                 gd1.setColor(fillColor1);
                 gd1.setCornerRadius(roundRadius1);
                 gd1.setStroke(strokeWidth1, strokeColor1);


                 dismissButton.setBackgroundDrawable(gd1);
                 dismissButton1.setBackgroundDrawable(gd1);

                 if(HelperUtils.isTransparentBackround(ctx)){
                         //layout.getBackground().setAlpha(200);
                         dismissButton1.getBackground().setAlpha(200);
                         dismissButton.getBackground().setAlpha(200);
                 }
         }


         dismissButton.setTextColor(fontColor);
         dismissButton1.setTextColor(fontColor);

         if(ctx.getResources().getBoolean(R.bool.is_service_enabled))
                 dismissButton.setVisibility(View.GONE);

		//Utils.isAddedFirstItem = false;


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
			int totalHeight = 0;
			for (int size = 0; size < adapter.getCount(); size++) {
				View listItem = adapter.getView(size, null, layout);
				if (listItem != null) {
					listItem.measure(0, 0);
					totalHeight += listItem.getMeasuredHeight();
				}
			}

			int a1 = (int)(screenHeight * 0.5);
			Button dismissButton = (Button) findViewById(R.id.CloseWindowId);
			//Log.d("not_Activity", "ll height===" + getLayoutHeight() + "==" + (int)(screenHeight * 0.5));
			if((ll1.getHeight() >= (int)(screenHeight * 0.5) || totalHeight >= (int)(screenHeight * 0.5)) && !isDismissed){		    
				params.height = (int)(screenHeight * 0.5);
			}else{

				if(isDismissed && ((layout.getHeight() + dismissButton.getHeight()) >= (int)(screenHeight * 0.5) ||  (totalHeight + dismissButton.getHeight()) >= (int)(screenHeight * 0.5))){				
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

		Utils.isAddedFirstItem = false;

		Utils.isScreenScrolling = false;

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

		layout.setAdapter(null);


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
			sv4 = ShowcaseView.insertShowcaseView(R.id.CloseWindowId1, this, "Tutorial", getString(R.string.dismiss_all_tutorial), co);
			sv4.setOnShowcaseEventListener(this);
			if(ctx.getResources().getBoolean(R.bool.is_service_enabled)){
				sv4.animateGesture((float) (button.getRight()/2),(float) (button.getBottom()) ,(float) (button.getRight()/2), (float) (button.getBottom()));
			}else{
				sv4.animateGesture((float) (button.getRight()/4),(float) (button.getBottom()) ,(float) (button.getRight()/4), (float) (button.getBottom()));
			}


		}else if(ssv == sv4){
			if(ctx.getResources().getBoolean(R.bool.is_new_service_enabled)){
				View button = findViewById(R.id.expandingLayoutId);
				sv5 = ShowcaseView.insertShowcaseView(R.id.CloseWindowId, this, "Tutorial", getString(R.string.close_all_tutorial), co);
				sv5.setOnShowcaseEventListener(this);
				sv5.animateGesture((float) (button.getRight()*.75),(float) (button.getBottom()) ,(float) (button.getRight()*.75), (float) (button.getBottom()));
			}

		}else if(ssv == sv5){

		}
	}



	@Override
	public void onShowcaseViewShow(ShowcaseView arg0) {
		// TODO Auto-generated method stub
		Log.d("not", "onShowcaseViewShow ==============" + arg0);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
	}

	@Override
	public void onClick(View view) {
		Log.d("not", "onClick ==============");
	}


}
