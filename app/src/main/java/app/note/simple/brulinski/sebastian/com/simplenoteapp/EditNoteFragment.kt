package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.util.Log

class EditNoteFragment : CreateNoteFragment() {

    lateinit var onFinishEditListener_: OnFinishEditListener

    interface OnFinishEditListener {
        fun OnFinish()
    }

    fun setOnFinishEditListener(onFinishEditListener: OnFinishEditListener) {
        this.onFinishEditListener_ = onFinishEditListener
    }

    lateinit var title: String
    lateinit var note: String

    override fun onStart() {
        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG
        Log.i("abcd", "fragment " + CurrentFragmentState.CURRENT)
        title = arguments.getString("title")
        note = arguments.getString("note")

        binding.createNoteTitleField.setText(title)
        binding.createNoteNoteField.setText(note)

        fabListener()
        super.onStart()
    }

    fun fabListener() {
        binding.createNoteFab.setOnClickListener {
            database.updateRow(title, note, binding.createNoteTitleField.text.toString(),
                    binding.createNoteNoteField.text.toString())
            onFinishEditListener_.OnFinish()
        }
    }
}