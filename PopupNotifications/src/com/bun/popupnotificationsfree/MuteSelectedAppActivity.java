package com.bun.popupnotificationsfree;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.bun.popupnotificationsfree.R;


public class MuteSelectedAppActivity extends SherlockActivity{



	ListView layout;
	MuteSelectedAppsAdapter adapter;
	EditText searchBox;
	Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mute_selected_main);
		layout = (ListView) findViewById(R.id.muteSelectedListViewId);	

		adapter = new MuteSelectedAppsAdapter(this);
		
		ctx = this;
		
		new Load().execute();

		

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


		default:
			return super.onOptionsItemSelected(item);

		}   
		return super.onOptionsItemSelected(item);
	} 

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		//Used to put dark icons on light action bar
		boolean isLight = false;



		menu.add(Menu.NONE,0,0,ctx.getString(R.string.search))
		.setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.ic_search)
		.setActionView(R.layout.collapsible_edittext)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);		

		return super.onCreateOptionsMenu(menu);


	}

	ProgressDialog progDailog ;

	private class Load extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(MuteSelectedAppActivity.this);
			progDailog.setMessage(ctx.getString(R.string.loading));
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
				adapter = new MuteSelectedAppsAdapter(this);
			}

			final PackageManager pm = this.getApplicationContext().getPackageManager();
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			HashMap<String,String> appNamesMap = new HashMap<String, String>();


			TreeSet<String> allowedApps = new TreeSet<String>();


			allowedApps = SharedPreferenceUtils.getAllAlowedApps(this);

			for (ApplicationInfo packageInfo : packages) {

				if(packageInfo.loadLabel(pm).toString().startsWith(ctx.getString(R.string.com))){
					continue;
				}

				appNamesMap.put(packageInfo.packageName, packageInfo.loadLabel(pm).toString());				

			}

			ArrayList<ApplicationBean> aList = new ArrayList<ApplicationBean>();

			for(String pack : allowedApps){
				ApplicationBean bean = new ApplicationBean();

				bean.setPackageName(pack);
				bean.setAppName(appNamesMap.get(bean.getPackageName()));	

				if(bean.getAppName() == null)
					continue;
				bean.setAppIcon(getAppIcon(bean.getPackageName()));

				String spText = SharedPreferenceUtils.getAppData(this, bean.getPackageName());

				if(!spText.equals("--") && !spText.equals("")){
					bean.setIsSelected(true);
					bean.setSummaryText(ctx.getString(R.string.muted_till) + spText);
					bean.setRemoveIcon(this.getResources().getDrawable(R.drawable.remove));
				}else{
					bean.setIsSelected(false);
					bean.setSummaryText("");
				}



				aList.add(bean);
			}

			Collections.sort(aList, new Comparator<ApplicationBean>(){
				public int compare(ApplicationBean a1, ApplicationBean a2) {
					int boolResult = a2.getIsSelected().compareTo(a1.getIsSelected());

					if(boolResult != 0){
						return boolResult;
					}

					return a1.getAppName().compareToIgnoreCase(a2.getAppName());
				}
			});

			aList.removeAll(Collections.singleton(null));

			if(aList != null){
				for(ApplicationBean ab : aList){
					if(ab != null)
						adapter.addApplication(ab);
				}
			}

			aList.clear();				

			aList = null;

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public void muteSelectedApps(View v){
		showMuteOptions();
	}
	RadioGroup radioGroup1;

	RadioGroup radioGroup2;
	RadioButton radioButton2;

	private void showMuteOptions(){
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.mute_app_dialogue, (ViewGroup) findViewById(R.id.cpRoot));



		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				MuteSelectedAppActivity.this);

		// Setting Dialog Title
		alertDialog2.setTitle("");

		// Setting Dialog Message
		alertDialog2.setView(layout);

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		radioGroup1 = (RadioGroup) layout.findViewById(R.id.muteOptions1);
		radioGroup1.setVisibility(View.GONE);

		alertDialog2.setPositiveButton(getString(R.string.upgrade),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {							

				HelperUtils.upgradeNowDialogue(ctx);

			}
		});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				dialog.cancel();			

			}
		});

		alertDialog2.show();
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
		finish();
	}

}
