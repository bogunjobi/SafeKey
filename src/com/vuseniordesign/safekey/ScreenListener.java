package com.vuseniordesign.safekey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenListener extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (arg1.getAction().equals(Intent.ACTION_USER_PRESENT)){
			if (MyAdmin.forcelock){
				//reset password
				Log.d("ScreenListener", "Listening");
		    	Intent myintent = new Intent(arg0, DADisabled.class);
	            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
	            		|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
	            		);
	            myintent.addCategory(Intent.CATEGORY_HOME);
	            arg0.startActivity(myintent);
			}
		}
		
	}

}
