package com.kaycleaves.unboxyourself;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roolet on 9/21/2016.
 * Handles all database functions for storing and retrieving outings
 * Works together with Outing class
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    // all Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1; // must be changed to alter schema

    // Database Name
    private static final String DATABASE_NAME = "unboxYourself";

    // Outings table name
    private static final String TABLE_OUTINGS = "outings";

    // Outings column names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_MODE = "mode";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OUTINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_OUTINGS + "(" + KEY_ID +
                " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT," + KEY_MODE + " TEXT, UNIQUE (" + KEY_DATE + ", " + KEY_MODE + ")" + ")";
        db.execSQL(CREATE_OUTINGS_TABLE);
    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTINGS);

        // Create table again
        onCreate(db);
    }

    // Create/retrieve/update/delete functions below
    // Adding new outing
    public void addOuting(Outing outing) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, outing.getDate()); // Date
        values.put(KEY_MODE, outing.getMode()); // tracking mode

        // inserting row
        db.insertWithOnConflict(TABLE_OUTINGS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    // Get single outing
    public Outing getOuting(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OUTINGS, new String[] { KEY_ID, KEY_DATE, KEY_MODE },
                KEY_ID + "=?", new String[] {String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Outing outing = new Outing(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2));
        // return outing
        db.close();
        return outing;
    }

    // Get most recent outing
    public Outing getLastOuting() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM outings ORDER BY id DESC LIMIT 1",null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Outing outing = new Outing(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2));
        // return outing
        db.close();
        return outing;
    }

    // Getting all outings
    // Loop through results with a for loop
    public List<Outing> getAllOutings() {
        List<Outing> outingList = new ArrayList<Outing>();
        //Select All query
        String selectQuery = "SELECT * FROM " + TABLE_OUTINGS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // loop through rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Outing outing = new Outing();
                outing.setID(Integer.parseInt(cursor.getString(0)));
                outing.setDate(cursor.getString(1));
                outing.setMode(cursor.getString(2));
                // Adding Outing to list
                outingList.add(outing);
            } while (cursor.moveToNext());
        }
        db.close();
        // return list
        return outingList;

    }

    // Getting outings count
    public int getOutingsCount(){
        String countQuery = "SELECT * FROM " + TABLE_OUTINGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Updating single contact
    public int updateOuting(Outing outing){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, outing.getDate());
        values.put(KEY_MODE, outing.getMode());

        // updating row
        return db.update(TABLE_OUTINGS, values, KEY_ID + " =?", new String[]
                { String.valueOf(outing.getID()) } );
    }

    // Deleting single outing
    public void deleteOuting(Outing outing){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OUTINGS, KEY_ID + " =?", new String[] { String.valueOf(outing.getID()) });
        db.close();
    }


}
