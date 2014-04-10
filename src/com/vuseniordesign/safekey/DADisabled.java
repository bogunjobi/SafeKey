package com.vuseniordesign.safekey;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class DADisabled extends Activity {

	DevicePolicyManager mDPM;
	ComponentName mDeviceAdminSample;
	AlertDialog.Builder alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
    	mDeviceAdminSample = new ComponentName(DADisabled.this, MyAdmin.class);
        
    	Log.d("DA", "In DA create");
    	showDialog(this);
    	alertDialog.show();
	}
	
	
	
	
	private void showDialog(Context context){
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.create();
		alertDialog.setTitle("Warning!");
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.setMessage("Device Admin has been disabled. This may cause SafeKey to perform unexpectedly.");
		alertDialog.setPositiveButton("Enable Device Admin", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//??
				Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
		        Log.d("Adminis", "Activate");
				startActivityForResult(i, 0);
		        //startActivity(i);
				//??
				
			}
		});
        
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode == RESULT_CANCELED) {
				alertDialog.show();
			 
		        //return;
		    } else {
		    	mDPM.resetPassword("", 0);
		    	
		    }
		}	
	
}
