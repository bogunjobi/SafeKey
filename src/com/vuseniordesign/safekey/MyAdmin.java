package com.vuseniordesign.safekey;


import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class MyAdmin extends DeviceAdminReceiver {

	public static boolean forcelock = false;
	public static boolean legalUninstall = false;
	
	public static final String EXTRA_DISABLE_WARNING = "For security purposes, clicking ok immediately locks this device with Safekey's admin password";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		super.onReceive(context, intent);
		
		if (intent.getAction().equals("com.vuseniordesign.safekey.UNINSTALL")){
			Log.d("Uninstall", "Legal");
			legalUninstall= true;
			onDisabled(context, intent);
		}
	}
	
	/** 
	 * This is called when the application is a device administrator
	 */
	public void onEnabled(Context context, Intent intent){
		super.onEnabled(context, intent);
		

		 //DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		 Toast.makeText(context, "Device Admin Enabled", Toast.LENGTH_LONG).show();
		 Log.d("TAG", "onEnabled");
		
	}
	
	
	/**
	 * This is called when the application is no longer a device administrator
	 */

	public void onDisabled(Context context, Intent intent){
		
		//This is added to request a password before the user disables device admin
		
		Toast.makeText(context, "SafeKey Device Admin Disabled", Toast.LENGTH_LONG).show();
		/*Log.d("TAG", "onDisabled");
		new SecureDevice().execute(context);*/
		
		if (!legalUninstall){
			DisableRequest(context);
			Toast.makeText(context, "Locking Device...", Toast.LENGTH_SHORT).show();
		}
		super.onDisabled(context, intent);
		return;
	}
	
	
	/**
	 * This is called when the user disables device administrator
	 */

	public CharSequence onDisableRequested(Context context, Intent intent){
		super.onDisableRequested(context, intent);
		
		if (!legalUninstall)
			return EXTRA_DISABLE_WARNING;
		else
			return null;
		
	}
	
	
	
	private void DisableRequest(Context ctxt){
		DevicePolicyManager mDPM = (DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
    	SharedPreferences auth_pref = ctxt.getSharedPreferences("Login", Context.MODE_PRIVATE);    	
		String pwd = auth_pref.getString("password", "");
    	mDPM.resetPassword(pwd, 1);	
    	forcelock = true;
		mDPM.lockNow();
		return;
	}
	
	
	
	
	
}
