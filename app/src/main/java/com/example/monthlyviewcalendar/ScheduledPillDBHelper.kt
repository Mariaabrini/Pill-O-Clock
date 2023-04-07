package com.example.monthlyviewcalendar

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class ScheduledPillDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COL + " TEXT," +
                TIMESADAY_COL + " TEXT," + DOSE_COL + " TEXT," +
                TYPE_COL + " TEXT," +
                CONTAINER_COL + " TEXT,"+
                TIME_COL + " TIMESTAMP,"+
                STOCK_COL + " TEXT,"+
                REFILL_COL + " TEXT,"+
                PATIENTNAME_COL + " TEXT"+")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }


    // This method is for adding data in our database
    fun addScheduledPill(name: String, email: String, password: String, caregiver: String ){
        
    }

    // below method is to get
    // all data from our database
    @SuppressLint("Range")
    fun getScheduledPill(email: String, password: String): String? {

        return null
    }

    @SuppressLint("Range")
    fun getUsernameOrEmail(email: String, name: String): String? {
        return null
    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "PILLOCLOCK"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "scheduledPill_table"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val NAME_COL = "med_name"

        // below is the variable for timesaday column
        val TIMESADAY_COL = "timesAday"

        // below is the variable for dose column
        val DOSE_COL = "dose"

        // below is the variable for type column
        val TYPE_COL = "type"

        // below is the variable for container column
        val CONTAINER_COL = "container"

        // below is the variable for time column
        val TIME_COL = "time"

        // below is the variable for stock column
        val STOCK_COL = "stock"

        // below is the variable for refill column
        val REFILL_COL = "refill"

        // below is the variable for patient name column
        val PATIENTNAME_COL = "patient"

    }
}