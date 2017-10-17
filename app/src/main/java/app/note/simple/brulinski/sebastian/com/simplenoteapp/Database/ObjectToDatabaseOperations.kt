package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.Context
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import org.jetbrains.anko.db.insert

class ObjectToDatabaseOperations {

    companion object {
        fun insertNoteObject(noteObject: ItemsHolder, context: Context) {
            val titleCol = Pair<String, String>(LocalSQLAnkoDatabase.TITLE, noteObject.title.trim())
            val noteCol = Pair<String, String>(LocalSQLAnkoDatabase.NOTE, noteObject.note.trim())
            val dateCol = Pair<String, String>(LocalSQLAnkoDatabase.DATE, CreateNoteFragment.getCurrentDateAndTime())
            val isDeletedCol = Pair<String, String>(LocalSQLAnkoDatabase.IS_DELETED, noteObject.isDeleted.toString())

            context.database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES, titleCol, noteCol, dateCol, isDeletedCol)
            }

            val noteIdCol = Pair<String, String>(LocalSQLAnkoDatabase.NOTE_ID, noteObject.id)
            val bgColorCol = Pair<String, String>(LocalSQLAnkoDatabase.BG_COLOR, noteObject.bgColor)
            val textColorCol = Pair<String, String>(LocalSQLAnkoDatabase.TEXT_COLOR, noteObject.textColor)
            val fontStyleCol = Pair<String, String>(LocalSQLAnkoDatabase.FONT_STYLE, noteObject.fontStyle)

            context.database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, noteIdCol, bgColorCol, textColorCol, fontStyleCol, isDeletedCol)
            }
        }

        fun deleteNoteObject(noteObject: ItemsHolder, context: Context) {
            context.database.use {
                delete(LocalSQLAnkoDatabase.TABLE_NOTES, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(noteObject.id))
                delete(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, "${LocalSQLAnkoDatabase.NOTE_ID}=?", arrayOf(noteObject.id))
            }
        }
    }
}