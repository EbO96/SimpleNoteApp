package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.ArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NoArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNoteProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NotesProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityArchivesBinding
import org.jetbrains.anko.db.select

class ArchivesActivity : AppCompatActivity() {

    private var archivedNotesArrayList = ArrayList<ItemsHolder>()
    private lateinit var binding: ActivityArchivesBinding
    private lateinit var deleteMenuItem: MenuItem
    private lateinit var itemsToDelete: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_archives)

        /*
        Set Toolbar
        */
        setSupportActionBar(binding.archivedNotesToolbar)
        setToolbarTitle(getString(R.string.archives))

        val notes = getArchivedNotes()
        val args = Bundle()
        args.putParcelableArrayList("archivedNoteObject", notes)

        if (notes.size == 0) {
            setNoArchivedNotesFragment()
        } else {
            setArchivedNotesFragment(args)
        }

        listenForActionMode() //Listen action mode
    }

    private fun setToolbarTitle(title: String) {
        supportActionBar!!.title = title
    }

    private fun listenForActionMode() {
        val fragment = supportFragmentManager.findFragmentById(binding.archivedNotesFragmentContainer.id)
        if(fragment is ArchivedNotesFragment){
            fragment.setOnActionModeListener(object : ArchivedNotesFragment.OnActionModeListener {

                override fun selectedItems(numberOfItems: Int, itemsIdArrayList: ArrayList<String>?) {
                    if (numberOfItems > 0) {
                        deleteMenuItem.isVisible = true
                        setToolbarTitle("$numberOfItems ${getString(R.string.items_selected)}")
                    } else {
                        deleteMenuItem.isVisible = false
                        setToolbarTitle(getString(R.string.archives))
                    }
                    if (itemsIdArrayList != null)  //After delete
                        itemsToDelete = itemsIdArrayList

                }
            })
        }
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

    private fun getArchivedNotes(): ArrayList<ItemsHolder> {
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
                val noteObject = ItemsHolder(item.id!!, item.title!!, item.note!!, item.date!!, propertiesArray[x].bgColor.toString(),
                        propertiesArray[x].textColor.toString(), propertiesArray[x].fontStyle.toString())
                archivedNotesArrayList.add(noteObject)
            }
        }
        return archivedNotesArrayList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.archives_menu, menu)
        deleteMenuItem = menu!!.findItem(R.id.archives_delete).setVisible(false)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.archives_delete -> {
                makeDeleteConfirmDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeDeleteConfirmDialog() {
        val alert = AlertDialog.Builder(this).create()

        alert.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_delete_black_24dp))
        alert.setTitle(getString(R.string.delete_this_note))

        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), { _, i ->
            val fragment = supportFragmentManager.findFragmentById(binding.archivedNotesFragmentContainer.id) as ArchivedNotesFragment
            fragment.deleteSelectedItems(itemsToDelete)
        })

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), { _, i ->
            //Do nothing
        })

        alert.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}
