import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.grifffith.mindfuljournal.db.MindfulJournalDBHelper

class FitnessRepository(private val dbHelper: MindfulJournalDBHelper) {

    companion object {
        const val DEFAULT_STEP_GOAL = 10000
    }

    fun getStepGoal(): Int {
        return try {
            dbHelper.readableDatabase.use { db ->
                val cursor = db.query("StepGoals", arrayOf("goal"), null, null, null, null, null)
                if (cursor.moveToFirst()) {
                    val goal = cursor.getInt(cursor.getColumnIndexOrThrow("goal"))
                    cursor.close()
                    goal
                } else {
                    insertDefaultStepGoal(db)
                    DEFAULT_STEP_GOAL
                }
            }
        } catch (e: Exception) {
            Log.e("FitnessRepository", "Error retrieving step goal", e)
            DEFAULT_STEP_GOAL
        }
    }

    suspend fun setStepGoal(goal: Int) {
        try {
            dbHelper.writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put("goal", goal)
                }
                val result = db.insertWithOnConflict(
                    "StepGoals",
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
                if (result == -1L) {
                    Log.e("FitnessRepository", "Failed to insert/update step goal.")
                } else {
                    Log.i("FitnessRepository", "Step goal updated to $goal")
                }
            }
        } catch (e: Exception) {
            Log.e("FitnessRepository", "Error setting step goal", e)
        }
    }

    fun calculateTodaySteps(totalSteps: Int): Int {
        return try {
            totalSteps // Placeholder for logic to calculate today's steps
        } catch (e: Exception) {
            Log.e("FitnessRepository", "Error calculating today's steps", e)
            0
        }
    }

    private fun insertDefaultStepGoal(db: SQLiteDatabase) {
        try {
            val values = ContentValues().apply {
                put("goal", DEFAULT_STEP_GOAL)
            }
            db.insertWithOnConflict(
                "StepGoals",
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
            )
            Log.i("FitnessRepository", "Default step goal inserted: $DEFAULT_STEP_GOAL")
        } catch (e: Exception) {
            Log.e("FitnessRepository", "Error inserting default step goal", e)
        }
    }
}
