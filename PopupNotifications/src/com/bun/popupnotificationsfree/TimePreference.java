package com.bun.popupnotificationsfree;

import com.bun.popupnotificationsfree.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Custom preference for time selection. Hour and minute are persistent and
 * stored separately as ints in the underlying shared preferences under keys
 * KEY.hour and KEY.minute, where KEY is the preference's key.
 */
public class TimePreference extends DialogPreference {
  
  /** The widget for picking a time */
  private TimePicker timePicker;

  /** Default hour */
  private static final int DEFAULT_HOUR = 8;

  /** Default minute */
  private static final int DEFAULT_MINUTE = 0;
  SharedPreferences prefs;
  
  Context ctx;
  
  int id = 0;

  /**
   * Creates a preference for choosing a time based on its XML declaration.
   * 
   * @param context
   * @param attributes
   */
  public TimePreference(Context context,
                        AttributeSet attributes) {
    super(context, attributes);
    setPersistent(false);
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
    ctx = context;
    id = this.getTitleRes();
  }

  /**
   * Initialize time picker to currently stored time preferences.
   * 
   * @param view
   * The dialog preference's host view
   */
  @Override
  public void onBindDialogView(View view) {
    super.onBindDialogView(view);
    timePicker = (TimePicker) view.findViewById(R.id.prefTimePicker);
    if(R.string.start_time == id){
    	String startTime = getSharedPreferences().getString("start_sleep_time", "23:00");
    	timePicker.setCurrentHour(Integer.valueOf(startTime.split(":")[0]));
        timePicker.setCurrentMinute(Integer.valueOf(startTime.split(":")[1]));
    }else{
    	String endTime = getSharedPreferences().getString("end_sleep_time", "07:00");
    	timePicker.setCurrentHour(Integer.valueOf(endTime.split(":")[0]));
        timePicker.setCurrentMinute(Integer.valueOf(endTime.split(":")[1]));
    }
    
    //timePicker.setIs24HourView(DateFormat.is24HourFormat(timePicker.getContext()));
    
  }

  /**
   * Handles closing of dialog. If user intended to save the settings, selected
   * hour and minute are stored in the preferences with keys KEY.hour and
   * KEY.minute, where KEY is the preference's KEY.
   * 
   * @param okToSave
   * True if user wanted to save settings, false otherwise
   */
  @Override
  protected void onDialogClosed(boolean okToSave) {
    super.onDialogClosed(okToSave);
    if (okToSave) {
      timePicker.clearFocus();
      SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
      Editor editor = sharedPrefs.edit();
      if(R.string.start_time == id)
    	  editor.putString("start_sleep_time", String.format("%02d", timePicker.getCurrentHour()) + ":" + String.format("%02d", timePicker.getCurrentMinute()));
      else
    	  editor.putString("end_sleep_time", String.format("%02d", timePicker.getCurrentHour()) + ":" + String.format("%02d", timePicker.getCurrentMinute()));
      //editor.putInt(getKey() + ".hour", timePicker.getCurrentHour());
      //editor.putInt(getKey() + ".minute", timePicker.getCurrentMinute());
      editor.commit();  
      
      
    }
  }
}
