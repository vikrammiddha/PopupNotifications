package com.bun.popupnotificationsfree;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

@SuppressLint("NewApi")
public class NotificationParser {

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

	Context ctx;
	Context baseContext;
	Application app;

	public NotificationParser(Context ctx,Context baseContext, Application app){
		this.ctx = ctx;
		this.baseContext = baseContext;
		this.app = app;
	}

	public void turnScreenOn() 
	{
		PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
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


	public void recursiveDetectNotificationsIds(ViewGroup v)
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


	public void extractTextFromView(RemoteViews view, boolean useFirstEvent, NotificationBean bean) 
	{
		CharSequence title = null;
		CharSequence text = null;
		CharSequence content = null;
		boolean hasParsableContent = true;
		ViewGroup localView = null;
		try
		{
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			localView = (ViewGroup) inflater.inflate(view.getLayoutId(), null);
			view.reapply(ctx.getApplicationContext(), localView);
		}
		catch (Exception exp)
		{
			hasParsableContent = false;                             
		}

		HashMap<Integer, CharSequence> notificationStrings;
		if (hasParsableContent)
			notificationStrings = getNotificationStringsFromView(localView);
		else
			notificationStrings = getNotificationStringFromRemoteViews(view);


		if (notificationStrings.size() > 0)
		{
			View v;                                         
			// try to get big text                          
			if (notificationStrings.containsKey(big_notification_content_text))
			{
				text = notificationStrings.get(big_notification_content_text);
			}

			// get title string if available
			if (notificationStrings.containsKey(notification_title_id))
			{
				title = notificationStrings.get(notification_title_id);
			} else if (notificationStrings.containsKey(big_notification_title_id))
			{
				title = notificationStrings.get(big_notification_title_id);
			} else if (notificationStrings.containsKey(inbox_notification_title_id))
			{
				title =  notificationStrings.get(inbox_notification_title_id);
			}

			// try to extract details lines 
			content = null;
			if (notificationStrings.containsKey(inbox_notification_event_1_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_1_id);
				if (!s.equals(""))
				{
					//firstEventStr = s;
					content = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_2_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_2_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content, "\n", s);
					//lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_3_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_3_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					// lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_4_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_4_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					//lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_5_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_5_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					//lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_6_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_6_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					//lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_7_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_7_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					//lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_8_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_8_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					//lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_9_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_9_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					//lastEventStr = s;
				}
			}

			if (notificationStrings.containsKey(inbox_notification_event_10_id))
			{
				CharSequence s = notificationStrings.get(inbox_notification_event_10_id);
				if (!s.equals(""))
				{
					//content = TextUtils.concat(content,"\n",s);
					//lastEventStr = s;
				}
			}

			//if (multipleEventsHandling.equals("first")) content = firstEventStr;
			//else if (multipleEventsHandling.equals("last")) content = lastEventStr;

			// if there is no text - make the text to be the content
			if (text == null || text.equals(""))
			{
				text = content;
				content = null;
			}

			// if no content lines, try to get subtext
			if (content == null)
            {
                if (notificationStrings.containsKey(notification_subtext_id))
                {
                    CharSequence s = notificationStrings.get(notification_subtext_id);

                    if (!s.equals(""))
                    {
                        content = s;
                    }
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

	public void detectNotificationIds()
	{               
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("1")
		.setContentText("2")
		.setContentInfo("3")
		.setSubText("4");

		Notification n = mBuilder.build();

		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup localView;

		// detect id's from normal view
		localView = (ViewGroup) inflater.inflate(n.contentView.getLayoutId(), null);
		n.contentView.reapply(ctx.getApplicationContext(), localView);
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


	public void detectExpandedNotificationsIds(Notification n)
	{
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup localView = (ViewGroup) inflater.inflate(n.bigContentView.getLayoutId(), null);
		n.bigContentView.reapply(ctx.getApplicationContext(), localView);
		recursiveDetectNotificationsIds(localView);
	}

	public void processNotification(Notification nnn, String packageName, Integer id, String tagId){
		Utils utils = new Utils(ctx);
		HelperUtils.writeLogs("Entered the parseNotification method--" + packageName, ctx, true);
		if(utils.performValidation(nnn , packageName)){
			//Notification nnn = (Notification) event.getParcelableData();
			HelperUtils.writeLogs("Validations cleared : --" + packageName, ctx, true);

			NotificationBean bean = new NotificationBean();


			// extract notification & app icons
			Resources res;
			PackageInfo info;
			ApplicationInfo ai;
			try 
			{
				res = ctx.getPackageManager().getResourcesForApplication(packageName);
				info = ctx.getPackageManager().getPackageInfo(packageName,0);
				ai = ctx.getPackageManager().getApplicationInfo(packageName,0);
			}
			catch(NameNotFoundException e)
			{
				info = null;
				res = null;
				ai = null;
			}

			if (res != null && info != null)
			{
				bean.setNotIcon(new BitmapDrawable(ctx.getResources(),BitmapFactory.decodeResource(res, nnn.icon)));
				bean.setIcon(new BitmapDrawable(ctx.getResources(),BitmapFactory.decodeResource(res, info.applicationInfo.icon)));

				if (bean.getIcon() == null)
				{
					bean.setIcon(bean.getNotIcon());    

				}                                                       
			}                                               
			if (nnn.largeIcon != null)
			{
				bean.setIcon(new BitmapDrawable(ctx.getResources(),nnn.largeIcon));
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
				//LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				//ViewGroup localView = (ViewGroup) inflater.inflate(rv.getLayoutId(), null);
				//rv.reapply(ctx.getApplicationContext(), localView);



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
						bean.setSender(ctx.getPackageManager().getApplicationLabel(ai).toString());
					else
						bean.setSender(packageName);

				}
				bean.setAppName(ctx.getPackageManager().getApplicationLabel(ai).toString());
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
				try{
					bean.setMessage(nnn.tickerText.toString());  
					bean.setAppName(ctx.getPackageManager().getApplicationLabel(ai).toString());
				}catch(Exception ex){
					return;
				}
			}
			
			if(id != null){
				bean.setId(id);
			}
			
			if(tagId != null){
				bean.setTagId(tagId);
			}

			/*Log.d("Notification Service", "title  -----" + bean.getSender());
			Log.d("Notification Service", "text  -----" + bean.getMessage());
			Log.d("Notification Service", "content  -----" + bean.getContent());*/



			bean.setPackageName(packageName);
			
			Utils.parseLastMessage(bean);
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
			//Log.d("Notification Service", "tickertext-----" + event.getText().toString());
			
			HelperUtils.writeLogs("Notifications Bean data : " + packageName, ctx, true);
			HelperUtils.writeLogs("App : " + bean.getAppName(), ctx , true);
			HelperUtils.writeLogs("Package : " + bean.getPackageName(), ctx, true);
			//HelperUtils.writeLogs("Message : " + bean.getSender(), ctx);
			HelperUtils.writeLogs("Sender : " + bean.getSender(), ctx, true);

			if(HelperUtils.showFeedback(ctx, SharedPreferenceUtils.getNotCount(ctx) + 1) && Utils.isScreenLocked(ctx)){
				
			}else{
				SharedPreferenceUtils.setNotCount(ctx, SharedPreferenceUtils.getNotCount(ctx) + 1);
			}
			
			if(Utils.isServiceRunning || Utils.isForgroundApp(ctx, ctx.getString(R.string.package_name))){
				Utils.getNotList().add(0,bean);
				Log.d("Notification Service", "Broadcast---");
				ctx.sendBroadcast(new Intent(NotificationReceiver.ACTION_NOTIFICATION_CHANGED));

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
					dialogIntent = new Intent(baseContext, NotificationActivity.class);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					app.startActivity(dialogIntent);
				}else if(!Utils.isScreenLocked(ctx) && (HelperUtils.getNotType(ctx) == Constants.NOT_POPUP  || HelperUtils.getNotType(ctx) == Constants.LOCKSCREEN_POPUP ) ){
					dialogIntent = new Intent(baseContext, PopupActivity.class);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					app.startActivity(dialogIntent);
				}
				else if(HelperUtils.getNotType(ctx) == Constants.NOT_BANNERS || HelperUtils.getNotType(ctx) == Constants.LOCKSCREEN_BANNER){
					Log.d("Notification Service", "Starting new service.");
					//dialogIntent = new Intent(getBaseContext(), BannerActivity.class);
					Utils.isServiceRunning = true;
					ctx.stopService(new Intent(ctx, BannerService.class));
					ctx.startService(new Intent(ctx, BannerService.class));

				}				

			}
			
			if(HelperUtils.showFeedback(ctx,-1) && !Utils.isScreenLocked(ctx)){
				Intent dialogIntent = new Intent(baseContext, FeedbackActivity.class);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				app.startActivity(dialogIntent);
			}

			if(HelperUtils.wakeOnNotification(ctx)){
				turnScreenOn();
			}

		}
	}

	 // use localview to get strings
    private HashMap<Integer, CharSequence> getNotificationStringsFromView(ViewGroup localView)
    {
        HashMap<Integer, CharSequence> notificationStrings = new HashMap<Integer, CharSequence>();

        View v = localView.findViewById(notification_title_id);
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(notification_title_id, ((TextView) v).getText());
        }
        v = localView.findViewById(notification_text_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(notification_text_id , ((TextView) v).getText());
        }
        v = localView.findViewById(notification_info_id);
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(notification_info_id , ((TextView) v).getText());
        }
        v = localView.findViewById(notification_subtext_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(notification_subtext_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(big_notification_summary_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(big_notification_summary_id, ((TextView) v).getText());
        }
        v = localView.findViewById(big_notification_title_id);
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(big_notification_title_id, ((TextView) v).getText());
        }
        v = localView.findViewById(big_notification_content_title );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(big_notification_content_title, ((TextView) v).getText());
        }
        v = localView.findViewById(big_notification_content_text);
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(big_notification_content_text , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_title_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_title_id , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_1_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_1_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_2_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_2_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_3_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_3_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_4_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_4_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_5_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_5_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_6_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_6_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_7_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_7_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_8_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_8_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_9_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_9_id  , ((TextView) v).getText());
        }
        v = localView.findViewById(inbox_notification_event_10_id );
        if (v != null && v instanceof TextView)
        {
            notificationStrings.put(inbox_notification_event_10_id  , ((TextView) v).getText());
        }

        return notificationStrings;
    }
    
    private HashMap<Integer, CharSequence> getNotificationStringFromRemoteViews(RemoteViews view)
    {
        HashMap<Integer, CharSequence> notificationText = new HashMap<Integer, CharSequence>();

        try
        {
            ArrayList<Object> actions = null;
            Field fs = view.getClass().getDeclaredField("mActions");
            if (fs != null)
            {
                fs.setAccessible(true);
                actions = (ArrayList<Object>) fs.get(view);
            }
            if (actions != null)
            {
                final int STRING = 9;
                final int CHAR_SEQUENCE = 10;

                for(Object action : actions)
                {
                    if (action.getClass().getName().equals("android.widget.RemoteViews$ReflectionAction"))
                    {
                        Class<?> reflectionActionClass=action.getClass();
                        Class<?> actionClass=Class.forName("android.widget.RemoteViews$Action");

                        Field methodNameField = reflectionActionClass.getDeclaredField("methodName");
                        Field typeField = reflectionActionClass.getDeclaredField("type");
                        Field valueField = reflectionActionClass.getDeclaredField("value");
                        Field viewIdField = actionClass.getDeclaredField("viewId");

                        methodNameField.setAccessible(true);
                        typeField.setAccessible(true);
                        valueField.setAccessible(true);
                        viewIdField.setAccessible(true);

                        String methodName = (String) methodNameField.get(action);
                        int type = typeField.getInt(action);
                        Object value = valueField.get(action);
                        int viewId = viewIdField.getInt(action);

                        if (type == CHAR_SEQUENCE)
                            notificationText.put(new Integer(viewId), (CharSequence) value);
                        else if (type == STRING)
                            notificationText.put(new Integer(viewId), (String) value);
                    }
                }
            }
        }
        catch(Exception exp)
        {
        }
        return notificationText;
    }


}