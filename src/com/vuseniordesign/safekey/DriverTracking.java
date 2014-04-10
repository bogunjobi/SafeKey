package com.vuseniordesign.safekey;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater.Filter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


public class DriverTracking extends Activity implements OnItemSelectedListener, OnClickListener{

	
	//static DB userDB; 
	static Context context;
	final int DAY = 0;
	final int MONTH = 1;
	final int YEAR = 2;
	
	
    
    LinearLayout lgraph;
    LinearLayout bgraph;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_tracking);
		
		//userDB = new DB(this);
		context = this;
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.spinner, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter); 
		spinner.setOnItemSelectedListener(this);
		
		
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    
	  
			
			//SwipeDetector swipe = new SwipeDetector(this, DriverTracking.this);
		
			//rg.setOnTouchListener(swipe);
			
			lgraph = (LinearLayout) findViewById(R.id.graph1);
			
			drawGraph(null);
			
	    //to debug the database
			try {
				writeToSD();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("WritetoSD", "Failed");
			}
			
			
		    
		   
					
	}

	
	public static Object[] getGraphData(DB userDB, String start, String end){
		List<String> userData = new ArrayList<String>();
		//userDB = new DB(context);
		if (start != null)
			userData = userDB.getEntryFrom(start, end);
		else
			userData = userDB.getAllEntries();	
		
		String[] date1 = new String[userData.size()/3];
		int [] duration = new int[userData.size()/3];
		int [] count = new int[userData.size()/3];
		
		if (userData.isEmpty()){
			//Toast.makeText(context, "No information to display", Toast.LENGTH_LONG).show();			
		} else {
			for (int i = 0 , j = 0; i < (userData.size()/3); i++, j++ )
			{
					date1[i] = userData.get(j);
					duration[i] = Integer.parseInt(userData.get(++j));
					count[i] = Integer.parseInt(userData.get(++j));								
			}
		}
		return new Object[]{date1, duration, count};
	}
	
	void drawGraph(String date) {
		//final java.text.DateFormat dtformatter = DateFormat.getDateFormat(this);
		   //int size = userData.size();
		   DB db = new DB(this);
		   Object [] data = getGraphData(db, date, null);
		   //String[] dates = (String[]) data[0];
		   //int [] duration = (int[]) data[1];
		   //int [] count = (int[]) data[2];
		   
		  
		   
		   //DISPLAY AGGREGATED DATA
		   //drawing dates vs. count 
		   LinkedHashMap<String, Integer> countData = (LinkedHashMap<String, Integer>) aggregateValues((String [])data [0], (int []) data[2], DAY);
		    //lazily converting map to arrays to display aggregated data
		   String [] keys = countData.keySet().toArray(new String [countData.size()]);
		   Integer[] counts = countData.values().toArray(new Integer [countData.size()]);
		   
		   
		    int size  = countData.size();
		    double max = 0;
		   GraphViewData[] values = new GraphViewData[size];	   
		   for (int index=0; index<countData.size(); index++){
			   Log.d("D", String.valueOf(keys[index]));
			   SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
			   long ms = 0;
			
				Date d = null;
				try {
					d = f.parse(keys[index]);
					ms = d.getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Log.d("D", String.valueOf(ms));
				Log.d("D", String.valueOf(counts[index]));
				if (max < counts[index])
						max = counts[index];
				values[index] = new GraphViewData(ms, counts[index]); 
		   }
		    
			GraphViewSeries series = new GraphViewSeries(values);
					
					
				 
				GraphView graphView = new LineGraphView(
				      this, "Attempted Phone Usage While Driving" // heading
				);
	graphView.setManualYAxis(true);
	graphView.setManualYAxisBounds(max, 0);
	graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
	@Override
	public String formatLabel(double value, boolean isValueX) {
	   if (isValueX) {
	  	  long dv = (long) value;
	   	  Date df = new java.util.Date(dv);
	   	  String date = new SimpleDateFormat("MM-dd-yyyy").format(df);
	   	  Log.d("Date", date);
	   	  return date; //.split(" ")[0];
	   } else {
	   	  return String.valueOf((int)value);
	   }
   }
});
	graphView.addSeries(series); 
	//graphView.setViewPort(2, 6);
	graphView.setScalable(true);
	graphView.setScrollable(true);
	
				 
	
	lgraph.addView(graphView);
	}

	//use to aggregate duration or counter by day
	public static Map<String, Integer> aggregateValues(String [] dates, int [] values, int agg){
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		Set<String> keyset = new HashSet<String>();	
		Integer val = 0;
		/*String [] dates = (String []) obj[0];
		//int [] duration = (int []) obj[1];
		int [] values = (int []) obj[2]; */
		switch (agg){
		case(1): //monthly
		
			for (int i=0; i < dates.length; i++){
				String s = dates[i].split(" ")[0]; //only store date
				String date = s.split("-")[0] + ", " + dates[i].split("-")[2];
				if (keyset.contains(date)){
					//val = map.get(a);
					int tempVal = map.get(date);
					map.put(date, Integer.valueOf(tempVal + values[i]));
				}else {
					keyset.add(date);
					map.put(date, values[i]);
				}			
			}
			break;
		case(2): //yearly
			for (int i=0; i < dates.length; i++){
				String s = dates[i].split(" ")[0]; //only store date
				String date = s.split("-")[2];
				if (keyset.contains(date)){
					int tempVal = map.get(date);
					map.put(date, tempVal + values[i]);
				}else {
					keyset.add(date);
					map.put(date, values[i]);
				}			
			}
			break;
		default: //daily
			for (int i=0; i < dates.length; i++){
				
				String date = dates[i].split(" ")[0];
				//if the date is already in the set, change its value
				if (keyset.contains(date)){
					//if (map.get(a) != null){
						val = map.get(date);
						//int tempVal = val.intValue();
						map.put(date, val + values[i]);
					
				}else {
					keyset.add(date);
					map.put(date, Integer.valueOf(values[i]));
				}			
			}
			break;
		}
		
		return map;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@SuppressWarnings("resource")
	private void writeToSD() throws IOException {
			Context context = this;
			String DB_PATH;
		    File sd = Environment.getExternalStorageDirectory();
		    
		    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
		        DB_PATH = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
		    }
		    else {
		        DB_PATH = context.getFilesDir().getPath() + context.getPackageName() + "/databases/";
		    }

		    if (sd.canWrite()) {
		        String currentDBPath = DB.DATABASE_NAME;
		        String backupDBPath = "backupname.db";
		        File currentDB = new File(DB_PATH, currentDBPath);
		        File backupDB = new File(sd, backupDBPath);

		        if (currentDB.exists()) {
		            FileChannel src = new FileInputStream(currentDB).getChannel();
		            FileChannel dst = new FileOutputStream(backupDB).getChannel();
		            dst.transferFrom(src, 0, src.size());
		            src.close();
		            dst.close();
		        }
		    }
		}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.driver_tracking, menu);
		return true;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		SimpleDateFormat cDate = new SimpleDateFormat("MM-dd-yyyy");
		String currentDate = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		//calendar.add(Calendar.DAY_OF_YEAR, -7);
		Date newDate; // = calendar.getTime();
		
		//Object item = parent.getItemAtPosition(position);
		switch (position) {
        case 0:           
			drawGraph(null);			
			break;            	
            
        case 1:
        	calendar.add(Calendar.DAY_OF_YEAR, -365);
    		newDate = calendar.getTime();
    		currentDate = cDate.format(newDate);
    		drawGraph(currentDate);
    		break;
        case 2:
        	calendar.add(Calendar.DAY_OF_YEAR, -182);
    		newDate = calendar.getTime();
    		currentDate = cDate.format(newDate);
    		drawGraph(currentDate);
    		break;
        case 3:
        	calendar.add(Calendar.DAY_OF_YEAR, -30);
    		newDate = calendar.getTime();
    		currentDate = cDate.format(newDate);
    		drawGraph(currentDate);
    		break;
        case 4:
        	calendar.add(Calendar.DAY_OF_YEAR, -7);
    		newDate = calendar.getTime();
    		currentDate = cDate.format(newDate);
    		drawGraph(currentDate);
    		break;
        case 5:
        	calendar.add(Calendar.DAY_OF_YEAR, -1);
    		newDate = calendar.getTime();
    		currentDate = cDate.format(newDate);
    		drawGraph(currentDate);
    		break;
    	default:
    		
    		break;
        	
        	
        }
		
	}


	
	
	
	@Override
	public void onClick(View v) {
		Filter f = (Filter) v.getTag();
		//FilterFullScreenActivity.show();
        //Object null;
		//Main.show(this, null, f);
		
	}


	

	

	
}
