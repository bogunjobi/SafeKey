package com.vuseniordesign.safekey;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.EditTextPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;



/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	
	static String contact1;
	static String contact2;
	static String number1;
	static String number2;
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	static int can_play;
	static final int PLAY_NOW = 0;
	static final int DONT_PLAY = -1;
	
	static boolean timerfinished = false;
	
	static String password;
	static View focusView = null;
	public static Activity activity;
	
	static CheckBoxPreference pref, missedCalls;
	static ListPreference message, reply;
	static SharedPreferences admin, settings;
	private static CountDownTimer cdt;
	
	static SharedPreferences.Editor editor;
	public static SharedPreferences getSharedPreferences(Context ctxt){
		return ctxt.getSharedPreferences("Login", Context.MODE_PRIVATE);
	}
	
	
    
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		
		activity = this;		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    setupSimplePreferencesScreen();
	    
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("Settings", "Stopped");
		if (!timerfinished && cdt != null){
			cdt.cancel();
			cdt.onFinish();
		}
		
	    
	}
	

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.
		SharedPreferences auth_pref = SettingsActivity.getSharedPreferences(SettingsActivity.this.getApplicationContext());
		String name = auth_pref.getString("name", null);
		password = auth_pref.getString("password", null);	
		
		admin = activity.getSharedPreferences("admin", Context.MODE_PRIVATE);
		
		
		SharedPreferences contacts = getSharedPreferences("Contacts", Context.MODE_PRIVATE);
		contact1 = contacts.getString("contact1", "");
		contact2 = contacts.getString("contact2", "");
		number1 = contacts.getString("number1", "");
		number2 = contacts.getString("number2", "");
		
		addPreferencesFromResource(R.xml.preferences);
			
		PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, false);
		
		
		// Add  preferences.
		EditTextPreference gen = (EditTextPreference) findPreference("username");
		Log.d("Username1", name);
		if (!name.equals(null)){
			gen.setDefaultValue(name);
			gen.setSummary(name);
			
			gen.getEditText().setText(name);
			//gen.getEditText().
			Log.d("EditText Value", gen.getEditText().getText().toString());
						
		} else {
			return;
		}
		//Log.d("Value", gen.getSummary().toString());	
		
		
		
		
		message = (ListPreference)findPreference("textreply");
		bindPreferenceSummaryToValue(message);
		
		missedCalls = (CheckBoxPreference) findPreference("missedcalls");
		missedCalls.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		
		reply = (ListPreference)findPreference("sendto");
		bindPreferenceSummaryToValue(reply);
		
		
		//first time initialization ONLY
		
		bindPreferenceSummaryToValue(gen);
		gen.setSummary(name);
		gen.getEditText().setText(name);
		
		
		
		
		
		pref = (CheckBoxPreference) findPreference("enableAdmin");
		pref.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		
		
		Preference ls_pref = findPreference("LockscreenImage");
		ls_pref.setOnPreferenceClickListener(onPreferenceClick);
		
		Preference uninstall = findPreference("uninstall");
		uninstall.setOnPreferenceClickListener(onPreferenceClick);
		
		Preference help = findPreference("help");
		help.setOnPreferenceClickListener(onPreferenceClick);
		
		
		Preference demo = findPreference("Lockscreen");
		demo.setOnPreferenceClickListener(l);
		
		Preference data = findPreference("loaddata");
		data.setOnPreferenceClickListener(l);
		
		
		Log.d("EditText Value2", gen.getEditText().getText().toString());
	}

	
	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) 
					>= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}
	

	private static void showDialog(final CheckBoxPreference preference){
		AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.activity);
		builder.setCancelable(false);
	 	builder.setTitle("Enter Admin Password");
	 	
	 	
	 	final EditText input = new EditText(SettingsActivity.activity);
	 	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

	 	builder.setView(input);
	 	
	 	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	 		public void onClick(DialogInterface dialog, int id) { } }); 
	 	
	 	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	 		public void onClick(DialogInterface dialog, int id) {
	 			preference.setChecked(false);
	 			dialog.cancel();
	 		}
	 	});
	 	final AlertDialog dialog = builder.create();
	 	dialog.show();
	 	dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {            
            @Override
            public void onClick(View v)
            {

	 			Boolean wantToCloseDialog = false;
	 			Log.d("String", input.getText().toString());
	 			Log.d("Passwd", password);
	 			if (input.getText().toString().equals(password)){
	 				preference.setChecked(true);
	 				editor = admin.edit();
	 				editor.putBoolean("adminEnabled", true);
	 				editor.commit();
	 				wantToCloseDialog = true;
	 				final NumberFormat formatter = new DecimalFormat("00");
	 				cdt = new CountDownTimer(180000, 1000){
	 					@Override
	 					public void onFinish() {
	 						timerfinished = true;
	 						pref.setSummary("Disabling Admin...");
	 						pref.setChecked(false);
	 						pref.setSummary("");
	 						editor = admin.edit();
	 		 				editor.putBoolean("adminEnabled", false);
	 		 				editor.commit();
	 					}

	 					@Override
	 					public void onTick(long millisUntilFinished) {
	 						pref.setSummary("Admin will be disabled in: " + millisUntilFinished / 60000 + ":" + formatter.format(Math.round(millisUntilFinished % 60000)/1000));
	 					}
	 				}.start(); 				
	 				
	 				
	 				 			
	 			}else{
	 				Log.d("Stringy", input.getText().toString());
	 				input.setError("Incorrect Password");
	 				focusView = input;
	 				focusView.requestFocus();
	 				preference.setChecked(false); 
	 				
	 				
	 			}	
            	       
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
	}
	/*private Preference.OnPreferenceClickListener listener = new OnPreferenceClickListener() {

	    public boolean onPreferenceClick(final Preference preference) {
	    	//boolean success = true;
	    	if (preference.getKey().equals("enableAdmin") && !preference.isEnabled()){
	    	 	AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
	    	 	builder.setMessage("Enter Admin Password");
	    	 	final EditText input = new EditText(preference.getContext());
	    	 	builder.setView(input);
	    	 	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	 		public void onClick(DialogInterface dialog, int id) {
	    	 			if (input.getText().toString().equals("password")){
	    	 				dialog.cancel();
	    	 				
	    	 			}else{
	    	 				
	    	 				input.setError("Enter new password again");
	    	 				focusView = input;
	    	 				preference.setEnabled(false);
	    	 				
	    	 				
	    	 			}
	    			 
	    	 		}
	    	 	}); 
	    	}
	    	return true;
	    }
	
		};*/
	
	
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(final Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);
				settings = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
				
				if (preference.getKey().equals("sendto")){
					editor = settings.edit();
	 				editor.putString("replyto", (String) ((ListPreference) preference).getEntry());	
	 				editor.commit();
				} else if (preference.getKey().equals("textreply")){
					editor = settings.edit();
	 				editor.putString("textmessage", (String) ((ListPreference) preference).getEntry());
	 				editor.commit();
				}
				
		} else if (preference instanceof CheckBoxPreference){
			Log.d("Pref", preference.getKey());
			Log.d("Val", stringValue);
			if (preference.getKey().equals("enableAdmin")){
				if (stringValue.equals("true")){
				showDialog((CheckBoxPreference) preference);				
				} else if (stringValue.equals("false")){
				if (!timerfinished && cdt != null){
					cdt.cancel();
					//cdt.onFinish();
				}
			}
				preference.setSummary("");
				editor = admin.edit();
	 			editor.putBoolean("adminEnabled", false);	 			
			} else if (preference.getKey().equals("missedcalls")){
				settings = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
				editor = settings.edit();
				if (stringValue.equals("true"))
						editor.putBoolean("missedcalls", true);	 
				else editor.putBoolean("missedcalls", false);
				
			}
			editor.commit();
		} 
		else if (preference instanceof EditTextPreference) {
			
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
				if (preference.getKey().equals("username")){
					SharedPreferences auth_pref = activity.getSharedPreferences("Login", MODE_PRIVATE);
					SharedPreferences.Editor editor = auth_pref.edit();
					editor.putString("name", stringValue);
					Log.d("Username", stringValue);
					editor.commit();
		}else {
			/*if (preference.getKey().equals("LockScreenImage")){
				SharedPreferences sp = activity.getSharedPreferences("LockScreen", MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("img", stringValue);
				Log.d("Pic", stringValue);
				editor.commit();
			}*/
		}
				
	}
			return true;
		}
		
	};
	
	
	OnPreferenceClickListener onPreferenceClick = new Preference.OnPreferenceClickListener() {
	       public boolean onPreferenceClick(Preference preference) {
	    	   if (preference.getKey().equals("LockscreenImage")){
	    		   Intent intent = new Intent();
	    		   intent.setType("image/*");
	    		   intent.setAction(Intent.ACTION_PICK);
	    		   startActivityForResult(Intent.createChooser(intent, "Select Image"),0);
	    		   return true;
	    	   } else if (preference.getKey().equals("uninstall")){
	    		   
	    		   uninstallApp newTask = new uninstallApp();
	   				newTask.execute((Void) null);
	    		   return true;
	    	   } else if(preference.getKey().equals("help")){
	    		   Intent intent = new Intent(SettingsActivity.this, Help.class);
	    		   startActivity(intent);
	    		   return true;
	    	   }
	    		   /*DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
	    		   ComponentName adminReceiver = new ComponentName(SettingsActivity.this, MyAdmin.class);
	    		   //mDPM.resetPassword("", 0);
	    		   
	    		   Intent i = new Intent();
	    		   i.setAction("com.vuseniordesign.safekey.UNINSTALL");	    		   
	    		   sendBroadcast(i);
	    		   
	    		   //MyAdmin.legalUninstall = true;
	    		   
	    		   //mDPM.removeActiveAdmin(adminReceiver);
	    		   
	    		   //wait for device admin to be deactivated.
	    		   //Warning: this is very inefficient
	    		   while (mDPM.isAdminActive(adminReceiver));
	    		   
	    		   Uri uri = Uri.fromParts("package", getPackageName(), null);
	    		   startActivity(new Intent(Intent.ACTION_DELETE, uri));
	    		   return true;
	    	   } else if (preference.getKey().equals("contact1")){
	    		   
	    		   return true;
	    	   }*/
	    	   return false;
	       }
	   };
	
	   OnPreferenceClickListener l = new Preference.OnPreferenceClickListener() {
	       public boolean onPreferenceClick(Preference preference) {
	    	   if (preference.getKey().equals("Lockscreen")){
	  
	    	   Intent i = new Intent(SettingsActivity.this, LockScreen.class);
	    	   i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	           i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	           i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			   startActivity(i);
			return true;		
	    	   } else if (preference.getKey().equals("loaddata")) {
	    		   startService(new Intent(SettingsActivity.this, LoadData.class ));
	    		   Toast.makeText(SettingsActivity.this, "Loading Data...", Toast.LENGTH_SHORT).show();
	    		   return true;
	    	   }
	    	   return false;
	    	   
			}
		};
				
		
	   
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			
			if (resultCode == RESULT_OK && data != null) {
			         Uri selectedImage = data.getData();
			         String[] filePathColumn = {MediaStore.Images.Media.DATA};
			         Cursor cursor = getContentResolver().query(selectedImage,
			                 filePathColumn, null, null, null);
			         cursor.moveToFirst();
			         int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			         String picturePath = cursor.getString(columnIndex);
			         cursor.close();
			       
			         //store chosen image
			         SharedPreferences ls_img = getSharedPreferences("LockScreen", Context.MODE_PRIVATE);
			         SharedPreferences.Editor editor = ls_img.edit();
			 		 editor.putString("img", picturePath);
			 		 editor.commit();
			 		 
			        /*Bitmap btmap = BitmapFactory.bitmapFactory.decodeFile(picturePath);
			         ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			         imageView.setImageBitmap(btmap);*/
				}
			}
	   
	   
	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		Log.d("Val", PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),
						"").toString());
		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),
						"")); 
		Log.d("Summary", preference.getSummary().toString());
	}

	
	
	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("username"));
			
		}
	}

	
	@Override
	public void onBackPressed() {
		//to ensure that users cannot go back to the setup page
		//super.onBackPressed();
		startActivity(new Intent(this, Main.class));
		finish();
	}
	
	
	public class MyCount extends CountDownTimer {
		public MyCount(long startTime, long interval) {
			super(startTime, interval);
		}
		
		@Override
        public void onFinish() {
        	pref.setSummary("Disabling Admin...");
        	pref.setChecked(false);
       }

        @Override
        public void onTick(long millisUntilFinished) {
            pref.setSummary("Left: " + millisUntilFinished / 1000);
        }
    }
	
	
	class uninstallApp extends AsyncTask<Void, Void, Boolean>
	{
		 DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		   ComponentName adminReceiver = new ComponentName(SettingsActivity.this, MyAdmin.class);
		   //mDPM.resetPassword("", 0);
		   
		  
		   
		   //MyAdmin.legalUninstall = true;
		   
		   //mDPM.removeActiveAdmin(adminReceiver);
		   
		   //wait for device admin to be deactivated.
		   //Warning: this is very inefficient
		   /*while (mDPM.isAdminActive(adminReceiver));
		   
		   Uri uri = Uri.fromParts("package", getPackageName(), null);
		   startActivity(new Intent(Intent.ACTION_DELETE, uri));
		   return true;

	    // runs on the UI thread */
	    @Override 
	    protected void onPreExecute() {
	    	  Intent i = new Intent();
			   i.setAction("com.vuseniordesign.safekey.UNINSTALL");	    		   
			   sendBroadcast(i);
			   
			   
			   //while (mDPM.isAdminActive(adminReceiver));
	        }
		@Override
		protected Boolean doInBackground(Void... params) {
			if (mDPM.isAdminActive(adminReceiver))
				   mDPM.removeActiveAdmin(adminReceiver);
			while (mDPM.isAdminActive(adminReceiver));
			return true;
		}
		
		/*@Override
		void onProgressUpdate(){
			Toast.makeText(SettingsActivity.this, "Please wait...", Toast.LENGTH_LONG).show();
		}*/
	    
	    // runs on the UI thread
	    @Override 
	    protected void onPostExecute(Boolean b) {
	    	
	    	   Uri uri = Uri.fromParts("package", getPackageName(), null);
			   startActivity(new Intent(Intent.ACTION_DELETE, uri));
			   
	    }

	}
	  
		  
	
	
	
	
}
