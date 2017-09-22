package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding

class NotePreviewFragment : Fragment() {

    var itemPosition = 0
    var font = ""
    var bcgColor = ""
    lateinit var database: LocalSQLAnkoDatabase

    lateinit var binding: PreviewCardBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.preview))
        database = LocalSQLAnkoDatabase(context)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val title = arguments.getString("title")
        val note = arguments.getString("note")
        itemPosition = arguments.getInt("position")

        binding.previewTitleField.text = title
        binding.previewNoteField.text = note

    }

}