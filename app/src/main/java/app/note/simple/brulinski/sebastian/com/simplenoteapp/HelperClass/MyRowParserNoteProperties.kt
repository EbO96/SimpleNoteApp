package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteID
import org.jetbrains.anko.db.MapRowParser

/**
 * Created by sebas on 9/22/2017.
 */
class MyRowParserNoteProperties : MapRowParser<List<NoteID>> {
    var idArrayList: ArrayList<NoteID> = ArrayList()

    override fun parseRow(columns: Map<String, Any?>): List<NoteID> {
        val id = columns.getValue("_id")

        idArrayList.add(NoteID(id = id.toString()))

        return idArrayList
    }
}