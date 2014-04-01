package com.example.safekey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class DADisabled extends Activity {

	DevicePolicyManager mDPM;
	ComponentName mDeviceAdminSample;
	AlertDialog.Builder alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
    	mDeviceAdminSample = new ComponentName(DADisabled.this, MyAdmin.class);
    	SharedPreferences auth_pref = this.getSharedPreferences("Login", MODE_PRIVATE);
		String pwd = auth_pref.getString("password", "");
    	mDPM.resetPassword(pwd, 1);
    	mDPM.lockNow();
	}
	
	
	protected void onResume(Bundle savedInstanceState){
		showDialog(this);
    	/*Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
    	Log.d("Admin", "Activate");
    	startActivityForResult(i, 0);
		//setContentView(R.layout.activity_dadisabled); */
		finish();

		
	}
	
	protected void startActivityForResult(int requestCode, int resultCode, Intent data){
		if (resultCode == RESULT_CANCELED) {
			alertDialog.show();
	        return;
	    } else {
	    	Intent intent = new Intent(this, Main.class);
			startActivity(intent);
	    }
	}
	
	private void showDialog(Context context){
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.create();
		alertDialog.setTitle("Warning!");
		alertDialog.setIcon(R.drawable.ic_action_warning);
		alertDialog.setMessage("Enabling Device Admin ensures that this app works as intended");
		alertDialog.setPositiveButton("Enable Device Admin", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//??
				Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
		        Log.d("Admin", "Activate");
				startActivityForResult(i, 3);
		        //startActivity(i);
				//??
				
			}
		});
        
	}

	
}
