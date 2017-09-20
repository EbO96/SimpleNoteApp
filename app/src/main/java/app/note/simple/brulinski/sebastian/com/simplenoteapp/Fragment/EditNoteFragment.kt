package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.content.ContentValues
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class EditNoteFragment : CreateNoteFragment() {

    lateinit var title: String
    lateinit var note: String
    var position = 0

    override fun onStart() {
        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.edit))


        title = arguments.getString("title")
        note = arguments.getString("note")
        position = arguments.getInt("position")

        bindingFrag.createNoteTitleField.setText(title)
        bindingFrag.createNoteNoteField.setText(note)

        super.onStart()
    }

    override fun onDestroyView() {
        /*
        Update notes in local database
         */
        if (!CurrentFragmentState.backPressed) {
            val values = ContentValues()
            values.put("title", bindingFrag.createNoteTitleField.text.toString())
            values.put("note", bindingFrag.createNoteNoteField.text.toString())

            val whereClause = "title=? AND note=?"

            database.use {
                update(
                        "notes", values, whereClause, arrayOf(title, note)
                )
            }
        }
        super.onDestroyView()
    }
}