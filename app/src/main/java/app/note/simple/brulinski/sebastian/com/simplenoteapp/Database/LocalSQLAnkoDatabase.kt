package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class LocalSQLAnkoDatabase(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "user_notes.db") {

    companion object {
        private var instance: LocalSQLAnkoDatabase? = null
        val TABLE_NOTES: String = "notes"

        /*
        Column names notes table
         */

        val ID = "_id"
        val TITLE = "title"
        val NOTE = "note"
        val DATE = "date"
        val IS_DELETED = "is_deleted"

        /*
        Column names to properties of note
         */

        val R_BG_COLOR = "r_bg_color"
        val G_BG_COLOR = "g_bg_color"
        val B_BG_COLOR = "b_bg_color"
        val R_TXT_COLOR = "r_txt_color"
        val G_TXT_COLOR = "g_txt_color"
        val B_TXT_COLOR = "b_txt_color"
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
        p0.createTable(TABLE_NOTES, true, ID to INTEGER + PRIMARY_KEY, TITLE to TEXT, NOTE to TEXT, DATE to TEXT, R_BG_COLOR to INTEGER, B_BG_COLOR to INTEGER,
                G_BG_COLOR to INTEGER, R_TXT_COLOR to INTEGER, B_TXT_COLOR to INTEGER, G_TXT_COLOR to INTEGER, FONT_STYLE to TEXT, IS_DELETED to TEXT)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.dropTable(TABLE_NOTES, true)
    }
}

val Context.database: LocalSQLAnkoDatabase
    get() = LocalSQLAnkoDatabase.getInstance(applicationContext)


