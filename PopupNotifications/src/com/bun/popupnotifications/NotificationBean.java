package com.bun.popupnotifications;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.PendingIntent;
import android.graphics.drawable.Drawable;

public class NotificationBean {
	
	private String appName;
	private String packageName;
	private Drawable icon;
	private String message;
	private PendingIntent pendingIntent;
	private String sender;
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}
	public void setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}


}
