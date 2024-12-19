package com.grifffith.mindfuljournal.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MindfulJournalDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MindfulJournal.db"
        private const val DATABASE_VERSION = 6

        // Table creation queries
        private const val CREATE_JOURNAL_ENTRIES_TABLE = """
            CREATE TABLE IF NOT EXISTS JournalEntries (
                entry_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                image_path TEXT,
                loves INTEGER DEFAULT 0,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """

        private const val CREATE_MOOD_LOGS_TABLE = """
            CREATE TABLE IF NOT EXISTS MoodLogs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                mood TEXT NOT NULL,
                note TEXT,
                question1 TEXT,
                question2 TEXT,
                question3 TEXT,
                question4 TEXT,
                question5 TEXT,
                question6 TEXT,
                question7 TEXT,
                logged_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """

        private const val CREATE_STEP_COUNTS_TABLE = """
            CREATE TABLE IF NOT EXISTS StepCounts (
                step_id INTEGER PRIMARY KEY AUTOINCREMENT,
                steps INTEGER NOT NULL,
                calories INTEGER,
                distance REAL,
                logged_at DATE NOT NULL UNIQUE
            );
        """

        private const val CREATE_READING_PROGRESS_TABLE = """
            CREATE TABLE IF NOT EXISTS ReadingProgress (
                progress_id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_title TEXT NOT NULL,
                current_page INTEGER NOT NULL CHECK (current_page <= total_pages),
                total_pages INTEGER NOT NULL,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """

        private const val CREATE_STEP_GOALS_TABLE = """
            CREATE TABLE IF NOT EXISTS StepGoals (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                goal INTEGER NOT NULL
            );
        """

        private const val DEFAULT_STEP_GOAL = 10000
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("MindfulJournalDBHelper", "Creating database tables.")
        db.execSQL(CREATE_JOURNAL_ENTRIES_TABLE)
        db.execSQL(CREATE_MOOD_LOGS_TABLE)
        db.execSQL(CREATE_STEP_COUNTS_TABLE)
        db.execSQL(CREATE_READING_PROGRESS_TABLE)
        db.execSQL(CREATE_STEP_GOALS_TABLE)

        // Insert default step goal
        db.execSQL("""
            INSERT INTO StepGoals (goal) VALUES ($DEFAULT_STEP_GOAL);
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("MindfulJournalDBHelper", "Upgrading database from version $oldVersion to $newVersion.")

        // Incremental upgrades based on version differences
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE JournalEntries ADD COLUMN image_path TEXT;")
        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE JournalEntries ADD COLUMN likes INTEGER DEFAULT 0;")
            db.execSQL("ALTER TABLE JournalEntries ADD COLUMN loves INTEGER DEFAULT 0;")
        }

        if (oldVersion < 4) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS StepCounts_new (
                    step_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    steps INTEGER NOT NULL,
                    logged_at DATE NOT NULL UNIQUE
                );
            """)
            db.execSQL("""
                INSERT INTO StepCounts_new (step_id, steps, logged_at)
                SELECT step_id, steps, logged_at FROM StepCounts;
            """)
            db.execSQL("DROP TABLE IF EXISTS StepCounts;")
            db.execSQL("ALTER TABLE StepCounts_new RENAME TO StepCounts;")
        }

        if (oldVersion < 5) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS MoodLogs_new (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    mood TEXT NOT NULL,
                    note TEXT,
                    question1 TEXT,
                    question2 TEXT,
                    question3 TEXT,
                    question4 TEXT,
                    question5 TEXT,
                    question6 TEXT,
                    question7 TEXT,
                    logged_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
            """)
            db.execSQL("""
                INSERT INTO MoodLogs_new (log_id, mood, note, logged_at)
                SELECT log_id, mood, note, logged_at FROM MoodLogs;
            """)
            db.execSQL("DROP TABLE IF EXISTS MoodLogs;")
            db.execSQL("ALTER TABLE MoodLogs_new RENAME TO MoodLogs;")
        }

        if (oldVersion < 6) {
            db.execSQL(CREATE_STEP_GOALS_TABLE)

            // Optional: Add a default step goal if the table is empty
            db.execSQL("""
                INSERT INTO StepGoals (goal) 
                SELECT $DEFAULT_STEP_GOAL WHERE NOT EXISTS (SELECT 1 FROM StepGoals);
            """)
        }
    }
}
