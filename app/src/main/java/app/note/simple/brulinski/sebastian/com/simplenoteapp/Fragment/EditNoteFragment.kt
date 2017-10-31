package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FragmentAndObjectStates
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class EditNoteFragment : CreateNoteFragment() {

    /**
     * Keys
     */
    private val SAVE_INSTANCE_STATE_NOTE_OBJECT_KEY = "note_object"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i("interLog", "$this, onActivityCreated()")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listenBarOptions()
        }
        setupEdit(FragmentAndObjectStates.currentNote)

        super.onActivityCreated(savedInstanceState)
    }

    fun setupEdit(noteItem: NoteItem?) {
        if (noteItem != null) {

            val titleView = binding.createNoteTitleField
            val noteView = binding.createNoteNoteField
            val cardView = binding.createNoteParentCard

            val title = noteItem.title
            val note = noteItem.note

            Log.i("interLog", "title = $title \n note = $note")

            EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(titleView, noteView, cardView, EditorManager.ColorManager.ACTION_BAR_COLOR), arrayListOf(noteItem!!))

            titleView.setText(title)
            noteView.setText(note)
        } else {
            setupEdit(FragmentAndObjectStates.getDefaultNote(context))
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            setupEdit(FragmentAndObjectStates.currentNote)
        }
        super.setUserVisibleHint(isVisibleToUser)
    }
}