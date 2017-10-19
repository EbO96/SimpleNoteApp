package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.ArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NoArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNoteProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ArchivedNotesItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NotesProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityArchivesBinding
import org.jetbrains.anko.db.select

class ArchivesActivity : AppCompatActivity() {

    private var archivedNotesArrayList = ArrayList<ArchivedNotesItemsHolder>()
    private lateinit var binding: ActivityArchivesBinding

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
        args.putParcelableArrayList("archivedNoteObject", notes)

        if (notes.size == 0) {
            setNoArchivedNotesFragment()
        } else {
            setArchivedNotesFragment(args)
        }
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

        fragmentTransaction.replace(binding.archivedNotesFragmentContainer.id, fragment).commit()
        fragmentManager.executePendingTransactions()
    }

    private fun setArchivedNotesFragment(notesBundle: Bundle) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = ArchivedNotesFragment()
        fragment.arguments = notesBundle

        fragmentTransaction.replace(binding.archivedNotesFragmentContainer.id, fragment).commit()
        fragmentManager.executePendingTransactions()

        listenForReplaceFragmentEvent(fragment)
    }

    private fun getArchivedNotes(): ArrayList<ArchivedNotesItemsHolder> {
        archivedNotesArrayList = ArrayList()

        val propertiesArray = ArrayList<NotesProperties>()

        database.use {
            val properties = select(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", "true").
                    parseList(MyRowParserNoteProperties())

            val size = properties.size

            for (x in 0 until size) {
                val item = properties[x].get(x)
                propertiesArray.add(NotesProperties(item.id, item.bgColor, item.textColor, item.fontStyle))
            }
        }

        database.use {
            val notes = select(LocalSQLAnkoDatabase.TABLE_NOTES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", "true").
                    parseList(MyRowParserNotes())
            val size = notes.size

            for (x in 0 until size) {
                val item = notes[x].get(x)
                val noteObject = ArchivedNotesItemsHolder(false, item.id!!, item.title!!, item.note!!, item.date!!, propertiesArray[x].bgColor.toString(),
                        propertiesArray[x].textColor.toString(), propertiesArray[x].fontStyle.toString(), true)
                archivedNotesArrayList.add(noteObject)
            }
        }
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
