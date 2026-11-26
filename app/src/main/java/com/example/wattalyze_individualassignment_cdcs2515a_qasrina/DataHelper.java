package com.example.wattalyze_individualassignment_cdcs2515a_qasrina;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    //Database Name
    private static final String DATABASE_NAME = "wattalyze.db";

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Create Constructor
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create Table
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE electricity_bill (" +
                "id INTEGER PRIMARY KEY, " +
                "month TEXT, " +
                "units INTEGER, " +
                "totalCharges REAL, " +
                "rebate INTEGER, " +
                "finalCost REAL)";

        Log.d("Data", "onCreate: " + sql);

        db.execSQL(sql);
    }

    //Upgrade Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}