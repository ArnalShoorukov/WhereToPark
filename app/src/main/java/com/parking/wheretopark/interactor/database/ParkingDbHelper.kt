package com.parking.wheretopark.interactor.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.parking.wheretopark.interactor.database.ParkingContract.ParkingEntry

/*
* {@link #ParkingDbHelper} is parking DB helper class for DB
*
*/

class ParkingDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {

        // Create tasks table (careful to follow SQL formatting rules)
        val CREATE_TABLE = "CREATE TABLE " + ParkingEntry.TABLE_NAME + " (" +

                ParkingEntry._ID                     + " INTEGER PRIMARY KEY, " +
                ParkingEntry.COLUMN_TITLE            + " TEXT NOT NULL, " +
                ParkingEntry.COLUMN_TIME_ENTERED     + " TEXT NOT NULL, " +
                ParkingEntry.COLUMN_TIME_EXIT        + " TEXT NOT NULL, " +
                ParkingEntry.COLUMN_PRICE            + " DOUBLE NOT NULL);"

        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + ParkingEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {

        private val DB_NAME = "parking.db"
        private val DB_VERSION = 1
    }
}
