package com.vuseniordesign.safekey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;

public class LoadData extends Service {
	DB db;
	static String [] data = new String[3];
	
	@Override
	public void onCreate(){
		super.onCreate();
		db = new DB(this);
		Log.d("In LoadData", "service me");
		
		
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Log.d("Loaddata", "Running");
        try {
			load("sampledata.txt");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return START_NOT_STICKY;
    }
	
	
    public void load(String filename) throws NumberFormatException, IOException {      
    	
       //Scanner Example - read file line by line in Java using Scanner
    	AssetManager am = this.getAssets();
    	InputStream is = am.open(filename);
    	InputStreamReader inputreader = new InputStreamReader(is); 
        BufferedReader br = new BufferedReader(inputreader); 
    	
    	String strLine;
        while ((strLine = br.readLine()) != null) {       
        	data = strLine.split(",");
           Log.d("Loaddata", data[0] + " " + data[1] + " " + data[2]);
           db.add(data[0].trim(), Long.parseLong(data[1].trim()), Integer.parseInt(data[2].trim()));
        }
        br.close();
    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}   
      
}

