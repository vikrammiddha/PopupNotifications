package com.bun.popupnotificationsfree;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

public class FeedbackActivity extends Activity{
	
	AlertDialog.Builder alertDialog2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);	

		setContentView(R.layout.feedback_activity);
		
		setFinishOnTouchOutside(false);

		//SharedPreferenceUtils.setNotCount(ctx, SharedPreferenceUtils.getNotCount(ctx) + 1);

		if(HelperUtils.showFeedback(this, -1)){
			Feedback.initiateFeedback(this, this);
		}	

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		finish();
	}
}
