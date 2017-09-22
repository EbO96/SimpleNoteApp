package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import org.jetbrains.anko.db.MapRowParser

/**
 * Created by sebas on 9/19/2017.
 */
class MyRowParserNotes : MapRowParser<List<Notes.Note>> {

    val listUserNotes = ArrayList<Notes.Note>()

    override fun parseRow(columns: Map<String, Any?>): List<Notes.Note> {

        val title = columns.getValue("title")
        val note = columns.getValue("note")
        val date = columns.getValue("date")

        listUserNotes.add(Notes.Note(title = title.toString(), note = note.toString(), date = date.toString()))

        return listUserNotes
    }
}