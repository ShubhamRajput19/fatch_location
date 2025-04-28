package com.example.fatch_location
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocationDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "location.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE location(id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL, timestamp TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS location")
        onCreate(db)
    }

    fun insertLocation(latitude: Double, longitude: Double, timestamp: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("latitude", latitude)
            put("longitude", longitude)
            put("timestamp", timestamp)
        }
        db.insert("location", null, values)
        db.close()
    }

    fun getLastLocation(): Map<String, Any>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM location ORDER BY id DESC LIMIT 1", null)
        return if (cursor.moveToFirst()) {
            val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))
            val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
            val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
            cursor.close()
            db.close()
            mapOf("latitude" to latitude, "longitude" to longitude, "timestamp" to timestamp)
        } else {
            cursor.close()
            db.close()
            null
        }
    }
}