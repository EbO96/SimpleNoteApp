package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


/**
 * Created by sebas on 11/4/2017.
 */

class SearchDatabaseTable {

    private val mDatabaseOpenHelper: DatabaseOpenHelper? = null

    companion object {
        private val TAG = "NotesTable"

        //The columns we'll include in dictionary table
        val COL_TITLE = "TITLE"
        val COL_NOTE = "NOTE"

        private val DATABASE_NAME = "NOTES"
        private val FTS_VIRTUAL_TABLE = "FTS"
        private val DATABASE_VERSION = 1

        private class DatabaseOpenHelper internal constructor(private val mHelperContext: Context) : SQLiteOpenHelper(mHelperContext, DATABASE_NAME, null, DATABASE_VERSION) {
            private lateinit var mDatabase: SQLiteDatabase

            override fun onCreate(db: SQLiteDatabase) {
                mDatabase = db
                mDatabase.execSQL(FTS_TABLE_CREATE)
                loadNotes()
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data")
                db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE)
                onCreate(db)
            }

            private fun loadNotes() {
                val notes = ObjectToDatabaseOperations.getObjects(mHelperContext, false)

                for (x in 0 until notes.size) {
                    addNote(notes[x].title!!, notes[x].note!!)
                }
            }

            private fun addNote(title: String, note: String): Long {
                val initialValues = ContentValues()
                initialValues.put(COL_TITLE, title)
                initialValues.put(COL_NOTE, note)
                return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues)
            }

            companion object {

                private val FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        COL_TITLE + ", " +
                        COL_NOTE + ")"
            }
        }
    }

}