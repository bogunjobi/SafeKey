package com.example.safekey;

import java.security.Timestamp;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenListener extends BroadcastReceiver {
	int userBehaviorTracker;
	long start;
	double duration;
	Date currentDate;
	
	//TODO - CREATE SQL LITE DB
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
	
		if (arg1.getAction().equals(Intent.ACTION_SCREEN_ON)){
			userBehaviorTracker++;
		} //once screen is unlocked and app screenlock is off
		else if (arg1.getAction().equals("my.action.string")){
			String state = arg1.getStringExtra("flag");
			
			if (state.equals("LOCKSCREEN_DISABLED")){
			//store duration, userbehaviortracker and date in sqllite db and refresh the counter
				
			duration = (System.nanoTime() - start) / 1000000000 ;  //convert to seconds;
			userBehaviorTracker = 0;
			
			} else if (state.equals("LOCKSCREEN_ENABLED")){
				start = System.nanoTime();
				currentDate = new Date();
			}
				
		}
		
	}

}
