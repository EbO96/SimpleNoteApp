package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment() {

    lateinit var binding: CreateNoteFragmentBinding
    lateinit var database: LocalDatabase


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)

        CurrentFragmentState.CURRENT = MainActivity.CREATE_NOTE_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.create))

        //Database statement
        binding.createNoteFab.setOnClickListener {
            saveNote(binding.createNoteTitleField.text.toString(), binding.createNoteNoteField.text.toString())
            (activity as MainActivity).onBackPressed()
        }
        database = LocalDatabase(context)
        return binding.root
    }

    fun saveNote(title: String, note: String) {
        database.addNote(title.trim(), note.trim(), getCurrentDateAndTime())
    }

    fun getCurrentDateAndTime(): String { //Get current time from system
        val calendar = Calendar.getInstance()

        val df = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a")
        return df.format(calendar.getTime())
    }

}