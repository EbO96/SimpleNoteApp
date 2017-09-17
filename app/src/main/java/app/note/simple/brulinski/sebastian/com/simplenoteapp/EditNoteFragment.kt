package app.note.simple.brulinski.sebastian.com.simplenoteapp

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

    override fun onStart() {
        CurrentFragmentState.CURRENT = MainActivity.EDIT_NOTE_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.edit))

        (activity as MainActivity).setToolbarItemsVisibility(false)

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
            if (resources.getBoolean(R.bool.twoPaneMode))
                mSaveListener.passData(binding.createNoteTitleField.text.toString(),
                    binding.createNoteNoteField.text.toString())
            else
                (activity as MainActivity).onBackPressed()
        }
    }

}