package app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem

/**
 * Created by sebas on 10/27/2017.
 */
interface AfterEditListener {
    fun updateEditedNoteItemObject(noteItem: NoteItem?)
}