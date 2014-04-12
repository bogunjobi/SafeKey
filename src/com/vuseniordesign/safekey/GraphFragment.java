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

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GraphFragment extends Fragment {

    int fragVal;
    ViewGroup container;
    static double scmax = 0;
    static double dmax = 0;
    static String radioVal;
    final int TRIP_DURATION = 2;
    final int SCREENCOUNTER = 1;
    		
     
     static GraphFragment init(int val){
     	GraphFragment newFrag = new GraphFragment();
     	Bundle args = new Bundle();
     	args.putInt("val", val);
     	newFrag.setArguments(args);
     	return newFrag;        	
     }
     
    @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
         radioVal = (String) CustomizerFragment.radioText;
         if (radioVal != null)
         Log.d("Radio Button", radioVal);
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
     	this.container = container;
         View rootView = inflater.inflate(R.layout.graph, container, false);
         LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.graph1);
         
         SimpleDateFormat cDate = new SimpleDateFormat("MM-dd-yyyy");
         
 			Calendar calendar = Calendar.getInstance();
 			Date newDate;
 			String currentDate;
 			
 			String startDate; 
 			String endDate;
 			
 			GraphView gview = null;
 			radioVal = (String) CustomizerFragment.radioText;
 			
 			if (radioVal == null)
 				 gview = drawGraph(null, null);
 			else{
 			Log.d("Radio Text", radioVal);
         
         if (radioVal.equals("Beginning of Time"))
        	 gview = drawGraph(null, null);
         /*else {
        	 startDate = cDate.format(CustomizerFragment.start);
        	 Log.d("Start Date", startDate);
        	 endDate = cDate.format(CustomizerFragment.end);
        	 Log.d("End Date", endDate);
        	 gview = drawGraph(startDate, endDate);
         } */     	 
         //TODO: Compress this
         else if (radioVal.equals("1 year")){
        	 calendar.add(Calendar.YEAR, -1);
      		 newDate = calendar.getTime();
      		 currentDate = cDate.format(newDate);
      		 Log.d("Current", currentDate);
      		 gview = drawGraph(currentDate, null);
         }  else if (radioVal.equals("1 month")){
        	 calendar.add(Calendar.MONTH, -1);
      		 newDate = calendar.getTime();
      		 currentDate = cDate.format(newDate);
      		 gview = drawGraph(currentDate, null);
         } else if (radioVal.equals("1 week")){
        	 calendar.add(Calendar.WEEK_OF_YEAR, -1);
      		 newDate = calendar.getTime();
      		 currentDate = cDate.format(newDate);
      		 gview = drawGraph(currentDate, null);
         }else if (radioVal.equals("Custom")){
        	 startDate = cDate.format(CustomizerFragment.start);
        	 Log.d("Start Date", startDate);
        	 endDate = cDate.format(CustomizerFragment.end);
        	 Log.d("End Date", endDate);
        	 gview = drawGraph(startDate, endDate);
         }
 		} 
         ll.addView(gview);   
         
         try {
				writeToSD();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("WritetoSD", "Failed");
			}
         
                   
         return rootView;
     }
     
     public static Object[] getGraphData(DB userDB, String date, String end){
    	 
    	//if (radioVal.equalsIgnoreCase("custom"))
 		List<String> userData = new ArrayList<String>();
 		//userDB = new DB(context);
 		if (date != null)
 			userData = userDB.getEntryFrom(date, end);
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
     
     //TODO: clean up this method
     GraphView drawGraph(String date, String end) {
     		DB db = new DB(getActivity());
     		
 		   Object [] data = getGraphData(db, date, end);
 		  
 	
 		   //DISPLAY AGGREGATED DATA
 		   //drawing dates vs. count 
 		   
 		  LinkedHashMap<String, Integer> valueMap;
 		   
 		   if (fragVal == SCREENCOUNTER){
 			   valueMap = (LinkedHashMap<String, Integer>) aggregateValues((String [])data [0], (int []) data[2], 0);
 		   } else {
 			   valueMap = (LinkedHashMap<String, Integer>) aggregateValues((String [])data [0], (int []) data[1], 0);
 		   
 		   }
 		   int size  = valueMap.size();
 			   //lazily converting map to arrays to display aggregated data
 		   String [] keys = valueMap.keySet().toArray(new String [size]);
 		   Integer[] values = valueMap.values().toArray(new Integer [size]);
 		   //Integer [] duration = tripDuration.values().toArray(new Integer [countData.size()]);
 		   
 		    
 		   
 		   GraphViewData[] dataset = new GraphViewData[size];	   
 		   for (int index=0; index<size; index++){
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
 				
 				//make sure max is only computed for fragVal = 1
 				Log.d("D", String.valueOf(ms));
 				Log.d("D", String.valueOf(values[index]));
 				
 				if (fragVal == SCREENCOUNTER && scmax < values[index])
 						scmax = values[index];
 				
 				if (fragVal == TRIP_DURATION && dmax < values[index])
						dmax = values[index];
 				dataset[index] = new GraphViewData(ms, values[index]); 
 		   }
 		    
 			GraphViewSeries series = new GraphViewSeries(dataset);
 					
 				GraphView graphView;
 				
 				if (fragVal == TRIP_DURATION)
 					graphView = new BarGraphView(MyApplication.getAppContext(), "Trip Durations" );    				 
 				else
 					graphView = new LineGraphView(MyApplication.getAppContext(), "Attempted Phone Usage While Driving" // heading
 				);
 	
 	graphView.setManualYAxis(true);
 	if (fragVal == SCREENCOUNTER){
 		graphView.setManualYAxisBounds(scmax, 0);
 	} else graphView.setManualYAxisBounds(dmax, 0);
 	
 	
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
 		   
 		   if (fragVal == 2){
 			   if (value >= 3600)
 				   return String.valueOf((int) value/3600) + " h" + String.valueOf(value%3600) + " m" ;
 			   else if (value < 3600 && value > 60)
 			   		return String.valueOf(Math.round(value/60)) + " m";
 			   else if (value == 0)
 				   return String.valueOf(0);
 			   else 
 				   return String.valueOf((int) value) + " s";
 		   }
 	   	  return String.valueOf((int)value);
 	   }
    }
 });
 	graphView.addSeries(series); 
 	graphView.setScalable(true);
 	graphView.setScrollable(true);
 	graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
 	graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
 	
 	if (fragVal == 1)
 		((LineGraphView) graphView).setDrawDataPoints(true);
 	return graphView;
 	
 	}

 	//use to aggregate duration or counter by day
 	public static Map<String, Integer> aggregateValues(String [] dates, int [] values, int agg){
 		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
 		Set<String> keyset = new HashSet<String>();	
 		Integer val = 0;
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
 	
 	private void writeToSD() throws IOException {
		Context context = UserData.context;
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
	            @SuppressWarnings("resource")
				FileChannel src = new FileInputStream(currentDB).getChannel();
	            @SuppressWarnings("resource")
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
	            dst.transferFrom(src, 0, src.size());
	            src.close();
	            dst.close();
	        }
	    }
	}
     
 }
 