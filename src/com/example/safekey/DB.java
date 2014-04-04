package com.example.safekey;

import java.util.ArrayList;
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
    private static final String DATABASE_NAME = "userData";
 
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
        		" ( id INTEGER PRIMARY KEY AUTOINCREMENT, "
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
    void add(String date, long duration, int counter) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(DATE, date); 
        values.put(DURATION, duration); 
        values.put(COUNTER, counter);
 
        // Inserting Row
        db.insert(TABLE, null, values);
        
        Log.d(TAG, "INSERTED");
        db.close(); // Closing database connection
    }
 
    // Getting single contact
    List<String> getEntry(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE, new String[] { DATE,
                DURATION, COUNTER}, DATE + "=?",
                new String[] { String.valueOf(date) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        List <String> results = new ArrayList<String>();
        results.add(cursor.getString(1));
        results.add(cursor.getString(2));
        results.add(cursor.getString(3));
        
        return results;
    }
     
    // Getting All Contacts
    List<ArrayList<String>> getAllEntries() {
        List<ArrayList<String>> resultsList = new ArrayList<ArrayList<String>>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	int index = Integer.parseInt(cursor.getString(0));
            	ArrayList <String> results = new ArrayList<String>();
            	
                results.add(cursor.getString(1));
                results.add(cursor.getString(2));
                results.add(cursor.getString(3));
                
                resultsList.add(index, results);
            } while (cursor.moveToNext());
        }
 
        
        return resultsList;
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