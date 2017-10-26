package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.ArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NoArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ArchivedNotesNoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityArchivesBinding

class ArchivesActivity : AppCompatActivity() {

    private var archivedNotesArrayList = ArrayList<ArchivedNotesNoteItem>()
    private lateinit var binding: ActivityArchivesBinding
    private val ARCHIVED_TAG = "archived"
    private val NO_ARCHIVED_TAG = "no_archived"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_archives)

        /*
        Set Toolbar
        */
        setSupportActionBar(binding.archivedNotesToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(getString(R.string.archives))

        val notes = getArchivedNotes()
        val args = Bundle()
        if (savedInstanceState == null)
            args.putParcelableArrayList(ArchivedNotesFragment.BUNDLE_KEY, notes)
        else {
            args.putParcelableArrayList(ArchivedNotesFragment.BUNDLE_KEY, savedInstanceState.getParcelableArrayList<ArchivedNotesNoteItem>(ArchivedNotesFragment.BUNDLE_KEY))
        }

        if (notes.size == 0) {
            setNoArchivedNotesFragment()
        } else {
            setArchivedNotesFragment(args)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        val frag = supportFragmentManager.findFragmentById(binding.archivedNotesFragmentContainer.id)
        if (frag is ArchivedNotesFragment) {
            val array = frag.archivedNotesArrayList
            outState!!.putParcelableArrayList(ArchivedNotesFragment.BUNDLE_KEY, array)
        }
        super.onSaveInstanceState(outState)
    }

    fun setToolbarTitle(title: String) {
        supportActionBar!!.title = title
    }

    private fun listenForReplaceFragmentEvent(fragment: ArchivedNotesFragment) {
        fragment.setOnChangeScreenListener(object : ArchivedNotesFragment.OnChangeScreenListener {
            override fun replaceFragment() {
                setNoArchivedNotesFragment()
            }
        })
    }

    private fun setNoArchivedNotesFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = NoArchivedNotesFragment()

        fragmentTransaction.replace(binding.archivedNotesFragmentContainer.id, fragment, NO_ARCHIVED_TAG).commit()
        fragmentManager.executePendingTransactions()
    }

    private fun setArchivedNotesFragment(notesBundle: Bundle) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = ArchivedNotesFragment()
        fragment.arguments = notesBundle

        fragmentTransaction.replace(binding.archivedNotesFragmentContainer.id, fragment, ARCHIVED_TAG).commit()
        fragmentManager.executePendingTransactions()

        listenForReplaceFragmentEvent(fragment)
    }

    private fun getArchivedNotes(): ArrayList<ArchivedNotesNoteItem> {
        archivedNotesArrayList = ArrayList()
        //TODO new implementation of code below
//        val propertiesArray = ArrayList<NotesProperties>()
//
//        database.use {
//            val properties = select(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", "true").
//                    parseList(MyRowParserNoteProperties())
//
//            val size = properties.size
//
//            for (x in 0 until size) {
//                val item = properties[x].get(x)
//                propertiesArray.add(NotesProperties(item.id, item.bgColor, item.textColor, item.fontStyle))
//            }
//        }
//
//        database.use {
//            val notes = select(LocalSQLAnkoDatabase.TABLE_NOTES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", "true").
//                    parseList(MyRowParserNotes())
//            val size = notes.size
//
//            for (x in 0 until size) {
//                val item = notes[x].get(x)
//                val noteObject = ArchivedNotesNoteItem(false, item.id!!, item.title!!, item.note!!, item.date!!, propertiesArray[x].bgColor.toString(),
//                        propertiesArray[x].textColor.toString(), propertiesArray[x].fontStyle.toString(), true)
//                archivedNotesArrayList.add(noteObject)
//            }
//        }
        return archivedNotesArrayList
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
        super.onBackPressed()
    }
}
