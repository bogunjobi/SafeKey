package com.vuseniordesign.safekey;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BTReceiver extends BroadcastReceiver {

	private static final String CONNECTION_SUCCESSFUL = "com.vuseniordesign.safekey.CONNECTION_ESTABLISHED";
	private static final String CONNECTION_DISABLED = "com.vuseniordesign.safekey.CONNECTION_DISABLED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		Log.d("In Receiver", action);
       /* if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
            //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d("Connection! Status", "Device Connected");
            //Time to lock device & start lockscreen
            
            Intent i = new Intent(context, LockScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            
        }else*/
		if (action.equals(CONNECTION_SUCCESSFUL)){
            Log.d("Connection Successful", "Device Connected");
            //Time to lock device & start lockscreen
            
            Intent i = new Intent(context, LockScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            
        }else if (action.equals(CONNECTION_DISABLED)){
            Log.d("Connection Successful", "Device Disonnected");
            //Time to lock device & start lockscreen
            
            Intent i = new Intent(context, LockScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
        	scheduleAlarms(context);
        	Log.d("Action state changed", "Device Connected");
        } 

	}
	
	
	private static void scheduleAlarms(Context ctxt) {
	    AlarmManager mgr=
	        (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
	    Intent i = new Intent(ctxt, BTConnection.class);
	    PendingIntent pi = PendingIntent.getService(ctxt, 0, i, 0);

	    mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
	                     SystemClock.elapsedRealtime() + 30000, 30000, pi);
	  }

}
