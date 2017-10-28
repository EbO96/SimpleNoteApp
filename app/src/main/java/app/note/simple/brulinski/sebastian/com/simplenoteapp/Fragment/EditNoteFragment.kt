package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FragmentAndObjectStates
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class EditNoteFragment : CreateNoteFragment() {

    /**
     * Keys
     */
    private val SAVE_INSTANCE_STATE_NOTE_OBJECT_KEY = "note_object"

    /**
     * Others
     */
    private var noteItem: NoteItem? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        listenBarOptions()

        setupEdit(FragmentAndObjectStates.currentNote)
        super.onActivityCreated(savedInstanceState)
    }

    private fun setupEdit(noteItem: NoteItem?) {

        if (noteItem != null) {
            val titleView = binding.createNoteTitleField
            val noteView = binding.createNoteNoteField
            val cardView = binding.createNoteParentCard

            val title = noteItem.title
            val note = noteItem.note

            EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(titleView, noteView, cardView, EditorManager.ColorManager.ACTION_BAR_COLOR), arrayListOf(noteItem!!))

            titleView.setText(title)
            noteView.setText(note)
        } else {
            setupEdit(FragmentAndObjectStates.getDefaultNote(context))
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(SAVE_INSTANCE_STATE_NOTE_OBJECT_KEY, noteObject)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) setupEdit(FragmentAndObjectStates.currentNote)
        super.setUserVisibleHint(isVisibleToUser)
    }
}