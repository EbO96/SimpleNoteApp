package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
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
            if (!TextUtils.isEmpty(binding.createNoteTitleField.text.toString().trim()) || !TextUtils.isEmpty(binding.createNoteNoteField.text.toString().trim()))
                saveNote(binding.createNoteTitleField.text.toString(), binding.createNoteNoteField.text.toString())

            (activity as MainActivity).setNotesListFragment(activity.getPreferences(Activity.MODE_PRIVATE).getBoolean(getString(R.string.layout_manager_key), false), false)
            if(resources.getBoolean(R.bool.twoPaneMode))
                (activity as MainActivity).binding.mainFab.visibility = View.GONE
            else (activity as MainActivity).binding.mainFab.visibility = View.VISIBLE
        }
        database = LocalDatabase(context)
        return binding.root
    }

    fun saveNote(title: String, note: String) {
        database.addNote(title.trim(), note.trim(), getCurrentDateAndTime())
    }

    companion object {
        fun getCurrentDateAndTime(): String { //Get current time from system
            val calendar = Calendar.getInstance()

            val df = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a")
            return df.format(calendar.getTime())
        }
    }
}