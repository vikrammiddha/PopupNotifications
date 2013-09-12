package com.bun.popupnotifications;

import android.graphics.drawable.Drawable;

public class ApplicationBean {
	
	private String appName;
	private String packageName;
	private Drawable appIcon;
	private Boolean isSelected;
	private String summaryText;
	private Drawable removeIcon;
	
	
	public String getSummaryText() {
		return summaryText;
	}
	public void setSummaryText(String summaryText) {
		this.summaryText = summaryText;
	}
	public Drawable getRemoveIcon() {
		return removeIcon;
	}
	public void setRemoveIcon(Drawable removeIcon) {
		this.removeIcon = removeIcon;
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
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public Boolean getIsSelected() {
		return isSelected;
	}
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	

}
