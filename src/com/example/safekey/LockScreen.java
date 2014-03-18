package com.example.safekey;

import android.app.Activity;
import android.app.WallpaperManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;



public class LockScreen extends Activity {
	
	
    // Interaction with the DevicePolicyManager
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;
    Context context;
    int ringerMode;
    Drawable lsImage;
    WallpaperManager wm;
    Button bn911, bn, bn1;
    TextView emCall;
    String contact1, contact2, number1, number2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		SharedPreferences sp = this.getSharedPreferences("LockScreen", MODE_PRIVATE);
		String imgURI = sp.getString("img", "");
		Log.d("ImgURI", imgURI);
		if (!imgURI.equals("")){
			 Bitmap btmap = BitmapFactory.decodeFile(imgURI);
	         lsImage = new BitmapDrawable(getResources(), btmap);
	         getWindow().setBackgroundDrawable(lsImage);
		} else {
			//
			getWindow().setBackgroundDrawableResource(R.drawable.screenshot);
		}
		//load pic to use 
		
		
		
		setContentView(R.layout.lock_screen);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
		
	
		context = this;
		
		sp = this.getSharedPreferences("Contacts", MODE_PRIVATE);
		contact1 = sp.getString("contact1", "");
		contact2 = sp.getString("contact2", "");
		number1 = sp.getString("number1", "");
		number2 = sp.getString("number2", "");
		
		/*Spinner spn = (Spinner) findViewById(R.id.lsSpinner);
		ArrayAdapter <String> adapter =
				  new ArrayAdapter <String> (this, android.R.layout.simple_spinner_item ); */
		
		
		
		bn911 = (Button)findViewById(R.id.emergency1);
		bn = (Button)findViewById(R.id.emergency2);
		bn1 = (Button)findViewById(R.id.emergency3);
		
		
		bn911.setOnClickListener(listener);
		bn.setOnClickListener(listener);
		bn1.setOnClickListener(listener);
		
		
		
		
		
		
		emCall = (TextView) findViewById(R.id.emCall);
		emCall.setOnClickListener(listener);
		
		if (isActiveAdmin()){
			//mDPM.lockNow();
		 	AudioManager audiomanager =(AudioManager)LockScreen.this.getSystemService(Context.AUDIO_SERVICE);
		 	ringerMode = audiomanager.getRingerMode();
	        audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);    
		}
	        
	}
	
	private void buttonVisibility() {
		// TODO Auto-generated method stub
		bn911.setVisibility(View.VISIBLE);
		
		if (!(contact1.equals("") || number1.equals(""))){
			bn.setText(contact1);
			bn.setVisibility(View.VISIBLE);
			
			//adapter.add(contact1);
		}
		Log.d("CONTACT2", number2);
		if (!(contact2.equals("") || number2.equals(""))){
			bn1.setText(contact2);
			bn1.setVisibility(View.VISIBLE);
			
			//adapter.add(contact2);
		}
	}

	/**
     * Helper to determine if we are an active admin
     */
    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    OnClickListener listener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.emCall){
				if (emCall.getText().toString().equals("Cancel")){
					emCall.setText("Emergency Call");
					emCall.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call, 0, 0, 0);
					bn911.setVisibility(View.INVISIBLE);
					bn.setVisibility(View.GONE);
					bn1.setVisibility(View.GONE);
				} else {
				emCall.setText("Cancel");
				emCall.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_, 0, 0, 0);
				buttonVisibility();
				}
			} else {
			Intent intent = new Intent(Intent.ACTION_CALL);			
			switch(v.getId()){
			case(R.id.emergency1):
				intent.setData(Uri.parse("tel:" + "911"));
				Log.d("TAG", bn911.getText().toString().trim());
				//context.startActivity(intent);
				break;
			case(R.id.emergency2):
				intent.setData(Uri.parse("tel:" + number1.replaceAll("\\D", "")));
			Log.d("TAG1", number1);
				break;
			case(R.id.emergency3):
				intent.setData(Uri.parse("tel:" + number2.replaceAll("\\D", "")));
			Log.d("TAG2", number2);
				
				break;
			default:
				break;
				
			
			}
			startActivity(intent);
		}	
		}
	};
	
	/*OnItemSelectedListener spnListener = new OnItemSelectedListener(){
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	        // An item was selected. You can retrieve the selected item using
			Intent intent = new Intent(Intent.ACTION_CALL);			
			if (parent.getItemAtPosition(pos).equals(contact1)){
				intent.setData(Uri.parse("tel:" + number1.replaceAll("\\D", "")));
	    } else if (parent.getItemAtPosition(pos).equals(contact2)){
			intent.setData(Uri.parse("tel:" + number2.replaceAll("\\D", "")));
	    }
		}
		
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}

	};*/
			
	

}
