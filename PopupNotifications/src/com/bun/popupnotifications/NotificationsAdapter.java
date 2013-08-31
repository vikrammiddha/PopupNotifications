package com.bun.popupnotifications;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Typeface;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

public class NotificationsAdapter extends BaseAdapter{

	static class ViewHolder {
		public VerticalMarqueeTextView text;
		public ImageView image;
		public TextView timeText;
	}

	private ArrayList<NotificationBean> nList = new ArrayList<NotificationBean>();
	private Context context;

	public NotificationsAdapter(Context context) {
		super();
		this.context = context;

	}

	public void addNotification(NotificationBean notf){
		nList.add(notf);
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
					(LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(
					R.layout.notification_row, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
		      viewHolder.text = (VerticalMarqueeTextView) view.findViewById(R.id.notMessageTextId);
		      viewHolder.image = (ImageView)view.findViewById(R.id.appImageViewId);
		      viewHolder.timeText = (TextView) view.findViewById(R.id.notTimeTextId);
		      view.setTag(viewHolder);

		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		
		holder.image.setImageDrawable(n.getIcon());

		String message = "";

		if(n.getSender() != null && !n.getSender().trim().equals("")){
			message = n.getSender() + " : ";
		}

		message += n.getMessage();		
		
		
		holder.text.setText(message);
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(),
		        "fonts/robotomedium.ttf");
		
		holder.text.setTypeface(tf);
		
		holder.timeText.setMovementMethod(new ScrollingMovementMethod());
		
		holder.timeText.setText(n.getNotTime());
		return view;
	}

}
