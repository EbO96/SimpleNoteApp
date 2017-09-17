package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.util.Log

class EditNoteFragment : CreateNoteFragment() {


    lateinit var title: String
    lateinit var note: String

    override fun onStart() {
        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.edit))

        (activity as MainActivity).setToolbarItemsVisibility(false)

        title = arguments.getString("title")
        note = arguments.getString("note")

        binding.createNoteTitleField.setText(title)
        binding.createNoteNoteField.setText(note)

        fabListener()
        Log.i("fragState", "Edit" + this.isVisible.toString())
        super.onStart()
    }

    fun fabListener() {
        binding.createNoteFab.setOnClickListener {
            database.updateRow(title, note, binding.createNoteTitleField.text.toString(),
                    binding.createNoteNoteField.text.toString())
            (activity as MainActivity).onBackPressed()
        }
    }
}