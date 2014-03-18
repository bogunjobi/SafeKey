package com.example.safekey;


import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MyAdmin extends DeviceAdminReceiver {

	AlertDialog.Builder builder;
	public static final String EXTRA_DISABLE_WARNING = "Disabling Device Admin prevents SafeKey from working as intended";
	Context pubContext;
	Intent pubIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		pubContext = context;
		pubIntent = intent;
		
	 // detect whether disabling is requested?
		/**String action = intent.getAction();
		if (action == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED){
			disableRequest(context, intent);
		}else **/
		super.onReceive(context, intent);
	 	    
	}
	
	/** 
	 * This is called when the application is a device administrator
	 */
	public void onEnabled(Context context, Intent intent){
		super.onEnabled(context, intent);
		//mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE)
		 Toast.makeText(context, "Device Admin Enabled", Toast.LENGTH_LONG).show();
		 Log.d("TAG", "onEnabled");
		
	}
	
	
	/**
	 * This is called when the application is no longer a device administrator
	 */

	public void onDisabled(Context context, Intent intent){
		super.onDisabled(context, intent);
		Toast.makeText(context, "SafeKey Device Admin Disabled", Toast.LENGTH_LONG).show();
		Log.d("TAG", "onDisabled");
	}
	
	
	/**
	 * This is called when the user disables device administrator
	 */

	public CharSequence onDisableRequested(Context context, Intent intent){
		super.onDisabled(context, intent);
		Toast.makeText(context, "Device Admin Disable Requested", Toast.LENGTH_LONG).show();
		Log.d("TAG", "onDisableRequest");
		
		//This is added to request a password before the user disables device admin
		Intent it = new Intent(context, ShowDialog.class);
        //it.setClassName(context, "ShowDialog.class");       
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
		
		return EXTRA_DISABLE_WARNING;
		
	}
	
	
	
	
	
	
}
