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

        if (!resources.getBoolean(R.bool.twoPaneMode))
            (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.edit))


        title = arguments.getString("title")
        note = arguments.getString("note")

        bindingFrag.createNoteTitleField.setText(title)
        bindingFrag.createNoteNoteField.setText(note)

        fabListener()

        super.onStart()
    }

    fun fabListener() {
        bindingFrag.createNoteFab.setOnClickListener {
            database.updateRow(title, note, bindingFrag.createNoteTitleField.text.toString(),
                    bindingFrag.createNoteNoteField.text.toString())
            if (resources.getBoolean(R.bool.twoPaneMode))
                mSaveListener.passData(bindingFrag.createNoteTitleField.text.toString(),
                        bindingFrag.createNoteNoteField.text.toString())
            else
                (activity as MainActivity).onBackPressed()
        }
    }

}