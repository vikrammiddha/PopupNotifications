package com.bun.popupnotifications;

import java.util.ArrayList;

import com.readystatesoftware.viewbadger.BadgeView;



import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import android.util.TypedValue;
import android.view.LayoutInflater;

import android.view.View;

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
		public TextView badge;
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
	
	public  int getAdapterSize(){
		if(nList == null){
			return 0;
		}
		return nList.size();
	}
	
	public void removeNotification(int pos){
		if(nList != null){
			nList.remove(pos);
		}
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
	public NotificationBean getItem(int position) {
		// TODO Auto-generated method stub
		return nList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {		

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
		      viewHolder.badge = (TextView) view.findViewById(R.id.unreadCountTextId);
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
		
		
		if(n.getNotCount() > 0){
			holder.badge.setText(String.valueOf(n.getNotCount()));
			
			Resources r = context.getResources();
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());
			int r1 = (int)px;
			float[] outerR = new float[] {r1, r1, r1, r1, r1, r1, r1, r1};
	        
			RoundRectShape rr = new RoundRectShape(outerR, null, null);
			ShapeDrawable drawable = new ShapeDrawable(rr);
			drawable.getPaint().setColor(Color.RED);
			holder.badge.setBackground(drawable);
		}else{
			holder.badge.setVisibility(View.GONE);
		}
		
		holder.timeText.setText(n.getNotTime());		
		
		return view;
	}

}
