package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotePreviewFragmentBinding

class NotePreviewFragment : Fragment() {

    lateinit var mListener: OnEditNoteListener

    interface OnEditNoteListener {
        fun passData(title: String, note: String)
    }

    fun setOnEditNoteListener(mListener: OnEditNoteListener) {
        this.mListener = mListener
    }

    lateinit var binding: NotePreviewFragmentBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.note_preview_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val title = arguments.getString("title")
        val note = arguments.getString("note")

        binding.previewTitleField.text = title
        binding.previewNoteField.text = note

        binding.previewFab.setOnClickListener {
            mListener.passData(title, note)
        }
    }

}