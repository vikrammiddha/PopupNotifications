package com.bun.popupnotificationsfree;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import com.bun.popupnotificationsfree.R;

import android.app.Application;

@ReportsCrashes(formKey="",
mailTo = "bunny.decoder@gmail.com",
mode = org.acra.ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
)

public class MyApplication extends Application{
	 @Override
     public void onCreate() {
         super.onCreate();

         // The following line triggers the initialization of ACRA
         ACRA.init(this);
     }
}
