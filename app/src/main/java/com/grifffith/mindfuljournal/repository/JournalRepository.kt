import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.grifffith.mindfuljournal.db.MindfulJournalDBHelper

// Data class to represent a journal entry
data class JournalEntry(
    val entryId: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val imagePath: String?,
    var loves: Int = 0
)

class JournalRepository(private val dbHelper: MindfulJournalDBHelper) {

    companion object {
        private const val TABLE_NAME = "JournalEntries"
        private const val COLUMN_ID = "entry_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_IMAGE_PATH = "image_path"
        private const val COLUMN_LOVES = "loves"
    }

    // Add a new journal entry
    fun addJournalEntry(title: String, content: String, dateTime: String, imagePath: String?) {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
            put(COLUMN_CREATED_AT, dateTime)
            put(COLUMN_IMAGE_PATH, imagePath)
            put(COLUMN_LOVES, 0) // Initialize loves with 0 (not loved)
        }

        dbHelper.writableDatabase.use { db ->
            try {
                db.insert(TABLE_NAME, null, values)
            } catch (e: Exception) {
                Log.e("JournalRepository", "Error adding journal entry: ${e.message}", e)
            }
        }
    }

    // Retrieve all journal entries
    fun getAllJournalEntries(): List<JournalEntry> {
        val entries = mutableListOf<JournalEntry>()
        dbHelper.readableDatabase.use { db ->
            var cursor: Cursor? = null
            try {
                cursor = db.query(
                    TABLE_NAME,
                    arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_CREATED_AT, COLUMN_IMAGE_PATH, COLUMN_LOVES),
                    null, null, null, null,
                    "$COLUMN_CREATED_AT DESC"
                )
                while (cursor.moveToNext()) {
                    entries.add(cursorToEntry(cursor))
                }
            } catch (e: Exception) {
                Log.e("JournalRepository", "Error retrieving journal entries: ${e.message}", e)
            } finally {
                cursor?.close()
            }
        }
        return entries
    }

    // Retrieve all loved journal entries
    fun getLovedJournalEntries(): List<JournalEntry> {
        val lovedEntries = mutableListOf<JournalEntry>()
        dbHelper.readableDatabase.use { db ->
            var cursor: Cursor? = null
            try {
                cursor = db.query(
                    TABLE_NAME,
                    arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_CREATED_AT, COLUMN_IMAGE_PATH, COLUMN_LOVES),
                    "$COLUMN_LOVES = ?",
                    arrayOf("1"),
                    null,
                    null,
                    "$COLUMN_CREATED_AT DESC"
                )
                while (cursor.moveToNext()) {
                    lovedEntries.add(cursorToEntry(cursor))
                }
            } catch (e: Exception) {
                Log.e("JournalRepository", "Error retrieving loved journal entries: ${e.message}", e)
            } finally {
                cursor?.close()
            }
        }
        return lovedEntries
    }

    // Delete a journal entry by its ID
    fun deleteJournalEntry(entryId: String) {
        dbHelper.writableDatabase.use { db ->
            try {
                db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(entryId))
            } catch (e: Exception) {
                Log.e("JournalRepository", "Error deleting journal entry: ${e.message}", e)
            }
        }
    }

    // Update a journal entry's content and image path
    fun updateJournalEntry(entryId: String, newContent: String, newImagePath: String?) {
        val values = ContentValues().apply {
            put(COLUMN_CONTENT, newContent)
            put(COLUMN_IMAGE_PATH, newImagePath)
        }

        dbHelper.writableDatabase.use { db ->
            try {
                db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(entryId))
            } catch (e: Exception) {
                Log.e("JournalRepository", "Error updating journal entry: ${e.message}", e)
            }
        }
    }

    // Update the loves count of a journal entry
    fun updateLoves(entryId: String, newLoves: Int) {
        val values = ContentValues().apply {
            put(COLUMN_LOVES, newLoves)
        }

        dbHelper.writableDatabase.use { db ->
            try {
                db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(entryId))
            } catch (e: Exception) {
                Log.e("JournalRepository", "Error updating loves: ${e.message}", e)
            }
        }
    }

    private fun cursorToEntry(cursor: Cursor): JournalEntry {
        val entryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
        val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
        val loves = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOVES))
        return JournalEntry(entryId, title, content, createdAt, imagePath, loves)
    }
}
