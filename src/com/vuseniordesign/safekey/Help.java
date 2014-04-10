package com.vuseniordesign.safekey;


import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Help extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    addPreferencesFromResource(R.xml.helppref);
	    
	}
	
	

}
