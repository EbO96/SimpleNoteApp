package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class LocalSQLAnkoDatabase(ctx: Context): ManagedSQLiteOpenHelper(ctx, "user_notes.db") {

    companion object {
        private var instance: LocalSQLAnkoDatabase? = null
        val TABLE: String = "notes"

        @Synchronized
        fun getInstance(ctx: Context): LocalSQLAnkoDatabase{
            if(instance == null){
                instance = LocalSQLAnkoDatabase(ctx.applicationContext)
            }
                return instance!!
        }
    }

    override fun onCreate(p0: SQLiteDatabase) {
        p0.createTable(TABLE, true, "_id" to INTEGER + PRIMARY_KEY, "title" to TEXT, "note" to TEXT,
                "date" to TEXT, "font" to TEXT)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        p0.dropTable(TABLE, true)
    }


}

val Context.database: LocalSQLAnkoDatabase
    get() = LocalSQLAnkoDatabase.getInstance(applicationContext)


