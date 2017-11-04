package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class EditNoteFragment : CreateNoteFragment() {
    /**
     * Keys
     */
    private val SAVE_INSTANCE_STATE_POSITION_KEY = "note_object"

    /**
     * Others
     */
    companion object {
        var noteObject: NoteItem = Notes.Note.default
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i("interLog", "$this, onActivityCreated()")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listenBarOptions()
        }
        if (savedInstanceState != null)
            setupEdit(savedInstanceState.getParcelable(SAVE_INSTANCE_STATE_POSITION_KEY))
        super.onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putParcelable(SAVE_INSTANCE_STATE_POSITION_KEY, noteObject)
        super.onSaveInstanceState(outState)
    }


    fun setupEdit(noteItem: NoteItem?) {
        if (noteItem != null) {

            val titleView = binding.createNoteTitleField
            val noteView = binding.createNoteNoteField
            val cardView = binding.createNoteParentCard

            val title = noteItem.title
            val note = noteItem.note

            Log.i("interLog", "title = $title \n note = $note")

            EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(titleView, noteView, cardView), arrayListOf(noteItem))

            titleView.setText(title)
            noteView.setText(note)
        }
    }
}