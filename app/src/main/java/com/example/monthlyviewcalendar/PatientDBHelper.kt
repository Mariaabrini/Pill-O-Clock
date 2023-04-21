package com.example.monthlyviewcalendar

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

data class Patient(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val caregiverName: String
)


class PatientDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COL + " TEXT," +
                EMAIL_COL + " TEXT," + PASSWORD_COL + " TEXT," +
                CAREGIVERNAME_COL + " TEXT"+")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)

        // If you want to set default values for gender and birthdate columns to null
        //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + GENDER_COL + " TEXT DEFAULT NULL")
        //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + BIRTHDATE_COL + " TEXT DEFAULT NULL")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // Function to generate SHA-256 hash value from plaintext password
    private fun hashPassword(plaintextPassword: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(plaintextPassword.toByteArray())
        val digestBytes = messageDigest.digest()
        return digestBytes.fold("") { str, it -> str + "%02x".format(it) }
    }

    // This method is for adding data in our database
    fun addPatient(name: String, email: String, password: String, caregiver: String, gender: String?, birthdate: String? ){

        val hashedPassword = hashPassword(password)

        val values = ContentValues().apply {
            put(NAME_COL, name)
            put(EMAIL_COL, email)
            put(PASSWORD_COL, hashedPassword)
            put(CAREGIVERNAME_COL,caregiver)

        }

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $EMAIL_COL TEXT, $PASSWORD_COL TEXT," +
                " $CAREGIVERNAME_COL TEXT)")
        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    @SuppressLint("Range")
    fun getPatient(email: String, password: String): String? {

        val hashedPassword = hashPassword(password)

        val selection = "$EMAIL_COL = ? AND $PASSWORD_COL = ?"
        val selectionArgs = arrayOf(email, hashedPassword)

        // here we are creating a readable variable of our database as we want to read value from it
        val db = this.readableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY," +
                " $NAME_COL TEXT, $EMAIL_COL TEXT, $PASSWORD_COL TEXT, " +
                "$CAREGIVERNAME_COL TEXT)")

        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex(NAME_COL))
            cursor.close()
            return name
        }
        cursor.close()
        return null
    }

    @SuppressLint("Range")
    fun getUsernameOrEmail(email: String, name: String): String? {

        val selection = "$NAME_COL=? OR $EMAIL_COL=?"
        val selectionArgs = arrayOf(name, email)

        // here we are creating a readable variable of our database as we want to read value from it
        val db = this.readableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $EMAIL_COL TEXT, $PASSWORD_COL TEXT," +
                " $CAREGIVERNAME_COL TEXT)")

        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (cursor.moveToFirst()) {
            cursor.close()
            return "true"
        }
        cursor.close()
        return "false"
    }

    @SuppressLint("Range")
    fun getPatientsByCaregiver(caregiverName: String): List<Patient> {
        val patients = mutableListOf<Patient>()
        val db = readableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $EMAIL_COL TEXT, $PASSWORD_COL TEXT," +
                " $CAREGIVERNAME_COL TEXT)")

        val selection = "$CAREGIVERNAME_COL = ?"
        val selectionArgs = arrayOf(caregiverName)

        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID_COL))
                val name = cursor.getString(cursor.getColumnIndex(NAME_COL))
                val email = cursor.getString(cursor.getColumnIndex(EMAIL_COL))
                val password = cursor.getString(cursor.getColumnIndex(PASSWORD_COL))
                val caregiver = cursor.getString(cursor.getColumnIndex(CAREGIVERNAME_COL))
                patients.add(Patient(id, name, email, password, caregiver))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return patients
    }

    fun updatePatientPassword(patientName: String, newPassword: String) {
        val db = writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $EMAIL_COL TEXT, $PASSWORD_COL TEXT," +
                " $CAREGIVERNAME_COL TEXT)")

        val hashedPassword = hashPassword(newPassword)

        val contentValues = ContentValues()
        contentValues.put(PASSWORD_COL, hashedPassword)
        db.update(TABLE_NAME, contentValues, "$NAME_COL = ?", arrayOf(patientName))
        db.close()
    }




    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "PILLOCLOCK"

        // below is the variable for database version
        private val DATABASE_VERSION = 4

        // below is the variable for table name
        val TABLE_NAME = "patient_table"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val NAME_COL = "patient_username"

        // below is the variable for email column
        val EMAIL_COL = "email"

        // below is the variable for password column
        val PASSWORD_COL = "password"

        // below is the variable for caregiver column
        val CAREGIVERNAME_COL = "caregiver"


    }
}