package com.bun.popupnotifications;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

@SuppressLint("NewApi")
public class NotificationsAdapter extends BaseAdapter{

	public Integer textViewSize;

	static class ViewHolder {
		public TextView text;
		public ImageView image;
		public CircleImage circleImage;
		public ImageView smallImage;
		public TextView timeText;
		public TextView badge;
	}

	private ArrayList<NotificationBean> nList = new ArrayList<NotificationBean>();
	private Context context;
	private int lastPosition = -1;
	public  Boolean showDismissAnimation = false;

	public NotificationsAdapter(Context context) {
		super();
		this.context = context;

	}

	public void removeAllNotifications(){
		if(nList == null){
			return;
		}
		for(NotificationBean n : nList){
			n = null;
		}
		nList.clear();
		nList = null;
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
		if(nList != null)
			nList.clear();
		//nList = null;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(nList == null)
			return 0;

		return nList.size();
	}

	public void clearAppNotifications(Set<String> packageSet){
		Iterator<NotificationBean> iter = nList.iterator();

		while(iter.hasNext()){

			NotificationBean nb = iter.next();

			if(packageSet.contains(nb.getPackageName())){							
				iter.remove();
			}
		}
	}

	@Override
	public NotificationBean getItem(int position) {
		// TODO Auto-generated method stub
		try{
			return nList.get(position);
		}
		catch(Exception e){
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {		

		NotificationBean n = nList.get(position);
		//if(view == null || view.getTag() == null){
		LayoutInflater inflater;
		ViewHolder viewHolder  = new ViewHolder();;
		if(HelperUtils.isCircularImage(context, n.getPackageName())){
			inflater =
					(LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(
					R.layout.circle_notification_row, parent, false);

			viewHolder.text = (TextView) view.findViewById(R.id.notMessageTextId1);
			// viewHolder.text.setMovementMethod(new ScrollingMovementMethod());
			// viewHolder.text.setSelected(true);
			viewHolder.circleImage = (CircleImage)view.findViewById(R.id.appImageViewId1);
			viewHolder.smallImage = (ImageView)view.findViewById(R.id.appImageSmallId1);
			viewHolder.timeText = (TextView) view.findViewById(R.id.notTimeTextId1);
			viewHolder.badge = (TextView) view.findViewById(R.id.unreadCountTextId1);
		}else{
			inflater =
					(LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(
					R.layout.notification_row, parent, false);

			viewHolder.text = (TextView) view.findViewById(R.id.notMessageTextId);
			// viewHolder.text.setMovementMethod(new ScrollingMovementMethod());
			// viewHolder.text.setSelected(true);
			viewHolder.image = (ImageView)view.findViewById(R.id.appImageViewId);
			viewHolder.smallImage = (ImageView)view.findViewById(R.id.appImageSmallId);
			viewHolder.timeText = (TextView) view.findViewById(R.id.notTimeTextId);
			viewHolder.badge = (TextView) view.findViewById(R.id.unreadCountTextId);

		}
		view.setTag(viewHolder);

		//}



		ViewHolder holder = (ViewHolder) view.getTag();
		
		if(HelperUtils.isCircularImage(context, n.getPackageName())){	
			holder.circleImage.setImageDrawable(n.getIcon());
			holder.circleImage.setBorderColor(HelperUtils.getBorderColor(context, n.getPackageName()));
			holder.circleImage.setBorderWidth(HelperUtils.getBorderSize(context));
		}else{
			holder.image.setImageDrawable(n.getIcon());
		}
		holder.smallImage.setImageDrawable(n.getNotIcon());

		String message = "";

		if(n.getSender() != null && !n.getSender().trim().equals("")){ 
			message = "<b>" + n.getSender() + " : " + "</b>";
		}

		message += n.getMessage();		


		holder.text.setText(Html.fromHtml(message));
		
		holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, Float.valueOf(HelperUtils.getFontSize(context, n.getPackageName())));
		
		if(SharedPreferenceUtils.getAppFont(context, n.getPackageName()) != ""){
			holder.text.setTypeface(FontLoader.getTypeFace(context,SharedPreferenceUtils.getAppFont(context, n.getPackageName()))); 
		}else{
			holder.text.setTypeface(Utils.typeFace);
		}

		int fontColor = HelperUtils.getFontColor(context, n.getPackageName());

		if(fontColor == 0){
			holder.text.setTextColor(Color.WHITE);
		}else{
			holder.text.setTextColor(fontColor);
		}

		if(textViewSize != null){
			holder.text.setMaxLines(textViewSize);
		}
		
		if(n.getNotCount() > 1){
			holder.badge.setText(String.valueOf(n.getNotCount()));

			Resources r = context.getResources();
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());
			int r1 = (int)px;
			float[] outerR = new float[] {r1, r1, r1, r1, r1, r1, r1, r1};

			RoundRectShape rr = new RoundRectShape(outerR, null, null);
			ShapeDrawable drawable = new ShapeDrawable(rr);
			drawable.getPaint().setColor(Color.RED);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				holder.badge.setBackground(drawable);
			}else{
				holder.badge.setBackgroundDrawable(drawable);

			}

		}else{
			holder.badge.setVisibility(View.GONE);
		}

		if(Utils.isScreenLocked(context) || n.getPackageName().contains("com.bun.popupnotification")){
			holder.timeText.setText(n.getNotTime());	
			if(fontColor == 0){
				holder.timeText.setTextColor(Color.WHITE);
			}else{
				holder.timeText.setTextColor(fontColor);
			}
			
			holder.timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, Float.valueOf(HelperUtils.getFontSize(context, n.getPackageName())));
		}else{
			holder.timeText.setVisibility(View.GONE);
		}

		int bgColor = HelperUtils.getBackgroundColor(context, n.getPackageName());
		if(bgColor == 0){
			bgColor = Color.BLACK;
		}
		
		int borderColor = HelperUtils.getBorderColor(context, n.getPackageName());

		if(HelperUtils.getBackgroundColor(context, n.getPackageName()) != null ){
			
			int strokeWidth = HelperUtils.getBorderSize(context); // 3dp
			int roundRadius = 0; // 8dp
			
			if(context.getString(R.string.bubbles).equals(SharedPreferenceUtils.getTheme(context))){
				
				roundRadius = 25;
			}
			
			int strokeColor = 0;//Color.BLACK;//Color.parseColor("#B1BCBE");
			strokeColor = borderColor;
			if(Utils.isScreenLocked(context)){
				//strokeColor = Color.parseColor("#B1BCBE");
			}else{
				//strokeColor = Color.BLACK;
				view.setPadding(0, 10, 10, 10);
			}
			int fillColor = bgColor;

			GradientDrawable gd = new GradientDrawable();
			gd.setColor(fillColor);
			gd.setCornerRadius(roundRadius);
			gd.setStroke(strokeWidth, strokeColor);	

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				view.setBackground(gd);
			}else{
				view.setBackgroundDrawable(gd);
			}
		}

		
		view.getBackground().setAlpha(HelperUtils.getTransparentBackround(context));


		if(!HelperUtils.isDisableAnimations(context)){

			if(Utils.isScreenScrolling ){
				Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
				view.startAnimation(animation);
				lastPosition = position;
				//Utils.isAddedFirstItem = false;
			}

			if(showDismissAnimation){
				if(position%2 == 0){
					Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_left_out);
					view.startAnimation(animation);
				}else{
					Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_right_out);
					view.startAnimation(animation);
				}

			}	

			if(Utils.isAddedFirstItem && position == 0 && !showDismissAnimation){
				Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
				view.startAnimation(animation);
			}

		}

		return view;
	}

}
