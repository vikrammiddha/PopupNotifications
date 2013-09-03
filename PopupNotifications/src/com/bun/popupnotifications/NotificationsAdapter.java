package com.bun.popupnotifications;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Typeface;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

public class NotificationsAdapter extends BaseAdapter{

	static class ViewHolder {
		public TextView text;
		public ImageView image;
		public ImageView smallImage;
		public TextView timeText;
	}

	private ArrayList<NotificationBean> nList = new ArrayList<NotificationBean>();
	private Context context;

	public NotificationsAdapter(Context context) {
		super();
		this.context = context;

	}

	public void addNotification(NotificationBean notf){ 
		if(nList == null){
			nList = new ArrayList<NotificationBean>();
		}
		nList.add(notf);
	}

	public void clearNotifications(){
		nList.clear();
		//nList = null;
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
		if(view == null || view.getTag() == null){
			LayoutInflater inflater =
					(LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(
					R.layout.notification_row, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
		      viewHolder.text = (TextView) view.findViewById(R.id.notMessageTextId);
		     // viewHolder.text.setMovementMethod(new ScrollingMovementMethod());
		     // viewHolder.text.setSelected(true);
		      viewHolder.image = (ImageView)view.findViewById(R.id.appImageViewId);
		      viewHolder.smallImage = (ImageView)view.findViewById(R.id.appImageSmallId);
		      viewHolder.timeText = (TextView) view.findViewById(R.id.notTimeTextId);
		      view.setTag(viewHolder);

		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		
		holder.image.setImageDrawable(n.getIcon());
		holder.smallImage.setImageDrawable(n.getNotIcon());

		String message = "";

		if(n.getSender() != null && !n.getSender().trim().equals("")){ 
			message = n.getSender() + " : ";
		}

		message += n.getMessage();		
		
		
		holder.text.setText(message);	
		
		//holder.text.setTypeface(Utils.tf);	 	
		
		holder.timeText.setText(n.getNotTime());
		
		/*holder.text.setVerticalScrollBarEnabled(true);
		holder.text.setMovementMethod(new ScrollingMovementMethod());
		
		OnTouchListener listener = new OnTouchListener() {
		      @Override
		      public boolean onTouch(View v, MotionEvent event) {
		                boolean isLarger;

		                isLarger = ((TextView) v).getLineCount()
		                        * ((TextView) v).getLineHeight() > v.getHeight();
		                if (event.getAction() == MotionEvent.ACTION_MOVE
		                        && isLarger) {
		                    v.getParent().requestDisallowInterceptTouchEvent(true);

		                } else {
		                    v.getParent().requestDisallowInterceptTouchEvent(false);

		                }
		                return false;
		            }
		        };

		holder.text.setOnTouchListener(listener);*/
		
		return view;
	}

}
