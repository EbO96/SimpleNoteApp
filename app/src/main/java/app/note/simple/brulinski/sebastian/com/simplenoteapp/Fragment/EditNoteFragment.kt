package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import com.labo.kaji.fragmentanimations.MoveAnimation

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class EditNoteFragment : CreateNoteFragment() {

    lateinit var mToolbarListener: OnInflateNewToolbarListener

    interface OnInflateNewToolbarListener {
        fun fragmentCreated(visible: Boolean)
    }

    lateinit var title: String
    lateinit var note: String
    var position = 0
    var itemId = ""

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.edit)) //Set toolbar title depending on current fragment

        //Set up toolbar listener
        mToolbarListener.fragmentCreated(true)
        /*
        Get bundle and set at editText's
         */
        itemId = arguments.getString("id")
        title = arguments.getString("title")
        note = arguments.getString("note")
        position = arguments.getInt("position")
        val noteObject = arguments.getParcelableArrayList<ItemsHolder>("note_object")

        listenBarOptions()

        EditorManager.FontStyleManager.recogniseAndSetFont(noteObject[0].fontStyle, bindingFrag.createNoteTitleField,
                bindingFrag.createNoteNoteField)

        val bg = EditorManager.BackgroundColorManager(context)

        bg.recogniseAndSetBackgroundColor(noteObject[0].bgColor, bindingFrag.createNoteParentCard)

        bindingFrag.createNoteTitleField.setText(title)
        bindingFrag.createNoteNoteField.setText(note)

        if (savedInstanceState != null) {
            EditorManager.FontStyleManager.recogniseAndSetFont(EditorManager.FontStyleManager.currentFontStyle, bindingFrag.createNoteTitleField,
                    bindingFrag.createNoteNoteField)
            val bcg = EditorManager.BackgroundColorManager(context)
            bcg.recogniseAndSetBackgroundColor(EditorManager.BackgroundColorManager.currentBgColor, bindingFrag.createNoteParentCard)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    /*
    When this fragment is destroying then user note is updating or deleting from local SQL database
     */
    override fun onDestroyView() {
        /*
        Update notes in local database
         */
        var whereClause = "title=? AND note=?"

        if (!CurrentFragmentState.backPressed && validTitleAndNote()) { //When user click FloatingACtionButton and fields are not empty
            //Update note in database
            var values = ContentValues()
            values.put("title", bindingFrag.createNoteTitleField.text.toString())
            values.put("note", bindingFrag.createNoteNoteField.text.toString())

            database.use {
                update(
                        LocalSQLAnkoDatabase.TABLE_NOTES, values, whereClause, arrayOf(title, note)
                )

                whereClause = "note_id=?"

                values = ContentValues()
                values.put("bg_color", EditorManager.BackgroundColorManager.currentBgColor)
                values.put("text_color", EditorManager.FontColorManager.currentFontColor)
                values.put("font_style", EditorManager.FontStyleManager.currentFontStyle)

                database.use {
                    update(
                            LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, values, whereClause, arrayOf(itemId)
                    )
                }
            }
        } else if ((CurrentFragmentState.backPressed || !CurrentFragmentState.backPressed) && !validTitleAndNote()) { //When user click back button and fields are empty
            //Delete note from database
            database.use {
                delete(LocalSQLAnkoDatabase.TABLE_NOTES, whereClause, arrayOf(title, note))
            }
                whereClause = "note_id=?"
            database.use {
                delete(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, whereClause, arrayOf(itemId))
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
            throw ClassCastException(activity.toString() + " must implement OnInflateNewToolbarListener and interface")
        }

    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        } else {
            if (enter) {
                return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            } else {
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            }
        }
    }
}