package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.EditNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotePreviewFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.ChangeFabIcon
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NotesListFragment.OnListenRecyclerScroll, SearchView.OnQueryTextListener, EditNoteFragment.OnInflateNewToolbarListener,
        NotesListFragment.OnChangeItemVisible, ChangeFabIcon {

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
        var menuItemGrid: MenuItem? = null //Toolbar menu item (set recycler view layout as staggered layout)
        var menuItemLinear: MenuItem? = null //Toolbar menu item (set recycler view layout as linear layout)
        var menuItemSearch: MenuItem? = null //Toolbar menu item (set recycler view layout as linear layout)
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
    Inflate new toolbar or back to previous
     */
    override fun fragmentCreated(visible: Boolean) { //This interface is between this activity and EditNoteFragment
        if (visible) { //Inflate new toolbar
            //setSupportActionBar()
        } else { //Set up previous toolbar

        }
    }

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

        val createNoteFragment: CreateNoteFragment = CreateNoteFragment()

        ft.replace(binding.mainContainer.id, createNoteFragment, CREATE_NOTE_FRAGMENT_TAG)
        ft.addToBackStack(CREATE_NOTE_FRAGMENT_TAG)
        ft.commit()
    }

    /*
    This fragment is setting only from preview mode (NotePreviewFragment.kt)
     */
    private fun setEditNoteFragment(itemId: String, title: String, note: String, position: Int, noteObject: ItemsHolder) {
        val args = Bundle()

        args.putString("id", itemId)
        args.putString("title", title)
        args.putString("note", note)
        args.putInt("position", position)
        args.putParcelableArrayList("note_object", arrayListOf(noteObject))

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val editNoteFragment = EditNoteFragment()
        editNoteFragment.arguments = args

        val containerId = binding.mainContainer.id

        ft.replace(containerId, editNoteFragment, EDIT_NOTE_FRAGMENT_TAG)

        ft.addToBackStack(EDIT_NOTE_FRAGMENT_TAG)

        ft.commit()
    }

    /*
    This fragment is setting when user click RecyclerView item
     */
    @SuppressLint("CommitTransaction")
    private fun setNotePreviewFragment(itemId: String, title: String, note: String, position: Int, noteObject: ItemsHolder) {

        val args = Bundle()
        args.putString("id", itemId)
        args.putString("title", title)
        args.putString("note", note)
        args.putInt("position", position)
        args.putParcelableArrayList("note_object", arrayListOf(noteObject))

        val previewFragment = NotePreviewFragment()
        previewFragment.arguments = args

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

        val searchView: SearchView = (menu.findItem(R.id.search).getActionView() as SearchView)
        searchView.queryHint = getString(R.string.search_hint) //Set search hint

        searchView.setOnQueryTextListener(this)

        /*
        Listen when search mode is active and when collapse
         */
        MenuItemCompat.setOnActionExpandListener(menu!!.findItem(R.id.search), object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                setMenuItemsVisibility(true)
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                setMenuItemsVisibility(false)
                return true
            }
        })

        return true
    }

    override fun changeMenuItemsVisibility(visible: Boolean) {
        setMenuItemsVisibility(visible)
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
            R.id.main_menu_settings -> {
                //TODO create settings activity
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*
    Save Layout Manager style in SharedPreference file
    true -> Linear layout
    false -> StaggeredGridLayout
     */
    fun saveLayoutManagerStyle(flag: Boolean) {
        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.layout_manager_key), flag)
        editor.apply()
    }

    override fun onBackPressed() {
        CurrentFragmentState.backPressed = true //Set flag at true. Necessary in onDestroyView() method in CreateNoteFragment.kt

        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            /*
            User interface implement only one floating action button in this activity.
            When user press back button then we changeMenuItemsVisibility icon at floatingActionButton

            Example: Current screen is "Edit Mode" (EditNoteFragment.kt) and user press back button.
            In result we switch to "Preview Mode" (NotePreviewFragment.kt) and changing icon at floatingActionButton
            from "Apply" to "Edit"
             */
            val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

            if (frag is CreateNoteFragment && frag.tag.equals(CREATE_NOTE_FRAGMENT_TAG)) {
                CurrentFragmentState.PREVIOUS = MainActivity.CREATE_NOTE_FRAGMENT_TAG
            } else if (frag is EditNoteFragment) { //Update RecyclerView item and return to NoteListFragment
                CurrentFragmentState.PREVIOUS = MainActivity.EDIT_NOTE_FRAGMENT_TAG
            } else if (frag is NotePreviewFragment) {
                CurrentFragmentState.PREVIOUS = MainActivity.NOTE_PREVIEW_FRAGMENT_TAG
            }
        } else if (supportFragmentManager.backStackEntryCount <= 1) {
            if (doubleTapToExit){
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

    fun editItem(frag: NotesListFragment) {
        /*
        Listen item click and switch to "Preview Mode"
         */
        frag.setOnEditModeListener(object : NotesListFragment.OnEditModeListener {
            override fun switch(itemId: String, title: String, note: String, position: Int, noteObject: ItemsHolder) {
                setNotePreviewFragment(itemId, title, note, position, noteObject)
            }
        })
    }

    /*
    Listen floatingActionButton
     */
    fun floatingActionButtonListener() {
        binding.mainFab.setOnClickListener {
            CurrentFragmentState.backPressed = false //Set flag at false. Necessary in onDestroyView() method in CreateNoteFragment.kt

            /*
            Depending on the current fragment switch to another define below
             */
            val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

            if (frag is NotesListFragment) {
                setCreateNoteFragment()
                CurrentFragmentState.PREVIOUS = MainActivity.NOTE_PREVIEW_FRAGMENT_TAG
            } else if (frag is CreateNoteFragment && frag.tag.equals(CREATE_NOTE_FRAGMENT_TAG)) {
                supportFragmentManager.popBackStack()
                CurrentFragmentState.PREVIOUS = MainActivity.CREATE_NOTE_FRAGMENT_TAG
            } else if (frag is EditNoteFragment) { //Update RecyclerView item and return to NoteListFragment
                for (x in 1..2) {
                    supportFragmentManager.popBackStack()
                }
                CurrentFragmentState.PREVIOUS = MainActivity.EDIT_NOTE_FRAGMENT_TAG
                //mUpdateListener.passData(frag.title, frag.note, frag.position)
            } else if (frag is NotePreviewFragment) {
                setEditNoteFragment(frag.itemId, frag.binding.previewTitleField.text.toString(),
                        frag.binding.previewNoteField.text.toString(), frag.itemPosition, frag.noteObject.get(0))
                CurrentFragmentState.PREVIOUS = MainActivity.NOTE_PREVIEW_FRAGMENT_TAG
            }
        }
    }

    /*
    Change FAB icon
    */
    override fun changeFabDrawableIcon(from: String) {
        var drawIcon: Drawable? = null
        when (from) {
            ChangeFabIcon.EDIT -> drawIcon = resources.getDrawable(R.drawable.ic_done_white_24dp)
            ChangeFabIcon.CREATE -> drawIcon = resources.getDrawable(R.drawable.ic_done_white_24dp)
            ChangeFabIcon.PREVIEW -> drawIcon = resources.getDrawable(R.drawable.ic_mode_edit_white_24dp)
            ChangeFabIcon.LIST -> drawIcon = resources.getDrawable(R.drawable.ic_add_white_24dp)
        }
        binding.mainFab.setImageDrawable(drawIcon!!)
    }

    /*
    Listener recycler scroll via interface between this activity and NoteListFragment.
    When recycler is scrolling then floating action button is hiden
     */
    override fun recyclerScrolling(dx: Int?, dy: Int?, newState: Int?) {
        if (newState == null) {
            if (dy!! > 0 || dy < 0 && binding.mainFab.isShown())
                binding.mainFab.hide();
        } else {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                binding.mainFab.show();
            }
        }
    }

    fun setMenuItemsVisibility(visible: Boolean) {
        try {
            if (menuItemGrid!!.isVisible != visible)
                menuItemLinear!!.setVisible(visible)
            menuItemGrid!!.setVisible(visible)
            menuItemSearch!!.setVisible(visible)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

