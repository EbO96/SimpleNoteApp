package app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem

interface OnRefreshNoteList {
    fun onRefreshList(noteItem: NoteItem)
}