package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FontManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class EditNoteFragment : CreateNoteFragment() {

    lateinit var mToolbarListener: OnInflateNewToolbarListener

    interface OnInflateNewToolbarListener {
        fun fragmentCreated(visible: Boolean)
    }

    lateinit var title: String
    lateinit var note: String
    lateinit var font: String
    var position = 0

    override fun onStart() {
        super.onStart()

        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.edit)) //Set toolbar title depending on current fragment

        //Set up toolbar listener
        mToolbarListener.fragmentCreated(true)
        /*
        Get bundle and set at editText's
         */
        title = arguments.getString("title")
        note = arguments.getString("note")
        font = arguments.getString("font")
        position = arguments.getInt("position")

        listenBarOptions(bindingFrag.createNoteTitleField, bindingFrag.createNoteNoteField)

        Log.i("font", "my font " + font)
        FontManager.recogniseAndSetFont(font, bindingFrag.createNoteTitleField, bindingFrag.createNoteNoteField)

        bindingFrag.createNoteTitleField.setText(title)
        bindingFrag.createNoteNoteField.setText(note)
    }

    /*
    When this fragment is destroying then user note is updating or deleting from local SQL database
     */
    override fun onDestroyView() {
        /*
        Update notes in local database
         */
        val whereClause = "title=? AND note=? AND font=?"

        if (!CurrentFragmentState.backPressed && validTitleAndNote()) { //When user click FloatingACtionButton and fields are not empty
            //Update note in database
            val values = ContentValues()
            values.put("title", bindingFrag.createNoteTitleField.text.toString())
            values.put("note", bindingFrag.createNoteNoteField.text.toString())
            values.put("font", currentFont)

            database.use {
                Log.i("font", currentFont)
                update(
                        "notes", values, whereClause, arrayOf(title, note, font)
                )
            }
        } else if ((CurrentFragmentState.backPressed || !CurrentFragmentState.backPressed) && !validTitleAndNote()) { //When user click back button and fields are empty
            //Delete note from database
            database.use {
                delete("notes", whereClause, arrayOf(title, note, font))
            }
        }
        //Set up main toolbar
        mToolbarListener.fragmentCreated(false)
        super.onDestroyView()
    }

    /*
    Check data validation(Fields can not be empty)
     */

    fun validTitleAndNote(): Boolean {
        return !TextUtils.isEmpty(bindingFrag.createNoteTitleField.text.trim()) && !TextUtils.isEmpty(bindingFrag.createNoteNoteField.text.trim())
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mToolbarListener = (activity as OnInflateNewToolbarListener)
        } catch (e: ClassCastException) {
            e.printStackTrace()
            throw ClassCastException(activity.toString() + " must implement OnInflateNewToolbarListener interface")
        }

    }
}