package app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem

/**
 * Created by sebas on 10/31/2017.
 */
interface OnRefreshPreviewListener {
    fun onRefresh(noteItem: NoteItem?)
    fun onReset()
}