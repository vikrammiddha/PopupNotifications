package com.bun.popupnotifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.ProgressBar;

public class AppSelectionActivity extends Activity{
	
	ListView layout;
	AppSelectionAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.app_selection_main);
		
		 
		
		layout = (ListView) findViewById(R.id.appSelectionMainListViewId);	
		
		adapter = new AppSelectionAdapter(this);
		
		new Load().execute();
		
	}
	
	 ProgressDialog progDailog ;
	
	private class Load extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(AppSelectionActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }
        @Override
        protected String doInBackground(String... aurl) {
        	populateAdapter();
            return null;
        }
        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            progDailog.dismiss();
            layout.setAdapter(adapter);
        }
    }
	
	private void populateAdapter(){
		
		final PackageManager pm = this.getApplicationContext().getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		HashMap<String,String> appNamesMap = new HashMap<String, String>();
		
		
		TreeSet<String> allowedApps = new TreeSet<String>();
		TreeSet<String> otherApps = new TreeSet<String>();
		
		allowedApps = SharedPreferenceUtils.getAllAlowedApps(this);

		for (ApplicationInfo packageInfo : packages) {
			
			appNamesMap.put(packageInfo.packageName, packageInfo.loadLabel(pm).toString());
			
			if(!allowedApps.contains(packageInfo.packageName)){
				otherApps.add(packageInfo.packageName);
				
			}
			
		}
		
		ArrayList<ApplicationBean> aList = new ArrayList<ApplicationBean>();
		
		for(String pack : allowedApps){
			ApplicationBean bean = new ApplicationBean();
			
			bean.setPackageName(pack);
			bean.setAppName(appNamesMap.get(bean.getPackageName()));			
			bean.setAppIcon(getAppIcon(bean.getPackageName()));
			bean.setIsSelected(true);
			aList.add(bean);
		}
		
		Collections.sort(aList, new Comparator<ApplicationBean>(){
			  public int compare(ApplicationBean a1, ApplicationBean a2) {
			    return a1.getAppName().compareToIgnoreCase(a2.getAppName());
			  }
			});
		
		adapter.addAppList(aList);
		
		aList.clear();
		
		for(String pack : otherApps){
			ApplicationBean bean = new ApplicationBean();
			
			bean.setPackageName(pack);
			bean.setAppName(appNamesMap.get(bean.getPackageName()));			
			bean.setAppIcon(getAppIcon(bean.getPackageName()));
			bean.setIsSelected(false);
			aList.add(bean);
		}
		
		Collections.sort(aList, new Comparator<ApplicationBean>(){
			  public int compare(ApplicationBean a1, ApplicationBean a2) {
			    return a1.getAppName().compareToIgnoreCase(a2.getAppName());
			  }
			});
		
		adapter.addAppList(aList);
		
		aList.clear();
		
		
		
		
		
	}
	
	private Drawable getAppIcon(String packageName){
		Drawable icon = null;
		try{
			icon = getApplicationContext().getPackageManager().getApplicationIcon(packageName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(icon == null){
			icon = this.getResources().getDrawable( R.drawable.ic_launcher );
		}
		
		return icon;
	}

}
