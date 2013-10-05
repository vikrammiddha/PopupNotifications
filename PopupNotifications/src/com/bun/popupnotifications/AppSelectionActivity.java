package com.bun.popupnotifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.actionbarsherlock.app.SherlockActivity;
 
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import com.actionbarsherlock.view.MenuItem;


public class AppSelectionActivity extends SherlockActivity{



	ListView layout;
	AppSelectionAdapter adapter;
	EditText searchBox;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.app_selection_main);
		layout = (ListView) findViewById(R.id.appSelectionMainListViewId);	

		adapter = new AppSelectionAdapter(this);

		new Load().execute();	


		Boolean serviceStatus = Utils.isAccessibilityEnabled(this);

		if(serviceStatus == false){
			showServiceAlert();
		}

	}

	

	private void showServiceAlert(){
		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				AppSelectionActivity.this);

		// Setting Dialog Title
		alertDialog2.setTitle(getString(R.string.popup_not_service));

		// Setting Dialog Message
		alertDialog2.setMessage(R.string.service_warning);

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog2.setPositiveButton(getString(R.string.activate),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
				startActivityForResult(intent, 0);
			}
		});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton(getString(R.string.later),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});

		// Showing Alert Dialog
		alertDialog2.show();
	}



	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		//Log.d("App Selection", "Menu Item ===" + item.getTitle() + "=== Id==" + item.getItemId());


		switch (item.getItemId()) {
		case 0:
			searchBox = (EditText) item.getActionView();
			searchBox.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// Call back the Adapter with current character to Filter
					adapter.getFilter().filter(s.toString());
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
			searchBox.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			break;

		case 1:
			Intent i = new Intent(this, NotificationPreferenceActivity.class);
			startActivityForResult(i, 0);
			break;
			
		case 3:
			Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.market_url)));
        	startActivity(intent);
			break;

		default:
			return super.onOptionsItemSelected(item);

		}   
		return super.onOptionsItemSelected(item);
	} 

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		//Used to put dark icons on light action bar
		boolean isLight = false;



		menu.add(Menu.NONE,0,0,getString(R.string.search))
		.setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.ic_search)
		.setActionView(R.layout.collapsible_edittext)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		menu.add(Menu.NONE,1,1,getString(R.string.menu_settings)); 

		menu.add(Menu.NONE,2,2,getString(R.string.menu_tutorial)); 
		
		menu.add(Menu.NONE,3,3,getString(R.string.menu_rateus)); 

		return super.onCreateOptionsMenu(menu);


	}

	ProgressDialog progDailog ;

	private class Load extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(AppSelectionActivity.this);
			progDailog.setMessage(getString(R.string.loading));
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

		try{

			if(adapter == null){
				adapter = new AppSelectionAdapter(this);
			}

			final PackageManager pm = this.getApplicationContext().getPackageManager();
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			HashMap<String,String> appNamesMap = new HashMap<String, String>();


			TreeSet<String> allowedApps = new TreeSet<String>();
			TreeSet<String> otherApps = new TreeSet<String>();

			allowedApps = SharedPreferenceUtils.getAllAlowedApps(this);

			for (ApplicationInfo packageInfo : packages) {

				if(packageInfo.loadLabel(pm).toString().startsWith(getString(R.string.com))){
					continue;
				}

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

				if(bean.getAppName() == null)
					continue;
				bean.setAppIcon(getAppIcon(bean.getPackageName()));
				bean.setIsSelected(true);
				aList.add(bean);
			}

			Collections.sort(aList, new Comparator<ApplicationBean>(){
				public int compare(ApplicationBean a1, ApplicationBean a2) {
					//Log.d("App Selection", "Package NAme----" + a1.getPackageName());
					return a1.getAppName().compareToIgnoreCase(a2.getAppName());
				}
			});
			
			if(adapter == null){
				adapter = new AppSelectionAdapter(this);
			}
			
			//Log.d("adapter", "adapter=======" + adapter);
			//Log.d("adapter", "aList=======" + aList);
			
			if(aList != null)
				adapter.addAppList(aList);

			aList.clear();

			for(String pack : otherApps){
				ApplicationBean bean = new ApplicationBean();

				bean.setPackageName(pack);
				bean.setAppName(appNamesMap.get(bean.getPackageName()));

				if(bean.getAppName() == null)
					continue;

				bean.setAppIcon(getAppIcon(bean.getPackageName()));
				bean.setIsSelected(false);
				aList.add(bean);
			}

			Collections.sort(aList, new Comparator<ApplicationBean>(){
				public int compare(ApplicationBean a1, ApplicationBean a2) {
					return a1.getAppName().compareToIgnoreCase(a2.getAppName());
				}
			});
			
			if(adapter == null){
				adapter = new AppSelectionAdapter(this);
			}

			adapter.addAppList(aList);

			aList.clear();

			aList = null;

		}catch(Exception e){
			e.printStackTrace();
		}

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		adapter.clearNotifications();
		adapter = null;
		try{
			progDailog.dismiss();
		}catch(Exception e){

		}
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//adapter = null;
		progDailog.dismiss();
		finish();
	}

}
