package com.example.safekey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;



public class SplashScreen extends Activity {

	final static int DURATION = 700;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
	}

	@Override
	protected void onResume() {
		super.onResume();
		splashWelcome(DURATION);
	}

	/* Run the splash screen for given time limit */
	protected void splashWelcome(final int limit) {
		
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (waited < limit) {
						sleep(100);
						waited += 100;
					}
				} catch (InterruptedException e) {
					Log.d("SplashScreen Error:", e.getMessage().toString());
				} finally {
					SettingsActivity.can_play= SettingsActivity.DONT_PLAY;
					Intent i=null;
					String pref = getSharedPreferences("Login", MODE_PRIVATE).getString("password", "");
					if (pref == "")
						i = new Intent(SplashScreen.this, LoginActivity.class);
					else
						i = new Intent(SplashScreen.this, Main.class);	 
					startActivity(i);
					finish();	
				}
				
			} 
		};
		splashThread.start();
	}
}
