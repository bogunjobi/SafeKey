package com.vuseniordesign.safekey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class DB extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    private final String TAG = "DB";
    // Database Name
    public static final String DATABASE_NAME = "userTracker";
 
    // Contacts table name
    private static final String TABLE = "usageData";
 
    // Contacts Table Columns names
    private static final String DATE = "date";
    private static final String COUNTER = "screenCount";
    private static final String DURATION = "duration";
    
    Context context;
 
    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + 
        		" ( id INTEGER PRIMARY KEY , "
                + DATE + " TEXT NOT NULL," + DURATION + " LONG NOT NULL,"
                + COUNTER + " INTEGER NOT NULL DEFAULT '0')";
        db.execSQL(CREATE_TABLE);
       
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE); 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new contact
    void add(String currentDate, long duration, int counter) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(DATE, currentDate); 
        values.put(DURATION, duration); 
        values.put(COUNTER, counter);
 
        // Inserting Row
        db.insert(TABLE, null, values);
        
        Log.d(TAG, "INSERTED");
        db.close(); // Closing database connection
        
    }
 
    // Getting single contact
    List<String> getEntryFrom(String date, String enddate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate;
        if (enddate == null){ //get current date
        SimpleDateFormat cDate = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
		currentDate = cDate.format(new Date());
        } else currentDate = enddate;
        List <String> results = new ArrayList<String>();
        Cursor cursor = db.query(TABLE, new String[] { DATE,
                DURATION, COUNTER}, DATE + " BETWEEN ? AND ?",
                new String[] { date+" 00:00:00", currentDate}, null, null, null, null);
        if (cursor.moveToFirst()){
        	
        	do {        
        		results.add(cursor.getString(0));
        		results.add(cursor.getString(1));
        		results.add(cursor.getString(2));
        		
        		Log.d("Date", cursor.getString(0));
        		Log.d("Duration", cursor.getString(1));
        		Log.d("Counter", cursor.getString(2));
        		
        	} while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return results;
    }
     
    // Getting All Contacts
    List<String> getAllEntries() {
        List<String> results = new ArrayList<String>();
        // Select All Query
        
       
        
        //String selectQuery = "SELECT * FROM " + TABLE;
 
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE, new String[] { DATE,
                DURATION, COUNTER}, null, null, null, null, null);
        //Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	Log.d(TAG, "Reading all");
            	//int index = (Integer.parseInt(cursor.getString(0)));
            	//ArrayList <String> results = new ArrayList<String>();
            	
                results.add(cursor.getString(0));
                results.add(cursor.getString(1));
                results.add(cursor.getString(2));
                
                
                
                //Log.d("Index", Integer.toString(index));
            } while (cursor.moveToNext());
        }
 
        db.close();
        cursor.close();
        return results;
    }   
    
 
    // Getting contacts Count
    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
}