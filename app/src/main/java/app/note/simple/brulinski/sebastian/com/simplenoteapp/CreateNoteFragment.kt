package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding

/**
 * Created by sebas on 15.09.2017.
 */
class CreateNoteFragment : Fragment() {

    lateinit var binding: CreateNoteFragmentBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)

        return binding.root
    }

    //TODO onCreateView
}