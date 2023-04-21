package com.example.monthlyviewcalendar

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Medication(
    val id: Int,
    val name: String,
    val timesPerDay: String,
    val dose: String,
    val type: String,
    val container: String,
    val time: String,
    val stock: String,
    val refill: String,
    val taken: String,
    val patientName: String
)

class ScheduledPillDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COL + " TEXT," +
                TIMESADAY_COL + " TEXT," + DOSE_COL + " TEXT," +
                TYPE_COL + " TEXT," +
                CONTAINER_COL + " TEXT,"+
                TIME_COL + " TEXT,"+
                STOCK_COL + " TEXT,"+
                REFILL_COL + " TEXT,"+ TAKEN_COL+ " TEXT,"+ PATIENTNAME_COL + " TEXT"+")")

        // we are calling sqlite method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }


    // This method is for adding data in our database
    fun addScheduledPill(name: String, timesaday: String, dose: String, type: String,
                         container: String,time: String, stock: String,refill: String
                         ,taken: String,patientName: String){


        val values = ContentValues().apply {
            put(NAME_COL, name)
            put(TIMESADAY_COL, timesaday)
            put(DOSE_COL, dose)
            put(TYPE_COL,type)
            put(CONTAINER_COL,container)
            put(TIME_COL,time)
            put(STOCK_COL,stock)
            put(REFILL_COL,refill)
            put(TAKEN_COL,taken)
            put(PATIENTNAME_COL,patientName)
        }

        val db = this.writableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")


        db.insert(TABLE_NAME, null, values)

        db.close()
    }

    // below method is to get all data from our database
    @SuppressLint("Range")
    fun getScheduledPill(): String? {
        return null
    }

    @SuppressLint("Range")
    fun getName(name: String, patientName: String): String? {
        // here we are creating a readable variable of our database as we want to read value from it
        val db = this.readableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")

        val selection = "${NAME_COL} = ? AND ${PATIENTNAME_COL} = ?"
        // Define the columns you want to retrieve from the table
        val selectionArgs = arrayOf(name, patientName)

        // Query the table for the names of caregivers
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (cursor.moveToFirst()) {

            cursor.close()
            return "true"
        }
        cursor.close()
        return "false"
    }

    @SuppressLint("Range")
    fun getContainer(name: String,container: String): String? {
        // here we are creating a readable variable of our database as we want to read value from it
        val db = this.readableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")

        val selection = "${PATIENTNAME_COL} = ? AND ${CONTAINER_COL} = ?"
        // Define the columns you want to retrieve from the table
        val selectionArgs = arrayOf(name,container)

        // Query the table for the names of caregivers
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (cursor.moveToFirst()) {
            cursor.close()
            return "true"
        }
        cursor.close()
        return "false"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    fun getMedicationsByPatientName(patientN: String): List<Medication> {
        val medications = ArrayList<Medication>()

        val selection = "$PATIENTNAME_COL=?"
        val selectionArgs = arrayOf(patientN)

        val db = this.readableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")

        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        medications.clear()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(ID_COL))
            val name = cursor.getString(cursor.getColumnIndex(NAME_COL))
            val timesPerDay = cursor.getString(cursor.getColumnIndex(TIMESADAY_COL))
            val dose = cursor.getString(cursor.getColumnIndex(DOSE_COL))
            val type = cursor.getString(cursor.getColumnIndex(TYPE_COL))
            val container = cursor.getString(cursor.getColumnIndex(CONTAINER_COL))
            val time = cursor.getString(cursor.getColumnIndex(TIME_COL))
            val stock = cursor.getString(cursor.getColumnIndex(STOCK_COL))
            val refill = cursor.getString(cursor.getColumnIndex(REFILL_COL))
            val taken = cursor.getString(cursor.getColumnIndex(TAKEN_COL))
            val patientName = cursor.getString(cursor.getColumnIndex(PATIENTNAME_COL))

            val medication = Medication(id, name, timesPerDay, dose, type, container, time, stock, refill,taken, patientName)
            medications.add(medication)
        }

        cursor.close()
        db.close()

        // Assuming medications is the ArrayList<Medication> to be sorted
        val sortedMedications = medications.sortedBy {
            LocalTime.parse(it.time, DateTimeFormatter.ofPattern("hh:mm:ss a"))
        }


        return sortedMedications
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    fun getMedicationsNotTakenByPatientName(patientN: String): List<Medication> {
        val medications = ArrayList<Medication>()


        val selection = "$PATIENTNAME_COL=? AND $TAKEN_COL=?"
        val selectionArgs = arrayOf(patientN, "false")

        val db = this.readableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")

        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        medications.clear()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(ID_COL))
            val name = cursor.getString(cursor.getColumnIndex(NAME_COL))
            val timesPerDay = cursor.getString(cursor.getColumnIndex(TIMESADAY_COL))
            val dose = cursor.getString(cursor.getColumnIndex(DOSE_COL))
            val type = cursor.getString(cursor.getColumnIndex(TYPE_COL))
            val container = cursor.getString(cursor.getColumnIndex(CONTAINER_COL))
            val time = cursor.getString(cursor.getColumnIndex(TIME_COL))
            val stock = cursor.getString(cursor.getColumnIndex(STOCK_COL))
            val refill = cursor.getString(cursor.getColumnIndex(REFILL_COL))
            val taken = cursor.getString(cursor.getColumnIndex(TAKEN_COL))
            val patientName = cursor.getString(cursor.getColumnIndex(PATIENTNAME_COL))

            val medication = Medication(id, name, timesPerDay, dose, type, container, time, stock, refill,taken, patientName)
            medications.add(medication)
        }

        cursor.close()
        db.close()

        // Assuming medications is the ArrayList<Medication> to be sorted
        val sortedMedications = medications.sortedBy {
            LocalTime.parse(it.time, DateTimeFormatter.ofPattern("hh:mm:ss a"))
        }

        return sortedMedications
    }

    @SuppressLint("Range")
    fun getMedicationNamesForPatient(patientName: String): List<String> {
        val db = this.readableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")
        val projection = arrayOf(NAME_COL)
        val selection = "$PATIENTNAME_COL = ?"
        val selectionArgs = arrayOf(patientName)
        val cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null)
        val medicationNames = mutableListOf<String>()

        medicationNames.clear()

        while (cursor.moveToNext()) {
            val medicationName = cursor.getString(cursor.getColumnIndex(NAME_COL))
            medicationNames.add(medicationName)
        }
        cursor.close()
        return medicationNames
    }

    fun deleteMedsByMedNameAndPatientName(medName: String, patient: String): Boolean {
        val db = this.writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")
        val args = arrayOf(medName, patient)
        val result = db.delete(TABLE_NAME, "$NAME_COL =? AND $PATIENTNAME_COL =?", args)
        db.close()
        return result != -1
    }
    fun deleteMedsByMedNameAndPatientNameAndTime(medName: String, patient: String, time:String): Boolean {
        val db = this.writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY, " +
                "$NAME_COL TEXT, " +
                "$TIMESADAY_COL TEXT, " +
                "$DOSE_COL TEXT, " +
                "$TYPE_COL TEXT, " +
                "$CONTAINER_COL TEXT, " +
                "$TIME_COL TEXT, " +
                "$STOCK_COL TEXT, " +
                "$REFILL_COL TEXT, " +
                "$TAKEN_COL TEXT, " +
                "$PATIENTNAME_COL TEXT" +
                ")")
        val args = arrayOf(medName, patient,time)
        val result = db.delete(TABLE_NAME, "$NAME_COL =? AND $PATIENTNAME_COL =? AND $TIME_COL =?", args)
        db.close()
        return result != -1
    }



    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "PILLOCLOCK"

        // below is the variable for database version
        private val DATABASE_VERSION = 4

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

        // below is the variable for taken column
        val TAKEN_COL = "taken"

        // below is the variable for patient name column
        val PATIENTNAME_COL = "patient"

    }
}