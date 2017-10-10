package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.content.Context
import android.os.Bundle
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
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
        fun editDestroy(noteObject: ItemsHolder?)
    }

    companion object {
        var noteObject: ItemsHolder? = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        listenBarOptions()

        val titleView = bindingFrag.createNoteTitleField
        val noteView = bindingFrag.createNoteNoteField
        val cardView = bindingFrag.createNoteParentCard

        noteObject = MainActivity.noteToEdit

        val title = noteObject!!.title
        val note = noteObject!!.note
        val bgColor = noteObject!!.bgColor
        val fontStyle = noteObject!!.fontStyle
        val textColor = noteObject!!.textColor

        EditorManager.FontStyleManager.recogniseAndSetFont(fontStyle, titleView, noteView)

        val colorManager = EditorManager.ColorManager(context)

        colorManager.recogniseAndSetColor(bgColor, arrayListOf(cardView), "BG")
        colorManager.recogniseAndSetColor(textColor, arrayListOf(titleView, noteView), "FONT")

        titleView.setText(title)
        noteView.setText(note)

        super.onActivityCreated(savedInstanceState)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putParcelable("note_object", noteObject)
    }

    /*
    When this fragment is destroying then user note is updating or deleting from local SQL database
     */
    override fun onDestroyView() {
        val title = bindingFrag.createNoteTitleField.text.toString()
        val note = bindingFrag.createNoteNoteField.text.toString()
        val date = getCurrentDateAndTime()
        val fontStyle = MainActivity.noteToEdit!!.fontStyle
        val textColor = MainActivity.noteToEdit!!.textColor
        val bgColor = MainActivity.noteToEdit!!.bgColor

        noteObject!!.title = title
        noteObject!!.note = note
        noteObject!!.date = date
        noteObject!!.fontStyle = fontStyle
        noteObject!!.textColor = textColor
        noteObject!!.bgColor = bgColor

        mEditDestroyCallback.editDestroy(noteObject)
        super.onDestroyView()
    }

    /*
    Check data validation(Fields can not be empty)
     */

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