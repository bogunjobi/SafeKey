package com.example.safekey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SK_DialogPreference extends DialogPreference implements DialogInterface.OnClickListener  {

	SharedPreferences login;
	static String pwrd = null;
	static String newpwrd = null;
	static String newpwrd1 = null;
	EditText pwd = null;
	EditText newpwd = null;
	EditText newpwd1 = null;
	View focusView = null;
	Boolean cancel = false;
	
	 
	public SK_DialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTitle("Change Admin Password");
		setDialogLayoutResource(R.layout.sk_dialogpref);		
	}
	
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		 builder.setTitle("Change Password");
		 builder.setPositiveButton("OK", this);
         builder.setNegativeButton("Cancel", this);
         super.onPrepareDialogBuilder(builder);
		
	}
			
	@Override
	protected void onBindDialogView(View view) {
	      super.onBindDialogView(view);
		        // view is your layout expanded and added to the dialog
		            // find and hang on to your views here, add click listeners etc
		            // basically things you would do in onCreate
	      pwd = (EditText)view.findViewById(R.id.oldpwd);
		  newpwd = (EditText)view.findViewById(R.id.newpwd);
		  newpwd1 = (EditText)view.findViewById(R.id.newpwd1);
		  pwrd = pwd.getText().toString();
		  newpwrd = newpwd.getText().toString();
		  newpwrd1 = newpwd1.getText().toString();
		  
		  Log.v("Password", pwrd);
		
		
	
		}
	
	public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE){
        	dialog.cancel();
        } else {
        	if (cancel) {
				// There was an error; don't attempt login and focus the first
				// form field with an error.
        		focusView.requestFocus();
        	}else {
        		if (TextUtils.isEmpty(pwrd)) {
        			pwd.setError("Enter old password");
        			focusView = pwd;
        			cancel = true;
        		} else if (TextUtils.isEmpty(newpwrd)){
        			pwd.setError("Enter new password");
        			focusView = newpwd;
        			cancel = true;
        		} else if(newpwrd.length() < 6) {
        			newpwd.setError("Password is too short");
        			focusView = newpwd;
        			cancel = true;
        		} else if (TextUtils.isEmpty(newpwrd1)){
        			pwd.setError("Enter new password again");
        			focusView = newpwd1;
        			cancel = true;
        	     }else if (!newpwrd.equals(newpwrd1)){
        			pwd.setError("Passwords do not match");
        			focusView = newpwd1;
        			cancel = true;
        	     }
        	
        	}
        }
    }
    
    

    protected void onDialogClosed(boolean positiveResult) {
    	super.onDialogClosed(positiveResult);

        if (positiveResult) {
        	login = PreferenceManager.getDefaultSharedPreferences(getContext());
    		SharedPreferences.Editor editor = login.edit();
    		editor.putString("password", newpwd.getText().toString());
    		editor.commit();
        }
    }
	        
	}
	

		      /*  @Override
		        protected void onDialogClosed(boolean positiveResult) {
		           super.onDialogClosed(positiveResult);

		            if (positiveResult) {
		                // deal with persisting your values here
		            }
		        }*/
		
	

	


