package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnSetupPreview
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding

class NotePreviewFragment : Fragment(), OnSetupPreview {
    /**
     * Key's values
     */
    private val NOTE_KEY = "note_object"
    /**
     * Others
     */
    private lateinit var binding: PreviewCardBinding
    private lateinit var titleView: TextView
    private lateinit var noteView: TextView
    private lateinit var cardView: CardView
    private var noteObject: NoteItem = Notes.Note.default

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)
        titleView = binding.previewTitleField
        noteView = binding.previewNoteField
        cardView = binding.previewCardParentCard

        if (savedInstanceState != null)
            onSetup(savedInstanceState.getParcelable(NOTE_KEY))
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putParcelable(NOTE_KEY, noteObject)
        super.onSaveInstanceState(outState)
    }

    override fun onSetup(noteItem: NoteItem) {
        noteObject = noteItem
        val title = noteItem.title
        val note = noteItem.note

        EditorManager.ColorManager((activity as MainActivity)).applyNoteTheme(arrayListOf(titleView, noteView, cardView), arrayListOf(noteItem))

        titleView.text = title
        noteView.text = note
    }
}