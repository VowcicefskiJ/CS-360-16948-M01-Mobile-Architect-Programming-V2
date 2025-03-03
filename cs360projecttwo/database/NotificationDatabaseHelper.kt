package com.example.cs360projecttwo.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotificationDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Notifications.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NOTIFICATIONS = "notifications"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MESSAGE = "message"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NOTIFICATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MESSAGE TEXT,
                $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATIONS")
        onCreate(db)
    }

    fun logNotification(message: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MESSAGE, message)
        }
        db.insert(TABLE_NOTIFICATIONS, null, values)
        db.close()
    }

    fun getNotifications(): List<String> {
        val notifications = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NOTIFICATIONS, arrayOf(COLUMN_MESSAGE), null, null, null, null, "$COLUMN_TIMESTAMP DESC")

        if (cursor.moveToFirst()) {
            do {
                val message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE))
                notifications.add(message)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notifications
    }
}
