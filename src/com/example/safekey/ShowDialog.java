package com.example.safekey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowDialog extends Activity {

	static Context context;
	static AlertDialog alert;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		//setContentView(R.layout.activity_show_dialog);
		
		showAlert(this);

		
	}

	public static void showAlert(Activity activity) {

        TextView title = new TextView(activity);
        title.setText("Warning!");
        title.setPadding(10, 10, 10, 10);
        //title.setGravity(Gravity.CENTER);
        //title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // builder.setTitle("Title");
        builder.setMessage("Disabling Device Admin will lock your device");
        builder.setCustomTitle(title);
       /* final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp); 
        builder.setView(input); */

        
        builder.setCancelable(false);
        builder.setPositiveButton("OK", listener);
        builder.setNegativeButton("Cancel", listener);
        alert = builder.create();
        alert.show();
	}   
    	
    static DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				if (which == DialogInterface.BUTTON_POSITIVE){
    					Toast.makeText(context, "Positive", Toast.LENGTH_LONG).show();
    				}else{
    					Toast.makeText(context, "Negative", Toast.LENGTH_LONG).show();
    				}
    		};
    		
    	};

        
       
   

}
