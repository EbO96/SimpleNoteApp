package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.Context
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import org.jetbrains.anko.db.insert

class ObjectToDatabaseOperations {

    //TODO new database operations
    companion object {
        fun insertObject(context: Context, noteObject: NoteItem) {

            val title = Pair<String, String>(LocalSQLAnkoDatabase.TITLE, noteObject.title.trim())
            val note = Pair<String, String>(LocalSQLAnkoDatabase.NOTE, noteObject.note.trim())
            val date = Pair<String, String>(LocalSQLAnkoDatabase.DATE, noteObject.date!!)
            val rBGColor = Pair<String, Int>(LocalSQLAnkoDatabase.R_BG_COLOR, noteObject.rBGColor)
            val bBGColor = Pair<String, Int>(LocalSQLAnkoDatabase.B_BG_COLOR, noteObject.bBGColor)
            val gBGColor = Pair<String, Int>(LocalSQLAnkoDatabase.G_BG_COLOR, noteObject.gBGColor)
            val rTXTColor = Pair<String, Int>(LocalSQLAnkoDatabase.R_TXT_COLOR, noteObject.rTXTColor)
            val bTXTColor = Pair<String, Int>(LocalSQLAnkoDatabase.B_TXT_COLOR, noteObject.bTXTColor)
            val gTXTColor = Pair<String, Int>(LocalSQLAnkoDatabase.G_TXT_COLOR, noteObject.gTXTColor)
            val fontStyle = Pair<String, String>(LocalSQLAnkoDatabase.FONT_STYLE, noteObject.fontStyle)
            val isDeleted = Pair<String, String>(LocalSQLAnkoDatabase.IS_DELETED, noteObject.isDeleted.toString())

            context.database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES, title, note, date, rBGColor, bBGColor, gBGColor, rTXTColor, bTXTColor, gTXTColor,
                        fontStyle, isDeleted)
            }
        }

        fun deleteObject(context: Context, noteObject: NoteItem) {
            val id = noteObject.id

            context.database.use {
                delete(LocalSQLAnkoDatabase.TABLE_NOTES, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(id))
            }
        }
    }
}