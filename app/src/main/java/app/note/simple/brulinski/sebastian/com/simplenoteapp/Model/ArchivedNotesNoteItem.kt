package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

class ArchivedNotesNoteItem(var isSelected: Boolean, id: String, title: String, note: String, date: String?, bgColor: String, textColor: String, fontStyle: String, isDeleted: Boolean, rBGColor: Int, gBGColor: Int, bBGColor: Int, rTXTColor: Int, gTXTColor: Int, bTXTColor: Int): NoteItem(id, title, note, date, rBGColor, gBGColor, bBGColor, rTXTColor, gTXTColor, bTXTColor, fontStyle, isDeleted) {
}