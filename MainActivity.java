package com.zap.chatty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

	SharedPreferences sharedpreferences;
	public static final String pref="MyPref";
	private final int SPLASH_DISPLAY_LENGTH = 2000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedpreferences = getSharedPreferences(MainActivity.pref, 
			      Context.MODE_PRIVATE);
		if (sharedpreferences.contains("id"))
		{
			Intent i = new Intent(this,ChatList.class);
			startActivity(i);
			this.finish();
		}
		
		else
		/* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(MainActivity.this,MainMenu.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
	}
}
