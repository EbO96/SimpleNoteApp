package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotePreviewFragmentBinding

class NotePreviewFragment : Fragment() {

    lateinit var binding: NotePreviewFragmentBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.note_preview_fragment, container, false)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.previewTitleField.text = arguments.getString("title")
        binding.previewNoteField.text = arguments.getString("note")

        binding.previewFab.setOnClickListener {

        }
    }

}