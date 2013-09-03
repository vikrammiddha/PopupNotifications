package com.bun.popupnotifications;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AppSelectionAdapter extends BaseAdapter{
	
	public static ArrayList<Boolean> itemChecked1 = new ArrayList<Boolean>(); 

	static class ViewHolder {
		public TextView appNameText;
		public ImageView appIcon;		
		public CheckBox cb;
	}

	private ArrayList<ApplicationBean> nList = new ArrayList<ApplicationBean>();
	private Context context;

	public AppSelectionAdapter(Context context) {
		super();
		this.context = context;

	}
	
	public void addAppList(ArrayList<ApplicationBean> appList){
		nList.addAll(appList);
	}

	public void addApplication(ApplicationBean notf){ 
		if(nList == null){
			nList = new ArrayList<ApplicationBean>();
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
	public View getView(final int position, View view, ViewGroup parent) {		

		final ApplicationBean n = nList.get(position);
		if(view == null || view.getTag() == null){
			LayoutInflater inflater =
					(LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(
					R.layout.app_selection_row, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.appNameText = (TextView) view.findViewById(R.id.appSelectionTextId);			
			viewHolder.appIcon = (ImageView)view.findViewById(R.id.appSelectionIconId);
			viewHolder.cb = (CheckBox)view.findViewById(R.id.appSelectionCheckBoxId);
			
			view.setTag(viewHolder);

		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.appIcon.setImageDrawable(n.getAppIcon());
		
		holder.appNameText.setText(n.getAppName());
		
		holder.cb.setChecked(n.getIsSelected());
		
			
		
		holder.cb.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            CheckBox cb = (CheckBox) v;
	            
	            //planet.setSelected(cb.isChecked());
	            if (holder.cb.isChecked()) {
	                nList.get(position).setIsSelected(true);
	                holder.cb.setChecked(true);
	                SharedPreferenceUtils.setAllowedApps(context, n.getPackageName(), "");
	            } else if (!holder.cb.isChecked()) {
	            	nList.get(position).setIsSelected(false);
	                holder.cb.setChecked(false);
	                SharedPreferenceUtils.removeApp(context, n.getPackageName());
	            }
	        }
	    });

		return view;
	}

}
