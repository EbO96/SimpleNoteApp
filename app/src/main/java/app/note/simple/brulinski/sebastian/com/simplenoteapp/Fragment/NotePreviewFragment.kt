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
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FragmentAndObjectStates
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding

class NotePreviewFragment : Fragment() {


    lateinit var binding: PreviewCardBinding
    lateinit var titleView: TextView
    lateinit var noteView: TextView
    lateinit var cardView: CardView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)
        titleView = binding.previewTitleField
        noteView = binding.previewNoteField
        cardView = binding.previewCardParentCard

        setupPreview(FragmentAndObjectStates.currentNote)
        return binding.root
    }

    fun setupPreview(noteItem: NoteItem?) {
        if (noteItem != null) {
            val title = noteItem.title
            val note = noteItem.note

            EditorManager.ColorManager((activity as MainActivity)).applyNoteTheme(arrayListOf(titleView, noteView, cardView, EditorManager.ColorManager.ACTION_BAR_COLOR), arrayListOf(noteItem))

            titleView.text = title
            noteView.text = note
        } else setupPreview(FragmentAndObjectStates.getDefaultNote(context))
    }
}