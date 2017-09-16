package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class LocalDatabase(context: Context) : SQLiteOpenHelper(context, "note.db", null, 1) {

    val TABLE: String = "user_notes"

    companion object {
        val TITLE: String = "TITLE"
        val NOTE: String = "NOTE"
        val DATE: String = "DATE"
    }

    val DATABASE_CREATE: String = "CREATE TABLE " + TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${TITLE} TEXT," + " ${NOTE} TEXT, " + "${DATE} TEXT" + ")"

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS " + TABLE)
    }

    fun addNote(title: String, note: String, date: String) {
        val database: SQLiteDatabase = writableDatabase
        val content: ContentValues = ContentValues()
        content.put(TITLE, title)
        content.put(NOTE, note)
        content.put(DATE, date)

        database.insert(TABLE, null, content)
        database.close()
    }

    fun getAllNotes(): Cursor {
        val database: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM ${TABLE}"
        val notes: Cursor = database.rawQuery(query, null)

        return notes
    }

    fun deleteRow(title: String, note: String) {
        val database: SQLiteDatabase = writableDatabase
        val whereClause = "${TITLE}=? AND ${NOTE}=?"
        database.delete(TABLE, whereClause, arrayOf(title, note))
    }
}