package com.example.safekey;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.EditTextPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	static int can_play;
	static final int PLAY_NOW = 0;
	static final int DONT_PLAY = -1;
	static final String LINK="http://";
	
	static String password;
	static View focusView = null;
	public static Activity activity;
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
		addPreferencesFromResource(R.xml.preferences);
			
		
		// Add  preferences.
		EditTextPreference gen = (EditTextPreference) findPreference("username");
		Log.d("Username1", name);
		if (!name.equals(null)){
			gen.setDefaultValue(name);
			gen.setSummary(name);
			gen.getEditText().setText(name);
						
		} else {
			return;
		}
		//Log.d("Value", gen.getSummary().toString());	
		
		
		PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, false);
		bindPreferenceSummaryToValue(findPreference("sync_frequency"));
		bindPreferenceSummaryToValue(gen);
		
		//first time initialization ONLY
		
		gen.setSummary(name);
		gen.getEditText().setText(name);
		
		
		
		
		
		//Add password preferences
		/*PreferenceCategory pwdHeader = new PreferenceCategory(this);
		pwdHeader.setTitle("Password");
		getPreferenceScreen().addPreference(pwdHeader);
		addPreferencesFromResource(R.xml.pref_password);
		
		PreferenceCategory dhHeader = new PreferenceCategory(this);
		dhHeader.setTitle("Driver History");
		getPreferenceScreen().addPreference(dhHeader);
		addPreferencesFromResource(R.xml.pref_notification);*/
		
		
		Preference pref = findPreference("enableAdmin");
		pref.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		
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
	 	builder.setTitle("Enter Admin Password");
	 	final EditText input = new EditText(SettingsActivity.activity);
	 	input.setHint("Enter Password");
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
	 				wantToCloseDialog = true;
	 				 			
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
		} else if (preference instanceof CheckBoxPreference){
			Log.d("Pref", preference.getKey());
			Log.d("Val", stringValue);
			if (preference.getKey().equals("enableAdmin") && stringValue.equals("true")){
				showDialog((CheckBoxPreference) preference);
			}
		}else {
			
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
				if (preference.getKey().equals("username")){
					SharedPreferences auth_pref = MyApplication.getAppContext().getSharedPreferences("Login", MODE_PRIVATE);
					SharedPreferences.Editor editor = auth_pref.edit();
					editor.putString("name", stringValue);
					Log.d("d Username", stringValue);
					editor.commit();
				}
				
			}
			return true;
		}
	};

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
		super.onBackPressed();
		finish();
	}
	

	
}
