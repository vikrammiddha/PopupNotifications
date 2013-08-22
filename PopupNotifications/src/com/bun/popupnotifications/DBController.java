package com.bun.popupnotifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBController  extends SQLiteOpenHelper {
	private static final String LOGCAT = null;
	Context ctx;

	public DBController(Context applicationcontext) {
		super(applicationcontext, "popupnotifications.db", null, 1);
		Log.d(LOGCAT,"Created");
		ctx = applicationcontext;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String query;
		query = "CREATE TABLE app_list ( appId INTEGER PRIMARY KEY, appName Text, packageName Text, muteDate Text)";

		String prefTableQuer = "CREATE TABLE preferences (key Text, value Text)";

		database.execSQL(query);
		database.execSQL(prefTableQuer);

		populateDefaultPreferences(database);

		Log.d(LOGCAT,"not history Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
		String query;
		query = "DROP TABLE IF EXISTS app_list";
		String prefQuery = "DROP TABLE IF EXISTS preferences";    

		database.execSQL(query);
		database.execSQL(prefQuery);   
		onCreate(database);
	}


	public void deleteAppFromList(String packageName){
		SQLiteDatabase database = this.getWritableDatabase();	
		database.delete("app_list",  "packageName = ?" , new String[] { packageName });		 
	}


	public HashMap<String,String> getAppPackageMap(){

		HashMap<String,String> retMap = new HashMap<String,String>();

		String selectQuery = "SELECT  distinct appName, packageName FROM app_list ";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				retMap.put(cursor.getString(0), cursor.getString(1));	    		
			} while (cursor.moveToNext());
		}
		cursor.close();

		return retMap;

	}

	public void insertAppIntoList(HashMap<String, String> queryValues, Context context) {

		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();   
		values.put("appName", queryValues.get("appName"));
		values.put("packageName", queryValues.get("packageName"));
		values.put("muteDate", queryValues.get("muteDate"));
		database.insert("app_list", null, values);   

	}

	public void insertPreference(HashMap<String, String> queryValues, Context context) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("key", queryValues.get("key"));
		values.put("value", queryValues.get("value"));

		database.insert("preferences", null, values);

	}

	public int updatePreferences(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();  
		ContentValues values = new ContentValues();
		String k = "";
		for(String key : queryValues.keySet()){
			values.put("value", queryValues.get(key));
			k = key; 
		}
		//database.close();
		return database.update("preferences", values, "key" + " = ?", new String[] { k });
	}


	public ArrayList<HashMap<String, String>> getAllApps() {
		ArrayList<HashMap<String, String>> wordList;
		wordList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM app_list order by appId desc";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				if(cursor.getString(1) != null && (cursor.getString(1).toUpperCase().equals("SYSTEM UI"))
						|| cursor.getString(1).toUpperCase().equals("ANDROID SYSTEM"))
					continue;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("appId", cursor.getString(0));        
				if(cursor.getString(1) != null && cursor.getString(4).toUpperCase().equals("GOOGLE SERVICES FRAMEWORK")){
					map.put("appName", "Google Talk");
				}else{
					map.put("appName", cursor.getString(1));
				}

				map.put("packageName", cursor.getString(2));
				map.put("muteDate", cursor.getString(3));
				wordList.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		//database.close();
		return wordList;
	}

	public HashMap<String, String> getAllPreferences() {
		HashMap<String, String> prefMap;
		prefMap = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM preferences ";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				prefMap.put(cursor.getString(0), cursor.getString(1));

			} while (cursor.moveToNext());
		}
		cursor.close();

		return prefMap;
	}


	public void populateDefaultPreferences(SQLiteDatabase database){

		HashMap<String,String> map = new HashMap<String,String>();		
		map.put("FirstTimeLoad", "Yes");


		ContentValues values = new ContentValues();
		for(String key : map.keySet()){
			values.put("key", key);
			values.put("value", map.get(key));
			database.insert("preferences", null, values);
			values.clear();
		}
		
	}
}

