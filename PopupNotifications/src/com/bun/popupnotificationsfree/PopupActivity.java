package com.bun.popupnotificationsfree;

import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class PopupActivity extends NotificationActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState){
                window = getWindow();
                super.onCreate(savedInstanceState);        

                myKeyGuard = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);

                myLock = myKeyGuard.newKeyguardLock(KEYGUARD_SERVICE);

                window.addFlags(
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                ); 

                LinearLayout ll1 = (LinearLayout) findViewById(R.id.mainLayoutId);

                ll1.setBackgroundColor(Color.BLACK);
                
                ll1.getBackground().setAlpha(200);
        }

}