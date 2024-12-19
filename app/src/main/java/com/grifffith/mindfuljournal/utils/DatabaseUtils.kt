package com.grifffith.mindfuljournal.utils

import android.database.Cursor

object DatabaseUtils {

    // Check if a table exists
    fun doesTableExist(cursor: Cursor, tableName: String): Boolean {
        while (cursor.moveToNext()) {
            if (cursor.getString(0) == tableName) return true
        }
        return false
    }

    // Convert Cursor to a Map for easier data access
    fun cursorToMap(cursor: Cursor): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()
        val columnNames = cursor.columnNames
        while (cursor.moveToNext()) {
            val row = mutableMapOf<String, Any>()
            for (column in columnNames) {
                row[column] = cursor.getString(cursor.getColumnIndexOrThrow(column))
            }
            result.add(row)
        }
        return result
    }

    // Close Cursor safely
    fun closeCursor(cursor: Cursor?) {
        cursor?.let {
            if (!it.isClosed) {
                it.close()
            }
        }
    }
}
