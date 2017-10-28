package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.ColorCreator
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.AfterEditListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import com.labo.kaji.fragmentanimations.MoveAnimation

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class EditNoteFragment : CreateNoteFragment() {

    /*
    Interfaces
     */
    lateinit var mEditDestroyCallback: OnEditDestroy
    lateinit var mAfterEditListener: AfterEditListener

    interface OnEditDestroy {
        fun editDestroy(noteObject: NoteItem?)
    }

    companion object {
        var noteObject: NoteItem? = null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        listenBarOptions()

        val titleView = binding.createNoteTitleField
        val noteView = binding.createNoteNoteField
        val cardView = binding.createNoteParentCard

        noteObject = (activity as MainActivity).getNotes()[0]//arguments.getParcelable(MainActivity.NOTE_TO_EDIT_EXTRA_KEY)

        if (savedInstanceState != null) {
            noteObject = CreateNoteFragment.noteObject

        } else {
            CreateNoteFragment.noteObject = noteObject
        }

        val title = noteObject!!.title
        val note = noteObject!!.note

        EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(titleView, noteView, cardView, EditorManager.ColorManager.ACTION_BAR_COLOR), arrayListOf(noteObject!!))

        titleView.setText(title)
        noteView.setText(note)

        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        mAfterEditListener.updateEditedNoteItemObject(setNoteObject())
        super.onDestroyView()
    }

    fun setNoteObject(): NoteItem {
        return NoteItem(noteObject!!.id, binding.createNoteTitleField.text.toString().trim(), binding.createNoteNoteField.text.toString().trim(), getCurrentDateAndTime(),
                ColorCreator.getColorFromCard(activity, binding.createNoteParentCard),
                ColorCreator.getColorIntFromColorStateList(binding.createNoteTitleField.textColors), EditorManager.FontStyleManager.DEFAULT_FONT,
                false, false
        )
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putParcelable("note_object", noteObject)
    }
    //TODO
//    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
//
//        return if (CurrentFragmentState.backPressed) {
//            MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
//
//        } else {
//            MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
//        }
//    }

    override fun onAttach(context: Context?) {
        try {
            mEditDestroyCallback = (context as OnEditDestroy)
            mAfterEditListener = (context as AfterEditListener)
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnEditDestroy")
        }
        super.onAttach(context)
    }
}