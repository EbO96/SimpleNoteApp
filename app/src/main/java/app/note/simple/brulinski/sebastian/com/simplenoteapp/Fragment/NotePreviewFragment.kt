package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotePreviewFragmentBinding
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding
import org.jetbrains.anko.support.v4.nestedScrollView
import java.lang.reflect.Executable
import java.security.spec.ECField

class NotePreviewFragment : Fragment() {

    lateinit var mListener: OnEditNoteListener

    var itemPosition = 0

    interface OnEditNoteListener {
        fun passData(title: String, note: String)
    }

    fun setOnEditNoteListener(mListener: OnEditNoteListener) {
        this.mListener = mListener
    }

    lateinit var binding: PreviewCardBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.preview))

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