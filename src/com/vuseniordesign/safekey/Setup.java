package com.vuseniordesign.safekey;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Setup extends Activity {
	int i = 0;
	Context context;
	String contact_1, number_1, contact_2, number_2;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		context = this;
		
		
		
		final TextView contact1 = (TextView) findViewById(R.id.contact1);
		final TextView contact2 = (TextView) findViewById(R.id.contact2);
		
		
		
		
		findViewById(R.id.contact1).setOnClickListener( 
				
				new View.OnClickListener() {
		        @Override
		        	public void onClick(View v) {
		            	if (contact1.getText().toString() != ""){
		            		if (contact2.getText().toString()!= ""){
		            			contact1.setText(contact2.getText().toString());
		            			contact2.setText("");
		            			contact2.setVisibility(View.INVISIBLE);
		            		}else{
		            			contact1.setText("");
		            			contact1.setVisibility(View.INVISIBLE);
		            		}
		            		
		            		i--;
		            	}
			        }
		        
		    });
		
		findViewById(R.id.contact2).setOnClickListener( 
				
				new View.OnClickListener() {
		        @Override
		        	public void onClick(View v) {
		            	if (contact2.getText().toString() != ""){
		            		contact2.setText("");
		            		contact2.setVisibility(View.INVISIBLE);
		            		i--;
		            	}
		        }
		    });
			
		
		
		findViewById(R.id.add_contacts).setOnClickListener( 
			
			new View.OnClickListener() {
	        @Override
	        	public void onClick(View v) {
	        		if (i >= 2)
	        			Toast.makeText(context, "Maximum of two emergency contacts", Toast.LENGTH_LONG).show();  
	        		else {
	            	// user BoD suggests using Intent.ACTION_PICK instead of .ACTION_GET_CONTENT to avoid the chooser
	            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	            	// BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
	            	intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
	            	startActivityForResult(intent, 1); 
	        		}
	        }
	    });
		
		findViewById(R.id.skip).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent i = new Intent(Setup.this, Main.class);
						startActivity(i);
					}
				});
		findViewById(R.id.finish).setOnClickListener(
				new View.OnClickListener() {
					@Override
					
					public void onClick(View view) {
						//final String contact_1 = contact1.getText().toString();
						//final String contact_2 = contact2.getText().toString();
						SharedPreferences auth_pref = getApplicationContext().getSharedPreferences("Contacts", MODE_PRIVATE);
						SharedPreferences.Editor editor = auth_pref.edit();
						//Set<String> contact1 = new HashSet<String>(Arrays.asList(contact_1, number_1));
						//Set<String> contact2 = new HashSet<String>(Arrays.asList(contact_2, number_2));
						editor.putString("contact1", contact_1);
						editor.putString("contact2", contact_2);
						editor.putString("number1", number_1);
						editor.putString("number2", number_2);
						editor.commit();
						
						
						 
						
						Intent intent = new Intent(Setup.this, Main.class);
						/*i.putExtra("contact1", contact_1);
						i.putExtra("contact2", contact_2);*/
						startActivity(intent);
					}
				});
	
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
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
	                    String number = c.getString(0);
	                    int type = c.getInt(1);
	                    String name = c.getString(2);
	                    showSelectedNumber(type, number, name);
	                }
	            } finally {
	                if (c != null) {
	                    c.close();
	                }
	            }
	        }
	    }
	}
	
	
	
	public void showSelectedNumber(int type, String number, String name) {
		final TextView contact1 = (TextView) findViewById(R.id.contact1);
		final TextView contact2 = (TextView) findViewById(R.id.contact2);
		if (i == 0){
			contact1.setText(name);
			contact1.setVisibility(View.VISIBLE);
			number_1 = number;
			contact_1 = name;
			i++;
		} else if (i == 1){
			contact2.setText(name);
			contact2.setVisibility(View.VISIBLE);
			number_2 = number;
			contact_2 = name;
			i++;
		} else {				
	    Toast.makeText(this, "Maximum of two emergency contacts", Toast.LENGTH_LONG).show();  
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		return true;
	}

}
