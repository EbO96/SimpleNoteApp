package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.ArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NoArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityArchivesBinding

class ArchivesActivity : AppCompatActivity() {

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

        val notes = ObjectToDatabaseOperations.getObjects(context = this, isDeletedWhereClause = true) //Get deleted notes from database
        val args = Bundle()
        if (savedInstanceState == null)
            args.putParcelableArrayList(ArchivedNotesFragment.BUNDLE_KEY, notes)
        else {
            args.putParcelableArrayList(ArchivedNotesFragment.BUNDLE_KEY, savedInstanceState.getParcelableArrayList<NoteItem>(ArchivedNotesFragment.BUNDLE_KEY))
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
