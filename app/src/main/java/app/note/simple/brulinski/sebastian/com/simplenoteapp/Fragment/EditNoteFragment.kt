package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.content.ContentValues
import android.text.TextUtils
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
        val whereClause = "title=? AND note=?"

        if (!CurrentFragmentState.backPressed && validTitleAndNote()) {

            val values = ContentValues()
            values.put("title", bindingFrag.createNoteTitleField.text.toString())
            values.put("note", bindingFrag.createNoteNoteField.text.toString())

            database.use {
                update(
                        "notes", values, whereClause, arrayOf(title, note)
                )
            }
        } else {
            database.use {
                delete("notes", whereClause, arrayOf(title, note))
            }
        }
        super.onDestroyView()
    }

    fun validTitleAndNote(): Boolean {
        return !TextUtils.isEmpty(bindingFrag.createNoteTitleField.text.trim()) && !TextUtils.isEmpty(bindingFrag.createNoteNoteField.text.trim())
    }
}