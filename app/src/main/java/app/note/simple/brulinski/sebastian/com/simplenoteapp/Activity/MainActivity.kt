package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.EditNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotePreviewFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.RecyclerMainInterface
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NotesListFragment.OnListenRecyclerScroll, SearchView.OnQueryTextListener,
        EditNoteFragment.OnEditDestroy, RecyclerMainInterface {


    lateinit var mSearchCallback: OnSearchResultListener

    interface OnSearchResultListener {
        fun passNewText(newText: String?)
    }

    fun setOnSearchResultListener(mSearchCallback: OnSearchResultListener) {
        this.mSearchCallback = mSearchCallback
    }

    lateinit var binding: ActivityMainBinding
    lateinit var fm: FragmentManager
    lateinit var ft: FragmentTransaction
    lateinit var noteFragment: Fragment
    lateinit var managerStyle: LayoutManagerStyle
    var doubleTapToExit = false

    companion object {
        var NOTE_LIST_FRAGMENT_TAG: String = "NOTES" //Fragment TAG
        var CREATE_NOTE_FRAGMENT_TAG: String = "CREATE" //Fragment TAG
        var EDIT_NOTE_FRAGMENT_TAG: String = "EDIT" //Fragment TAG
        var NOTE_PREVIEW_FRAGMENT_TAG: String = "PREVIEW" //Fragment TAG
        lateinit var menuItemGrid: MenuItem //Toolbar menu item (set recycler view layout as staggered layout)
        lateinit var menuItemLinear: MenuItem//Toolbar menu item (set recycler view layout as linear layout)
        lateinit var menuItemSearch: MenuItem //Toolbar menu item (set recycler view layout as linear layout)
        var noteToEdit: ItemsHolder? = null
    }

    /*
    mLayoutListener - it is a interface between MainActivity and NoteListFragment. Used to
    changeMenuItemsVisibility layout manager
     */
    lateinit var mLayoutListener: OnChangeLayoutListener

    interface OnChangeLayoutListener {
        fun passData(flag: Boolean)
    }

    fun setOnChangeLayoutListener(mLayoutListener: OnChangeLayoutListener) {
        this.mLayoutListener = mLayoutListener
    }

    /*
    There we starts...
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        managerStyle = LayoutManagerStyle(this)

        /*
        Set main fragment  (NoteListFragment.kt) manually  only once when saveInstanceState is null
        At the same time we getting layout manager type from SharedPreferences
         */
        if (savedInstanceState == null)
            setNotesListFragment()

        floatingActionButtonListener() //Listen for floating action button actions and click

    } //END OF onCreate(...)

    /*
    This function replace another fragment in FrameLayout container by our main Fragment (NoteListFragment.kt - display our notes)
     */
    private fun setNotesListFragment() {
        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val notesListFragment = NotesListFragment()

        noteFragment = notesListFragment
        ft.replace(binding.mainContainer.id, notesListFragment, NOTE_LIST_FRAGMENT_TAG)

        ft.addToBackStack(NOTE_LIST_FRAGMENT_TAG)

        ft.commit()
        fm.executePendingTransactions()
    }

    /*
    Fragment used to create new note
     */
    private fun setCreateNoteFragment() {
        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val createNoteFragment = CreateNoteFragment()

        ft.replace(binding.mainContainer.id, createNoteFragment, CREATE_NOTE_FRAGMENT_TAG)
        ft.addToBackStack(CREATE_NOTE_FRAGMENT_TAG)
        ft.commit()
    }

    /*
    This fragment is setting only from preview mode (NotePreviewFragment.kt)
     */
    private fun setEditNoteFragment() {

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val editNoteFragment = EditNoteFragment()

        val containerId = binding.mainContainer.id

        ft.replace(containerId, editNoteFragment, EDIT_NOTE_FRAGMENT_TAG)

        ft.addToBackStack(EDIT_NOTE_FRAGMENT_TAG)

        ft.commit()
    }

    /*
    This fragment is setting when user click RecyclerView item
     */
    private fun setNotePreviewFragment() {

        val previewFragment = NotePreviewFragment()

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val containerID = binding.mainContainer.id
        ft.replace(containerID, previewFragment, NOTE_PREVIEW_FRAGMENT_TAG)
        ft.addToBackStack(NOTE_PREVIEW_FRAGMENT_TAG)
        ft.commit()
    }

    /*
    Create menu at Toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        menuItemGrid = menu!!.findItem(R.id.main_menu_grid)
        menuItemLinear = menu.findItem(R.id.main_menu_linear)
        menuItemSearch = menu.findItem(R.id.search)

        val searchView: SearchView = (menu.findItem(R.id.search).actionView as SearchView)
        searchView.queryHint = getString(R.string.search_hint) //Set search hint

        searchView.setOnQueryTextListener(this)

        val frag = supportFragmentManager.findFragmentById(findViewById<FrameLayout>(R.id.main_container).id)

        var drawIcon: Drawable? = null
        var title = resources.getString(R.string.notes)


        if (frag is NotesListFragment) {
            drawIcon = resources.getDrawable(R.drawable.ic_add_white_24dp)
            title = resources.getString(R.string.notes)

        } else if (frag is NotePreviewFragment) {
            drawIcon = resources.getDrawable(R.drawable.ic_mode_edit_white_24dp)
            title = resources.getString(R.string.preview)
        } else if (frag is EditNoteFragment && frag.tag.equals(EDIT_NOTE_FRAGMENT_TAG)) {
            drawIcon = resources.getDrawable(R.drawable.ic_done_white_24dp)
            title = resources.getString(R.string.edit)
        } else if (frag is CreateNoteFragment) {
            drawIcon = resources.getDrawable(R.drawable.ic_done_white_24dp)
            title = resources.getString(R.string.create)
        }

        supportActionBar!!.title = title
        findViewById<FloatingActionButton>(R.id.main_fab).setImageDrawable(drawIcon)

        return true
    }

    fun setTitleAndFab(icon: Drawable?, title: String) {
        try {
            supportActionBar!!.title = title
            findViewById<FloatingActionButton>(R.id.main_fab).setImageDrawable(icon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (supportFragmentManager.findFragmentById(binding.mainContainer.id) is NotesListFragment)
            mSearchCallback.passNewText(newText)
        return true
    }
    /*
    Select menu item at Toolbar and execute action
     */

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.main_menu_grid -> {
                saveLayoutManagerStyle(false) //Save layout manager style to SharedPreference file
                mLayoutListener.passData(false) //Change layout style via interface between this Activity and NoteListFragment
            }
            R.id.main_menu_linear -> {
                saveLayoutManagerStyle(true) //Save layout manager style to SharedPreference file
                mLayoutListener.passData(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*
    Save Layout Manager style in SharedPreference file
    true -> Linear layout
    false -> StaggeredGridLayout
     */
    private fun saveLayoutManagerStyle(flag: Boolean) {
        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.layout_manager_key), flag)
        editor.apply()

    }

    override fun onBackPressed() {
        CurrentFragmentState.backPressed = true //Set flag at true. Necessary in onDestroyView() method in CreateNoteFragment.kt

        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()

        } else if (supportFragmentManager.backStackEntryCount <= 1) {
            if (doubleTapToExit) {
                this.finish()
                return
            }

            Toast.makeText(applicationContext, "One more time to exit", Toast.LENGTH_SHORT).show()
            doubleTapToExit = true

            Handler().postDelayed({
                doubleTapToExit = false
            }, 2000)
        }
    }

    /*
    Listen floatingActionButton
     */
    private fun floatingActionButtonListener() {
        binding.mainFab.setOnClickListener {
            CurrentFragmentState.backPressed = false //Set flag at false. Necessary in onDestroyView() method in CreateNoteFragment.kt

            /*
            Depending on the current fragment switch to another define below
             */
            val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

            if (frag is NotesListFragment) {
                setCreateNoteFragment()
            } else if (frag is CreateNoteFragment && frag.tag.equals(CREATE_NOTE_FRAGMENT_TAG)) {
                frag.onSaveNote()
                supportFragmentManager.popBackStack()
            } else if (frag is EditNoteFragment) { //Update RecyclerView item and return to NoteListFragment
                for (x in 1..2) {
                    supportFragmentManager.popBackStack()
                }
            } else if (frag is NotePreviewFragment) {
                setEditNoteFragment()
            }
        }
    }

    /*
    Listener recycler scroll via interface between this activity and NoteListFragment.
    When recycler is scrolling then floating action button is hiden
     */
    override fun recyclerScrolling(dx: Int?, dy: Int?, newState: Int?) {
        if (newState == null) {
            if (dy!! > 0 || dy < 0 && binding.mainFab.isShown)
                binding.mainFab.hide()
        } else {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                binding.mainFab.show()
            }
        }
    }

    /*
    Listen form fragmnt destroyed
     */

    override fun editDestroy(noteObject: ItemsHolder?) {
        noteToEdit = noteObject

        val id = noteObject!!.id
        val whereClause = "_id=?"

        val title = noteObject.title
        val note = noteObject.note
        val date = noteObject.date

        val bgColor = noteObject.bgColor
        val textColor = noteObject.textColor
        val fontStyle = noteObject.fontStyle

        val valuesNote = ContentValues()
        val valuesProperties = ContentValues()

        valuesNote.put(LocalSQLAnkoDatabase.TITLE, title)
        valuesNote.put(LocalSQLAnkoDatabase.NOTE, note)
        valuesNote.put(LocalSQLAnkoDatabase.DATE, date)

        database.use {
            //Update notes database
            update(
                    LocalSQLAnkoDatabase.TABLE_NOTES, valuesNote, whereClause, arrayOf(id)
            )
        }

        valuesProperties.put(LocalSQLAnkoDatabase.BG_COLOR, bgColor)
        valuesProperties.put(LocalSQLAnkoDatabase.TEXT_COLOR, textColor)
        valuesProperties.put(LocalSQLAnkoDatabase.FONT_STYLE, fontStyle)

        database.use {
            //Update notes properties database
            update(
                    LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, valuesProperties, whereClause, arrayOf(id)
            )
        }
    }

    override fun onNoteClicked(noteObject: ItemsHolder) {
        CurrentFragmentState.backPressed = false
        noteToEdit = noteObject
        setNotePreviewFragment()
    }

}