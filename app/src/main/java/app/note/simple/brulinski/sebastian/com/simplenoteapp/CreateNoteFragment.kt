package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.app.FragmentTransaction
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


class CreateNoteFragment : Fragment() {


    lateinit var binding: CreateNoteFragmentBinding
    lateinit var database: LocalDatabase

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)

        //Database statement
        database = LocalDatabase(context)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createNoteFab.setOnClickListener {
            saveNote(binding.createNoteTitleField.text.toString(), binding.createNoteNoteField.text.toString())
        }
    }

    override fun onDestroy() {
        activity.supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit()
        super.onDestroy()
    }

    fun saveNote(title: String, note: String) {
        database.addNote(title, note, getCurrentDateAndTime())
    }

    fun getCurrentDateAndTime(): String { //Get current time from system
        val calendar = Calendar.getInstance()

        val df = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a")
        return df.format(calendar.getTime())
    }
}