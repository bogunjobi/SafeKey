package com.example.safekey;


import java.util.ArrayList;
import java.util.List;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


public class DriverTracking extends Activity {

	
	DB userDB = new DB(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_tracking);

		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.spinner, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter); 
		List<ArrayList<String>> userData = new ArrayList<ArrayList<String>>();
		try {
		userData = userDB.getAllEntries();
		} catch (IndexOutOfBoundsException e){
			e.printStackTrace();
		} 
		
		if (userData.isEmpty()){
			Toast.makeText(this, "No information to display", Toast.LENGTH_LONG).show();
			
		} else{
			for (ArrayList<String> a: userData){
				for (String b: a)
					Log.d("Data", b);
			}
		}
		
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
			      new GraphViewData(1, 2.0d)
			      , new GraphViewData(2, 1.5d)
			      , new GraphViewData(3, 2.5d)
			      , new GraphViewData(4, 1.0d)
			      , new GraphViewData(5, 1.0d)
			      , new GraphViewData(6, 10.0d)
			});
			 
			GraphView graphView = new LineGraphView(
			      this, "Attempted Phone Usage While Driving" // heading
			);
			graphView.addSeries(exampleSeries); // data
			 
			LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
			layout.addView(graphView);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.driver_tracking, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
}
