package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NotesProperties
import org.jetbrains.anko.db.MapRowParser


class MyRowParserNoteProperties : MapRowParser<List<NotesProperties>> {
    var idArrayList =  ArrayList<NotesProperties>()

    override fun parseRow(columns: Map<String, Any?>): List<NotesProperties> {
        val id = columns.getValue("_id")
        val bgColor = columns.getValue("bg_color")
        val textColor = columns.getValue("text_color")
        val fontStyle = columns.getValue("font_style")

        idArrayList.add(NotesProperties(id = id.toString(), bgColor = bgColor.toString(), textColor = textColor.toString(), fontColor = fontStyle.toString()))

        return idArrayList
    }
}