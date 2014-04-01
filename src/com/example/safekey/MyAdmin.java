package com.example.safekey;


import com.example.safekey.LoginActivity.UserLoginTask;

import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MyAdmin extends DeviceAdminReceiver {

	AlertDialog.Builder builder;
	public static final String EXTRA_DISABLE_WARNING = "Disabling Device Admin prevents SafeKey from working as intended. It also locks this device with the admin password";
	Context pubContext;
	Intent pubIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		pubContext = context;
		pubIntent = intent;
		
	 // detect whether disabling is requested?
		String action = intent.getAction();
		if (action == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED)
			DisableRequest(context, intent);
		
		super.onReceive(context, intent);
	 	    
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
		super.onDisabled(context, intent);
		//This is added to request a password before the user disables device admin
		
		Toast.makeText(context, "SafeKey Device Admin Disabled", Toast.LENGTH_LONG).show();
		Log.d("TAG", "onDisabled");
		new SecureDevice().execute(context);
	}
	
	
	/**
	 * This is called when the user disables device administrator
	 */

	public CharSequence onDisableRequested(Context context, Intent intent){
		super.onDisableRequested(context, intent);
		
        return EXTRA_DISABLE_WARNING;
		
	}
	
	public void DisableRequest(Context context, Intent intent){
		Toast.makeText(context, "Device Admin Disable Requested", Toast.LENGTH_LONG).show();
		Log.d("TAG", "onDisableRequest");
		//Intent it = new Intent(context, DADisabled.class);
		
		/*Intent it = new Intent(context, ShowDialog.class);
        //it.setClassName(context, "ShowDialog.class");       
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it); */
        new DisableRequestTask().execute(context);
        //TODO: Make dialog box show up before the device locks
	}
	
	private class SecureDevice extends AsyncTask<Context, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Context... arg0) {
			try {
				// Simulate network access.
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				return null;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {

		}	

	}	
	
	private class DisableRequestTask extends AsyncTask<Context, Void, Void> {
		DevicePolicyManager mDPM;
		Context ctxt;
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Context... context) {
			ctxt = context[0];
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			mDPM = (DevicePolicyManager)context[0].getSystemService(Context.DEVICE_POLICY_SERVICE);
	    	SharedPreferences auth_pref = context[0].getSharedPreferences("Login", Context.MODE_PRIVATE);
	    	
			String pwd = auth_pref.getString("password", "");
	    	mDPM.resetPassword(pwd, 1);
	    	
	        
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			/*Intent intent=new Intent(ctxt, ShowDialog.class);
			//intent.setClassName(MyApplication.getAppContext(), "ShowDialog.class");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctxt.startActivity(intent); */
			mDPM.lockNow();

		}	

	}	
	
	
	
	
}
