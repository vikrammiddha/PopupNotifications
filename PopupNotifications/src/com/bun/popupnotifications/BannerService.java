package com.bun.popupnotifications;

import java.util.HashSet;
import java.util.LinkedHashMap;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

@SuppressLint("NewApi")
public class BannerService extends Service{

	WindowManager.LayoutParams prm;
	NotificationsAdapter adapter;
	//SwipeListView  layout;
	LinearLayout  layout;
	Context ctx;
	ImageView chatHead;
	SwipeListView sListView;
	private WindowManager windowManager;
	int rowPos = -1;
	CountDownTimer cTimer;
	Handler mHandler=new Handler();
	BaseSwipeListViewListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		adapter = new NotificationsAdapter(this);

		adapter.textViewSize = 2;

		ctx = this;

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		LayoutInflater li = LayoutInflater.from(this);

		layout = (LinearLayout)li.inflate(R.layout.banners, null);

		sListView = new SwipeListView(this, R.id.back, R.id.front);

		sListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
		sListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
		sListView.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
		//newView.setsw

		layout.addView(sListView);



		Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.slidein_top);
		sListView.setAnimation(animation);


		IntentFilter intentFilter = new IntentFilter(NotificationReceiver.ACTION_NOTIFICATION_CHANGED);
		registerReceiver(mReceiver, intentFilter);

		//chatHead = new ImageView(this);
		//chatHead.setImageDrawable(Utils.getNotList().get(0).getIcon());

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_FULLSCREEN |
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,	        
				PixelFormat.TRANSLUCENT);



		//params.verticalMargin = ;
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 0;

		populateAdapter(true);
		
		listener = new BaseSwipeListViewListener() {

			@Override
			public void onOpened(int position, boolean toRight) {
				Log.d("swipe", "onOpened----------" + toRight);

			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				Log.d("swipe", "onClosed----------" + fromRight);
			}

			@Override
			public void onListChanged() {
				Log.d("swipe", "onListChanged----------");
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
				try {
					Utils.intentMap.get(adapter.getItem(position).getPackageName()).send();
				} catch (CanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Utils.getNotList().clear();
				Utils.intentMap.clear();
				adapter.removeAllNotifications();
				adapter.notifyDataSetChanged();
				sListView.setPadding(0,0,0,0);				
				stopSelf();
				Log.d("swipe", "onClickFrontView----------");				
			}

			@Override
			public void onClickBackView(int position) {
				Log.d("swipe", "onClickBackView----------");
				//isItemClicked = true;
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {	
				Log.d("swipe", "onDismiss----------" + rowPos); 
				if(rowPos >= 0){
					try {
						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(250);
						//Log.d("not activity", "intent----------" + Utils.notList.get(position).getPackageName());
						//Utils.intentMap.get(Utils.getNotList().get(rowPos).getPackageName()).send();
						Utils.intentMap.get(adapter.getItem(rowPos).getPackageName()).send();

						Utils.getNotList().clear();
						Utils.intentMap.clear();
						adapter.removeAllNotifications();
						adapter.notifyDataSetChanged();

						sListView.setPadding(0,0,0,0);						
						stopSelf();
						return;
						//Utils.notList.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(250);
				for (int position : reverseSortedPositions) {
					Log.d("swipe", "onDismiss----------" + position);
					try{
						adapter.removeNotification(position);
						Utils.getNotList().remove(position);
					}catch(Exception e){

					}
				}



				if(adapter.getAdapterSize() == 0){

					Utils.getNotList().clear();
					Utils.intentMap.clear();	
				}
				adapter.notifyDataSetChanged();

				if(adapter.getAdapterSize() >0 && adapter.getAdapterSize() <=3){

					View item = adapter.getView(0, null, sListView);
					item.measure(0, 0);         
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					sListView.setLayoutParams(params);
				}

				sListView.setPadding(0, 0, 0, 0);
				cTimer.cancel();
				stopSelf();
			}

		};

		sListView.setSwipeListViewListener(listener);	



		windowManager.addView(layout, params);


		createTimer();
		cTimer.start();

	}

	private void createTimer(){
		cTimer = new CountDownTimer(5000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				/*if(!Utils.isServiceRunning){

					Utils.getNotList().clear();
					Utils.intentMap.clear();
					adapter.removeAllNotifications();
					adapter.notifyDataSetChanged();
					sListView.setPadding(0, 0, 0, 0);
					cTimer.cancel();

				}*/


				// TODO Auto-generated method stub

			}

			@Override
			public void onFinish() {

				//Utils.isServiceRunning = false;


				// TODO Auto-generated method stub



				stopSelf();
				//stopForeground(false);


			}
		};
	}

	private void populateAdapter(Boolean clearData){
		if(clearData){
			adapter.clearNotifications();
		}

		HashSet<String> alreadyEnteredValues = new HashSet<String>();

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
		/*

		for(NotificationBean n : Utils.getNotList()){
			if(n.getIsOddRow())
				continue;
			adapter.addNotification(n);

		}

		 */
		adapter.notifyDataSetChanged();

		sListView.setAdapter(adapter);

		sListView.setPadding(10, 10, 10, 10);

		if(adapter.getCount() > 3){
			View item = adapter.getView(0, null, sListView);
			item.measure(0, 0);         
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));			
			sListView.setLayoutParams(params);
		}



		//LinearLayout ll = (LinearLayout) findViewById(R.id.expandingLayoutId);			

		//setBackgroundHeight(false);

		//SwipeListView ll1 = (SwipeListView) findViewById(R.id.bannerListViewId);		


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
			//gd.setCornerRadius(roundRadius);
			//gd.setStroke(strokeWidth, strokeColor);	

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				sListView.setBackground(gd);
			}else{
				sListView.setBackgroundDrawable(gd);
			}


			if(HelperUtils.isTransparentBackround(ctx)){
				sListView.getBackground().setAlpha(500);

			}
		}

		if(cTimer != null){
			cTimer.cancel();
			createTimer();
			cTimer.start();
		}

	}

	private NotificationReceiver mReceiver = new NotificationReceiver() {
		public void onReceive(Context context, Intent intent) {
			populateAdapter(true);
		}

	};

	@Override
	public void onDestroy() {

		// TODO Auto-generated method stub
		super.onDestroy();

		unregisterReceiver(mReceiver);

		try{
			Animation animation   =    AnimationUtils.loadAnimation(ctx, R.anim.slidein_bottom);
			//animation.setDuration(5000);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					Log.d("test", "Animation start=========");

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					Log.d("test", "Animation repeat=========");
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					Log.d("test", "Animation ending=========");
					//sListView.clearAnimation();


				}
			});
			sListView.startAnimation(animation);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					cleanMemory();
					
				}
			}, 500);
			
		}catch(Exception e){

		}		


		//cTimer.cancel();

		

	}
	
	private void cleanMemory(){
		Log.d("Banner Service", "Clearing memory=======");
		listener = null;
		Utils.getNotList().clear();
		Utils.intentMap.clear();					
		adapter.removeAllNotifications();
		adapter.notifyDataSetChanged();
		sListView.setPadding(0, 0, 0, 0);
		cTimer.cancel();
		Utils.isServiceRunning = false;		
		layout.removeAllViews();	
		
		sListView.setAdapter(null);
		sListView.setSwipeListViewListener(null);
		sListView.clearAnimation();
		
		sListView = null;
		ctx = null;
		
		adapter = null;
		layout.removeAllViews();
		layout = null;
		
	}


}
