package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import org.jetbrains.anko.db.insert
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment() {

    lateinit var bindingFrag: CreateNoteFragmentBinding
    lateinit var database: LocalSQLAnkoDatabase


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingFrag = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)

        CurrentFragmentState.CURRENT = MainActivity.CREATE_NOTE_FRAGMENT_TAG

        database = LocalSQLAnkoDatabase(context)

        return bindingFrag.root
    }

    fun saveNote(title: String, note: String) {
        try {

            database.use {
                val titleCol = Pair<String, String>("title", title.trim())
                val noteCol = Pair<String, String>("note", note.trim())
                val dateCol = Pair<String, String>("date", getCurrentDateAndTime())

                insert("notes", titleCol, noteCol, dateCol)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    companion object {
        fun getCurrentDateAndTime(): String { //Get current time from system
            val calendar = Calendar.getInstance()

            val df = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a")
            return df.format(calendar.getTime())
        }
    }

    override fun onDestroyView() {
        if ((!TextUtils.isEmpty(bindingFrag.createNoteTitleField.text.toString().trim()) || !TextUtils.isEmpty(bindingFrag.createNoteNoteField.text.toString().trim())) &&
                CurrentFragmentState.CURRENT.equals(MainActivity.CREATE_NOTE_FRAGMENT_TAG) && !CurrentFragmentState.backPressed)
            saveNote(bindingFrag.createNoteTitleField.text.toString(), bindingFrag.createNoteNoteField.text.toString())
        super.onDestroyView()
    }
}


