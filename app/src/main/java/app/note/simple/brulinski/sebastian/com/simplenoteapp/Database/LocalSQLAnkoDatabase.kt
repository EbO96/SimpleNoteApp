package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class LocalSQLAnkoDatabase(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "user_notes.db") {

    companion object {
        private var instance: LocalSQLAnkoDatabase? = null
        val TABLE_NOTES: String = "notes"
        val TABLE_NOTES_PROPERTIES: String = "notes_properties"

        @Synchronized
        fun getInstance(ctx: Context): LocalSQLAnkoDatabase {
            if (instance == null) {
                instance = LocalSQLAnkoDatabase(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(p0: SQLiteDatabase) {
        p0.createTable(TABLE_NOTES, true, "_id" to INTEGER + PRIMARY_KEY, "title" to TEXT, "note" to TEXT,
                "date" to TEXT)
        p0.createTable(TABLE_NOTES_PROPERTIES, true, "_id" to INTEGER + PRIMARY_KEY, "note_id" to TEXT,
                "bg_color" to TEXT, "text_color" to TEXT, "font_style" to TEXT)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.dropTable(TABLE_NOTES, true)
        p0.dropTable(TABLE_NOTES_PROPERTIES, true)
    }


}

val Context.database: LocalSQLAnkoDatabase
    get() = LocalSQLAnkoDatabase.getInstance(applicationContext)


