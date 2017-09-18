package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState

class EditNoteFragment : CreateNoteFragment() {

    lateinit var mSaveListener: OnSaveNoteListener

    interface OnSaveNoteListener {
        fun passData(title: String, note: String)
    }

    fun setOnSaveListener(mSaveListener: OnSaveNoteListener) {
        this.mSaveListener = mSaveListener
    }

    lateinit var title: String
    lateinit var note: String
    var position = 0

    override fun onStart() {
        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG

        title = arguments.getString("title")
        note = arguments.getString("note")
        position = arguments.getInt("position")

        bindingFrag.createNoteTitleField.setText(title)
        bindingFrag.createNoteNoteField.setText(note)

        (activity as MainActivity).setOnUpdateListListener(object : MainActivity.OnUpdateListListener {
            override fun passData(title: String, note: String, position: Int) {
                database.updateRow(title, note, bindingFrag.createNoteTitleField.text.toString(),
                        bindingFrag.createNoteNoteField.text.toString())
            }
        })

        super.onStart()
    }
}