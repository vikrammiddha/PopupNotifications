package com.bun.popupnotifications;

import java.util.ArrayList;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

public class NotificationsAdapter extends BaseAdapter{
	
	private ArrayList<NotificationBean> nList = new ArrayList<NotificationBean>();
	private Context context;
	
	public NotificationsAdapter(Context context) {
		super();
		this.context = context;
		
	}
	
	public void addNotification(NotificationBean notf){
		nList.add(0, notf);
	}
	
	public void clearNotifications(){
		nList.clear();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return nList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		
		NotificationBean n = nList.get(position);
		if(view == null){
			LayoutInflater inflater =
					LayoutInflater.from(parent.getContext());
			view = inflater.inflate(
					R.layout.notification_row, parent, false);
			
		}
		
		View v = view.findViewById(R.id.appImageViewId);
		ImageView timeTextView = (ImageView)v;
		timeTextView.setImageDrawable(n.getIcon());
		
		v = view.findViewById(R.id.notMessageTextId);
		TextView tv = (TextView)v;
		String message = "";
		
		if(n.getSender() != null && !n.getSender().trim().equals("")){
			message = n.getSender() + " : ";
		}
		                            
		message += n.getMessage();
		tv.setText(message);
		return view;
	}

}
