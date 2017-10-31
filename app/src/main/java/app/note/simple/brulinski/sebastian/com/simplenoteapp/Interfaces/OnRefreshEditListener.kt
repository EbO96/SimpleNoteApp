package app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import java.text.FieldPosition

/**
 * Created by sebas on 10/31/2017.
 */
interface OnRefreshEditListener {
    fun onRefresh(noteItem: NoteItem)
}