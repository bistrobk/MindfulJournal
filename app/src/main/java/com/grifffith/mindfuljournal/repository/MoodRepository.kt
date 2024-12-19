// MoodRepository.kt
package com.grifffith.mindfuljournal.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.grifffith.mindfuljournal.db.MindfulJournalDBHelper

/**
 * Repository class to manage Mood Entries in the database.
 */
class MoodRepository(private val dbHelper: MindfulJournalDBHelper) {

    /**
     * Adds a new mood entry to the MoodLogs table.
     *
     * @param mood The mood emoji or description.
     * @param note Additional notes or thoughts.
     * @param answers List of answers to the mood questionnaire.
     */
    fun addMoodEntry(
        mood: String,
        note: String,
        answers: List<String>
    ) {
        // Validate the number of answers
        if (answers.size < 7) {
            throw IllegalArgumentException("All 7 questions must be answered.")
        }

        val db: SQLiteDatabase = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("mood", mood)
            put("note", note)
            // Store answers in question1, question2, ..., question7
            answers.forEachIndexed { index, answer ->
                put("question${index + 1}", answer)
            }
            put("logged_at", System.currentTimeMillis()) // Store as Long (epoch milliseconds)
        }

        try {
            db.insertOrThrow("MoodLogs", null, contentValues)
        } catch (e: Exception) {
            // Handle insertion errors (e.g., constraint violations)
            e.printStackTrace()
            // You might want to propagate the exception or handle it as per your app's requirements
        } finally {
            db.close()
        }
    }

    /**
     * Retrieves all mood entries from the MoodLogs table, ordered by most recent first.
     *
     * @return List of MoodEntry objects.
     */
    fun getAllMoodEntries(): List<MoodEntry> {
        val moodEntries = mutableListOf<MoodEntry>()
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val query = "SELECT * FROM MoodLogs ORDER BY logged_at DESC"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val mood = cursor.getString(cursor.getColumnIndexOrThrow("mood"))
                    val note = cursor.getString(cursor.getColumnIndexOrThrow("note"))
                    // Retrieve answers for questions 1 through 7
                    val answers = (1..7).map { i ->
                        cursor.getString(cursor.getColumnIndexOrThrow("question$i")) ?: "N/A"
                    }
                    val loggedAt = cursor.getLong(cursor.getColumnIndexOrThrow("logged_at"))
                    moodEntries.add(MoodEntry(mood, note, answers, loggedAt))
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return moodEntries
    }

    /**
     * Optional: Retrieves mood entries within the last 'n' days.
     *
     * @param days Number of days to look back.
     * @return List of MoodEntry objects within the specified timeframe.
     */
    fun getMoodEntriesWithinDays(days: Long): List<MoodEntry> {
        val moodEntries = mutableListOf<MoodEntry>()
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val currentTime = System.currentTimeMillis()
        val cutoffTime = currentTime - days * 24 * 60 * 60 * 1000 // Convert days to milliseconds

        val query = "SELECT * FROM MoodLogs WHERE logged_at >= ? ORDER BY logged_at DESC"
        val selectionArgs = arrayOf(cutoffTime.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val mood = cursor.getString(cursor.getColumnIndexOrThrow("mood"))
                    val note = cursor.getString(cursor.getColumnIndexOrThrow("note"))
                    // Retrieve answers for questions 1 through 7
                    val answers = (1..7).map { i ->
                        cursor.getString(cursor.getColumnIndexOrThrow("question$i")) ?: "N/A"
                    }
                    val loggedAt = cursor.getLong(cursor.getColumnIndexOrThrow("logged_at"))
                    moodEntries.add(MoodEntry(mood, note, answers, loggedAt))
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return moodEntries
    }
}

/**
 * Data class to represent a full mood entry.
 *
 * @property mood The mood emoji or description.
 * @property note Additional notes or thoughts.
 * @property answers List of answers to the mood questionnaire.
 * @property loggedAt Timestamp in epoch milliseconds.
 */
data class MoodEntry(
    val mood: String,
    val note: String,
    val answers: List<String>,
    val loggedAt: Long // Changed from String to Long to represent epoch milliseconds
)
