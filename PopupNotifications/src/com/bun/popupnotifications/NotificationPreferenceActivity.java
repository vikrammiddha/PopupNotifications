package com.bun.popupnotifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class NotificationPreferenceActivity  extends PreferenceActivity{

	SharedPreferences prefs;
	
	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_preference);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setMuteAllAppsPreferenceData();
		
		setSelectedAppListListener();
		
		ctx = this;

	}
	
	
	private void setSelectedAppListListener(){
		Preference pref = findPreference("mute_selected_screen");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

		    @Override
		    public boolean onPreferenceClick(Preference preference) {

		        Intent testIntent = new Intent(getApplicationContext(), MuteSelectedAppActivity.class);
		        startActivity(testIntent);

		        return true;
		    }
		});
	}
	
	String muteAllAppsSummary = "";
	
	private void setMuteAllAppsPreferenceData(){
		
		boolean muteAllApps = false;
		
		final Preference customPref = (Preference) findPreference("mute_all_apps");
		
		String muteAllAppsPref = SharedPreferenceUtils.getAppData(this, "com.AA");
		
		if(!"--".equals(muteAllAppsPref)){
			SharedPreferences.Editor p = PreferenceManager.getDefaultSharedPreferences(this).edit();
			p.putBoolean("mute_all_apps", true);
			p.commit();
			CheckBoxPreference cbp = (CheckBoxPreference)customPref;
			cbp.setChecked(true);
			muteAllApps = true;
		}
				

		if(muteAllApps){
			muteAllAppsSummary = "All Apps Muted till: "+ muteAllAppsPref;
			getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(false);
			
		}else{
			muteAllAppsSummary = "";
			getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(true);			
		}

		
		customPref.setSummary(muteAllAppsSummary);
		
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

	        @Override
	        public boolean onPreferenceChange(Preference preference,
	                Object newValue) {
	            
	        	if(Boolean.valueOf(newValue.toString())){
	        		customPref.setSummary("All Apps Muted");
	        		getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(false);
	        		showMuteOptions(customPref);
	        	}else{
	        		customPref.setSummary("");
	        		getPreferenceScreen().findPreference("mute_selected_screen").setEnabled(true);
	        		SharedPreferenceUtils.removeApp(ctx, "com.AA");
	        	}
	            return true;
	        }

	});
	}
	
	RadioGroup radioGroup1;
	
	RadioGroup radioGroup2;
	RadioButton radioButton2;
	
	private void showMuteOptions(final Preference customPref){
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.mute_app_dialogue, (ViewGroup) findViewById(R.id.cpRoot));
		
		

		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
				NotificationPreferenceActivity.this);

		// Setting Dialog Title
		alertDialog2.setTitle("");

		// Setting Dialog Message
		alertDialog2.setView(layout);

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		
		radioGroup1 = (RadioGroup) layout.findViewById(R.id.muteOptions1);
		radioGroup1.setVisibility(View.GONE);
		alertDialog2.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {							
				
				radioGroup2 = (RadioGroup) layout.findViewById(R.id.muteOptions2);
				int selectedId = radioGroup2.getCheckedRadioButtonId();				
				radioButton2 = (RadioButton) layout.findViewById(selectedId);
				
				SharedPreferenceUtils.setAllowedApps(ctx, "com.AA", Utils.getMuteTime(ctx,radioButton2.getText().toString()));
				
				customPref.setSummary("All Apps Muted till: "+ Utils.getMuteTime(ctx,radioButton2.getText().toString()));
				
				Toast.makeText(NotificationPreferenceActivity.this,
						Utils.getMuteToastText(ctx, ctx.getString(R.string.mute_all_apps), radioButton2.getText().toString(), ""), Toast.LENGTH_SHORT).show();
			}
		});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				dialog.cancel();
				customPref.setSummary("");
				Editor editor = prefs.edit();
				editor.putBoolean("mute_all_apps", false);
				editor.commit();
				
				CheckBoxPreference cbp = (CheckBoxPreference)customPref;
				cbp.setChecked(false);
				
			}
		});

		alertDialog2.show();
	}


}
