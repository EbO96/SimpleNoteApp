package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import com.labo.kaji.fragmentanimations.MoveAnimation

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class EditNoteFragment : CreateNoteFragment() {

    /*
    Interfaces
     */
    lateinit var mEditDestroyCallback: OnEditDestroy

    interface OnEditDestroy {
        fun editDestroy(noteObject: ItemsHolder)
    }

    lateinit var title: String
    lateinit var note: String
    var position = 0
    var itemId = ""
    var noteObject = ArrayList<ItemsHolder>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG

        /*
        Get bundle and set at editText's
         */
        itemId = arguments.getString("id")
        title = arguments.getString("title")
        note = arguments.getString("note")
        position = arguments.getInt("position")

        listenBarOptions()

        if (savedInstanceState != null) {
            noteObject = savedInstanceState.getParcelableArrayList("note_object")
            noteObject[0].bgColor = EditorManager.ColorManager.currentBgColor
            noteObject[0].fontStyle = EditorManager.FontStyleManager.currentFontStyle
            noteObject[0].textColor = EditorManager.ColorManager.currentFontColor
        } else {
            noteObject = arguments.getParcelableArrayList<ItemsHolder>("note_object")
            EditorManager.FontStyleManager.currentFontStyle = noteObject[0].fontStyle
            EditorManager.ColorManager.currentBgColor = noteObject[0].bgColor
            EditorManager.ColorManager.currentFontColor = noteObject[0].textColor
        }

        EditorManager.FontStyleManager.recogniseAndSetFont(noteObject[0].fontStyle, bindingFrag.createNoteTitleField,
                bindingFrag.createNoteNoteField)

        val bg = EditorManager.ColorManager(context)

        bg.recogniseAndSetColor(noteObject[0].bgColor, arrayListOf(bindingFrag.createNoteParentCard), "BG") //Change note color

        bg.recogniseAndSetColor(noteObject[0].textColor, arrayListOf(bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField), "FONT") //Change text color

        bindingFrag.createNoteTitleField.setText(title)
        bindingFrag.createNoteNoteField.setText(note)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        (activity as MainActivity).refreshActivity(MainActivity.EDIT_NOTE_FRAGMENT_TAG)
        super.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelableArrayList("note_object", noteObject)
    }

    /*
    When this fragment is destroying then user note is updating or deleting from local SQL database
     */
    override fun onDestroyView() {
        /*
        Update notes in local database
         */
        mEditDestroyCallback.editDestroy(noteObject[0])

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
                values.put("bg_color", EditorManager.ColorManager.currentBgColor)
                values.put("text_color", EditorManager.ColorManager.currentFontColor)
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
        super.onDestroyView()
    }

    /*
    Check data validation(Fields can not be empty)
     */

    fun validTitleAndNote(): Boolean {
        return !TextUtils.isEmpty(bindingFrag.createNoteTitleField.text.trim()) || !TextUtils.isEmpty(bindingFrag.createNoteNoteField.text.trim())
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)

        } else {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        }
    }

    override fun onAttach(context: Context?) {
        try {
            mEditDestroyCallback = (context as OnEditDestroy)
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnEditDestroy")
        }
        super.onAttach(context)

    }
}