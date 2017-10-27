package app.note.simple.brulinski.sebastian.com.simplenoteapp.Database

import android.content.ContentValues
import android.content.Context
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class ObjectToDatabaseOperations {

    companion object {
        fun insertObject(context: Context, noteObject: NoteItem?) {

            if (noteObject != null) {
                val title = Pair(LocalSQLAnkoDatabase.TITLE, noteObject.title!!.trim())
                val note = Pair(LocalSQLAnkoDatabase.NOTE, noteObject.note!!.trim())
                val date = Pair(LocalSQLAnkoDatabase.DATE, noteObject.date!!)
                val BGColor = Pair(LocalSQLAnkoDatabase.BG_COLOR, noteObject.BGColor.toString())
                val TXTColor = Pair(LocalSQLAnkoDatabase.TXT_COLOR, noteObject.TXTColor.toString())
                val fontStyle = Pair(LocalSQLAnkoDatabase.FONT_STYLE, noteObject.fontStyle)
                val isDeleted = Pair(LocalSQLAnkoDatabase.IS_DELETED, noteObject.isDeleted.toString())

                context.database.use {
                    insert(LocalSQLAnkoDatabase.TABLE_NAME, title, note, date, BGColor, TXTColor, fontStyle, isDeleted)
                }
            }
        }

        fun deleteObjects(context: Context, noteObjects: ArrayList<NoteItem>) {

            (0 until noteObjects.size)
                    .map { noteObjects[it].id }
                    .forEach {
                        context.database.use {
                            delete(LocalSQLAnkoDatabase.TABLE_NAME, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(it.toString()))
                        }
                    }
        }

        fun addDeleteFlag(context: Context, noteObjects: ArrayList<NoteItem>?, flag: Boolean) {
            for (x in 0 until noteObjects!!.size) {
                val id = noteObjects[x].id
                if (id != null) {
                    val isDeletedValue = ContentValues()
                    isDeletedValue.put(LocalSQLAnkoDatabase.IS_DELETED, flag.toString())

                    context.database.use {
                        update(LocalSQLAnkoDatabase.TABLE_NAME, isDeletedValue, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(id.toString())
                        )
                    }
                }
            }
        }

        /**
         * Pass null as 'isDeletedWhereClause' to get all from database. Pass 'true' or 'false' as
         * second parameter to get specific's row's defined by this value 'is_deleted' row
         */
        fun getObjects(context: Context, isDeletedWhereClause: Boolean?): ArrayList<NoteItem> {
            val array = ArrayList<NoteItem>()

            var notes: List<List<Notes.Note>>

            context.database.use {
                notes = if (isDeletedWhereClause == null)
                    select(LocalSQLAnkoDatabase.TABLE_NAME).parseList(MyRowParserNotes())
                else select(LocalSQLAnkoDatabase.TABLE_NAME).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", isDeletedWhereClause.toString()).parseList(MyRowParserNotes())

                val size = notes.size


                (0 until size).mapTo(array) {
                    NoteItem(
                            notes[it][it].id, notes[it][it].title, notes[it][it].note, notes[it][it].date,
                            notes[it][it].BGColor!!, notes[it][it].TXTColor!!, notes[it][it].fontStyle,
                            notes[it][it].isDeleted, notes[it][it].isSelected)
                }
            }
            return array
        }

        fun updateObject(context: Context, noteObjects: ArrayList<NoteItem?>?) {
            val contentValues = ContentValues()
            val db = LocalSQLAnkoDatabase

            if (noteObjects != null) {
                for (x in 0 until noteObjects.size) {
                    val id = noteObjects[x]!!.id

                    if (id != null) {
                        contentValues.put(db.TITLE, noteObjects[x]!!.title)
                        contentValues.put(db.NOTE, noteObjects[x]!!.note)
                        contentValues.put(db.DATE, noteObjects[x]!!.date)
                        contentValues.put(db.BG_COLOR, noteObjects[x]!!.BGColor)
                        contentValues.put(db.TXT_COLOR, noteObjects[x]!!.TXTColor)
                        contentValues.put(db.FONT_STYLE, noteObjects[x]!!.fontStyle)
                        contentValues.put(db.IS_DELETED, noteObjects[x]!!.isDeleted)
                        contentValues.put(db.IS_SELECTED, noteObjects[x]!!.isSelected)

                        context.database.use {
                            update(LocalSQLAnkoDatabase.TABLE_NAME, contentValues, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(id.toString()))
                        }
                    }
                }
            }

        }
    }
}