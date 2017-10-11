package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class LocalSQLAnkoDatabase(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "user_notes.db") {

    companion object {
        private var instance: LocalSQLAnkoDatabase? = null
        val TABLE_NOTES: String = "notes"
        val TABLE_NOTES_PROPERTIES: String = "notes_properties"

        /*
        Column names notes table
         */

        val ID = "_id"
        val TITLE = "title"
        val NOTE = "note"
        val DATE = "date"
        val IS_DELETED = "is_deleted"

        /*
        Column names note_properties table
         */

        val NOTE_ID = "note_id"
        val BG_COLOR = "bg_color"
        val TEXT_COLOR = "text_color"
        val FONT_STYLE = "font_style"

        @Synchronized
        fun getInstance(ctx: Context): LocalSQLAnkoDatabase {
            if (instance == null) {
                instance = LocalSQLAnkoDatabase(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(p0: SQLiteDatabase) {
        p0.createTable(TABLE_NOTES, true, ID to INTEGER + PRIMARY_KEY, TITLE to TEXT, NOTE to TEXT,
                DATE to TEXT, IS_DELETED to TEXT)
        p0.createTable(TABLE_NOTES_PROPERTIES, true, ID to INTEGER + PRIMARY_KEY, NOTE_ID to TEXT,
                BG_COLOR to TEXT, TEXT_COLOR to TEXT, FONT_STYLE to TEXT, IS_DELETED to TEXT)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.dropTable(TABLE_NOTES, true)
        p0.dropTable(TABLE_NOTES_PROPERTIES, true)
    }
}

val Context.database: LocalSQLAnkoDatabase
    get() = LocalSQLAnkoDatabase.getInstance(applicationContext)


