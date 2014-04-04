package com.example.safekey;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;



public class LockScreen extends Activity {
	
	private HomeLocker mHomeKeyLocker;
	
	final String TAG = "LockScreen"; 
    // Interaction with the DevicePolicyManager
	AudioManager audiomanager;
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;
    Context context;
    int ringerMode, musicVolume, miscVolume, notifications,currentVolume;
    
    //data collection
    static int userTracker = 0;
    static long start;
    static String currentDate;
    
    public static boolean enabled = true;
    Drawable lsImage;
    WallpaperManager wm;
    Button bn911, bn, bn1;
    //TextView emCall;
    String contact1, contact2, number1, number2;
    
    DB userDB = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		enabled = true;
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);	
		
		registerReceiver(broadcastReceiver, filter);
		
		userDB = new DB(this);
		
		if (Build.VERSION.SDK_INT >= 14){
			View decor = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		            |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 
					|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;				
				
			if (Build.VERSION.SDK_INT >= 19)
				uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decor.setSystemUiVisibility(uiOptions);
			
		}
		
		
		//if (Build.VERSION.SDK_INT < 16){
		getWindow().addFlags(//WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_FULLSCREEN |
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
	            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
	            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|
	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	           
	            
	         );    
		
		//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
		
		/*getWindow().getDecorView().setSystemUiVisibility(8);*/
		//}
		
		//data collection
		
		start = System.nanoTime();
		SimpleDateFormat cDate = new SimpleDateFormat("MM:dd:yyyy hh:mm:ss");
		currentDate = cDate.format(new Date());
		
		//Intent broadcast = new Intent("com.example.safekey.LOCKSCREEN_ENABLED");
		//sendBroadcast(broadcast);
		
		
		
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
		 
		
		//disableBars(this);
		setContentView(R.layout.lock_screen);
		mHomeKeyLocker = new HomeLocker();
		mHomeKeyLocker.lock(this);
		
		//WindowManager.LayoutParams param = new WindowManager.
		
		/*if (Build.VERSION.SDK_INT >= 16){ 
			 View decorView = getWindow().getDecorView();
			    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | 
			    		View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			    		
			    decorView.setSystemUiVisibility(uiOptions);
			    this.getActionBar().hide();
			    
		}*/
		
		
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
		
	
		context = this;
		
		muteDeviceStreams(true);
		
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
		
		
		
		final TextView emCall = (TextView)findViewById(R.id.emCall);
		emCall.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Log.d("Touch", "Don't touch me there");
				return false;
			}
			
		});
		
		emCall.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Log.d("Click em call", "click");
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
			
			}
		}); 
		
		if (isActiveAdmin()){
			
			//lock the screen after 3 seconds
			new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDPM.lockNow();
                }
            }, 3000);	
	        
		}
	        
	}
	
	
	/*@Override
    protected void onStop() 
    {
        
        Log.d("tag", "MYonStop is called");
        // insert here your instructions
    }*/
	
	private void muteDeviceStreams(boolean bool){
		audiomanager =(AudioManager)LockScreen.this.getSystemService(Context.AUDIO_SERVICE);
	 	audiomanager.setStreamMute(AudioManager.STREAM_ALARM, bool);
	 	audiomanager.setStreamMute(AudioManager.STREAM_MUSIC, bool);
		audiomanager.setStreamMute(AudioManager.STREAM_RING, bool);
		audiomanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, bool);
		audiomanager.setStreamMute(AudioManager.STREAM_SYSTEM, bool);
	}
	
		
	private int getNavDim(){
		int value = 0;
		int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			value = getResources().getDimensionPixelSize(resourceId);
		}
	Log.d("Height", Integer.toString(value));
	return value;
	}
	
	private void disableBars(Context context){
		View disableBar = new View(context);
		ViewGroup rl = (ViewGroup)getWindow().getDecorView();
		//WindowManager wm = (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);
		WindowManager.LayoutParams handleParams = new WindowManager.LayoutParams(-1, -1, 0, -getNavDim(),
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
			    |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			    |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			    |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR 
			    |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.OPAQUE);
				//handleParams.gravity(Gravity.BOTTOM);
				//handleParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
			    
			    getWindowManager().addView(disableBar, handleParams);
		
		/*WindowManager.LayoutParams w = new WindowManager.LayoutParams(    
				
			 	getWindow().addContentView(disableBar, w); */
	}
	
	
	@Override
	protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        enabled = false;
        if (!this.isFinishing()){
            Log.d("Noooo", "GET TO DA CHOPAAAA!");
            /*ActivityManager am = new ActivityManager();
           moveTaskToFront (new ActivityManager.RunningTaskInfo().id, ActivityManager.MOVE_TASK_NO_USER_ACTION);*/
            //startActivity(getIntent());
            
            //check if user unnlocks screen
        }
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
              if (!powerManager.isScreenOn()) {
              Log.d("Lockscreen", "Screen On");
              Intent broadcast = new Intent("com.example.safekey.SCREEN_ON");
          	  sendBroadcast(broadcast);
                
            }
     
        
       
     }
	
	@Override
	public void onResume() {
		super.onResume();
		enabled = true;
	    registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
	}
	
	@Override
	protected void onDestroy() {
	  super.onDestroy();
	  //audiomanager.setRingerMode(currentVolume);
	  Log.d(TAG, "Goodbye cruel world");
	  enabled = false;
	  unregisterReceiver(broadcastReceiver);
	  mHomeKeyLocker.unlock();
      mHomeKeyLocker = null;
      
      long duration = (long)(System.nanoTime() - start) / 1000000000 ;  //convert to seconds;
      Log.d(TAG, Long.toString(duration));
      Log.d(TAG, currentDate);
	  userDB.add(currentDate, duration, userTracker);
      
      //Intent broadcast = new Intent("com.example.safekey.LOCKSCREEN_DISABLED");
	  //sendBroadcast(broadcast);
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
		
	};
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
	        	enabled = false;
	        	muteDeviceStreams(false);
	            finish();
	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	            userTracker++;
	            Log.d(TAG, "plus one");
	        }
	    }
	
	};
	
	
	
	
	
	
	
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);

	   
	    Log.d("Focus debug", "Focus changed!");
	    
	    
	if (!hasFocus){
		//intent.putExtra("flag", "LOCKSCREEN_ENABLED"); 
	
	    Log.d("Focus debug", "Lost focus!");   
	    	Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	        sendBroadcast(closeDialog);
	        
	        
	        //Intent i = getIntent();
	        //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	        //finish();
	        //overridePendingTransition(0, 0);

	        //startActivity(i);
	       // overridePendingTransition(0, 0);
	        
	    }
	    
	    
	}
	
	
	
	
  /*public void onBackPressed() {
	        // Don't allow back to dismiss.
	        return;
	    }*/
	
  
	  

}
