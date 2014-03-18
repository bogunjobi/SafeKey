package com.example.safekey;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Main extends Activity {

	View callingView = null;
	SharedPreferences contacts = null;
	Editor editor = null;
	String contact1;
	String contact2;
	String number1;
	String number2;
	TextView mContact1;
	TextView mContact2;
	TextView mAddContact;
	View sep1;
	View sep2;
	View sep3;	
	
	//??
	DevicePolicyManager mDPM;
	ComponentName mDeviceAdminSample;
	LockScreen mActivity;
	AlertDialog.Builder alertDialog;
		
	///
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);		
		
				
		//load emergency contact information
		contacts = getSharedPreferences("Contacts", Context.MODE_PRIVATE);
		contact1 = contacts.getString("contact1", "");
		contact2 = contacts.getString("contact2", "");
		
		/*if (!contact1.equals(null))
			contactName1 = (String) contact1.toArray()[0];
		else contactName1 = "";
		if (!contact2.equals(null))
			contactName2 = (String) contact2.toArray()[0];
		else contactName2 = "";*/
		
		
		//load user image
		SharedPreferences user_pic = getSharedPreferences("UserPic", Context.MODE_PRIVATE);
        String map = user_pic.getString("image", null);		
        if (map != null){
        	Bitmap btmap = BitmapFactory.decodeFile(map);
        	ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        	imageView.setImageBitmap(btmap);
        }
        	
        //??
    	//mActivity = (LockScreen)getActivity();
    	mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
    	mDeviceAdminSample = new ComponentName(Main.this, MyAdmin.class);	   	
    	
        //check if device admin has been activated
      //??
        if (!isActiveAdmin()){
        	showDialog(this);
        	Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        	Log.d("Admin", "Activate");
        	startActivityForResult(i, 3);
        //startActivity(i);
        }
		//??
        
		TextView mName = (TextView) findViewById(R.id.hello_name);
		SharedPreferences auth_pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String name = auth_pref.getString("name", "user");
		mName.setText("HELLO " + name.split(" ")[0].toUpperCase() + "!");
		
		mContact1 = (TextView) findViewById(R.id.contacts2);
		mContact2 = (TextView) findViewById(R.id.contacts3);
		mAddContact = (TextView) findViewById(R.id.addcontacts);
		sep1 = (View)findViewById(R.id.separator2);
		sep2 = (View)findViewById(R.id.separator3);
		sep3 = (View)findViewById(R.id.separator4);
		
		///
		findViewById(R.id.button1).setOnClickListener(
				 new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						startActivity(new Intent(Main.this, LockScreen.class));						
					}
				});
				
		
		///
		findViewById(R.id.imageView1).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent i = new Intent(
								Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								 
						startActivityForResult(i,2); 
						
					}
				});		
		
		if (!contact1.equals("")){
			mContact1.setVisibility(View.VISIBLE);
			sep1.setVisibility(View.VISIBLE);
			mContact1.setText(contact1);
		}else {
			mAddContact.setVisibility(View.VISIBLE);
			sep3.setVisibility(View.VISIBLE);
		}
		if (!contact2.equals("")){
			mContact2.setVisibility(View.VISIBLE);
			sep2.setVisibility(View.VISIBLE);
			mContact2.setText(contact2);
		} else {
			mAddContact.setVisibility(View.VISIBLE);
			
			sep3.setVisibility(View.VISIBLE);
		}
		
		
		mContact1.setOnClickListener(tvlistener);
		mContact2.setOnClickListener(tvlistener);
		
		mAddContact.setOnClickListener(
			new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					if (mContact1.getVisibility() != View.VISIBLE){
						
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		            	// BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
		            	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		            	startActivityForResult(intent, 0); 
					} 
					else if (mContact2.getVisibility() != View.VISIBLE){
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		            	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		            	startActivityForResult(intent, 1);    
		            	
					}
				}	
		
			});
		
		
		
	}
	
	/**
	 * This function shows the warning dialog
	 */
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
	
	private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }
	
	OnClickListener tvlistener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			showPopup(v);
		}	
	};
	
	
	
	OnMenuItemClickListener listener = new OnMenuItemClickListener()
	{ 
		public boolean onMenuItemClick(MenuItem item){
			switch (item.getItemId()){
				case R.id.edit:
					edit();
					return true;
				case R.id.delete:
					delete();
				default:
					return false;
			}
		}
	};

	public void edit(){
		// user BoD suggests using Intent.ACTION_PICK instead of .ACTION_GET_CONTENT to avoid the chooser
       	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    // BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
	    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
	    if (callingView.getId() == R.id.contacts2)
	    	startActivityForResult(intent, 0); 
	    else if (callingView.getId() == R.id.contacts3)
	    	startActivityForResult(intent, 1);
	  }
	
	public void delete(){
		editor = contacts.edit();
		Log.d("CallingView", callingView.toString());
		if (callingView.getId() == R.id.contacts2){
			//repopulate the UI such that the first textview is not empty
			editor.putString("contact1", contact2);
			editor.putString("number1", number2);
			Log.d("Contact2", contact2);
		} 
		//Log.d("Outside", contact2);
		editor.putString("contact2", "");
		editor.putString("number2", "");
		//save the shared preferences and refresh the activity
		editor.commit();
		finish();
		startActivity(getIntent());
	}
	
	
	
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		if (requestCode == 2 ){
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
		         SharedPreferences user_pic = getSharedPreferences("UserPic", Context.MODE_PRIVATE);
		         SharedPreferences.Editor editor = user_pic.edit();
		 		 editor.putString("image", picturePath).commit();
		 		 
		         Bitmap btmap = BitmapFactory.decodeFile(picturePath);
		         ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		         imageView.setImageBitmap(btmap);
			}
		} else if (requestCode == 3){
			
			if (resultCode == RESULT_CANCELED) {
				alertDialog.show();
		        return;
		    } else {
		    	Intent intent = new Intent(this, Main.class);
				startActivity(intent);
		    }
		}	
		else{
		
		editor = contacts.edit();
	    if (data != null) {
	        Uri uri = data.getData();

	        if (uri != null) {
	            Cursor c = null;
	            try {
	                c = getContentResolver().query(uri, new String[]{ 
	                            ContactsContract.CommonDataKinds.Phone.NUMBER,  
	                            ContactsContract.CommonDataKinds.Phone.TYPE,
	                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
	                        null, null, null);

	                if (c != null && c.moveToFirst()) {
	                    String name = c.getString(2);
	                    String number = c.getString(0);
	                    if (requestCode == 0){
	                    	contact1 = name;
	                    	number1 = number;
							mContact1.setText(name);
	                    	editor.putString("contact1", contact1);	 
	                    	editor.putString("number1", number1);	 
	                    	if (mContact1.getVisibility() != View.VISIBLE)
	                    		mContact1.setVisibility(View.VISIBLE);
	                    		sep1.setVisibility(View.VISIBLE);
	            		}
	                    else {
	                    	contact2 = name;
	                    	number2 = number;
	                    	mContact2.setText(name);
	                    	editor.putString("contact2", contact2);
	                    	editor.putString("number2",number2);
	                    	if (mContact2.getVisibility() != View.VISIBLE)
	                    		mContact2.setVisibility(View.VISIBLE);
	                    		sep2.setVisibility(View.VISIBLE);
	                    		mAddContact.setVisibility(View.GONE);
	                    		sep3.setVisibility(View.GONE);
	                    }
	                    
	                    
	                    editor.commit();	                	
	                }
	            } finally {
	                if (c != null) {
	                	c.close();
	                }
	            }
	        }
	    }
		}
	}
	
	public void showPopup(View v) {
	    PopupMenu popup = new PopupMenu(this, v);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.actions, popup.getMenu());
	    popup.setOnMenuItemClickListener(listener);
	    callingView = v;
	    popup.show();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		//to ensure that users cannot go back to the setup page
		
	}

}
