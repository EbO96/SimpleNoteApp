package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.EditNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotePreviewFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var mUpdateListener: OnUpdateListListener

    interface OnUpdateListListener {
        fun passData(title: String, note: String, position: Int)
    }

    fun setOnUpdateListListener(mUpdateListener: OnUpdateListListener) {
        this.mUpdateListener = mUpdateListener
    }

    lateinit var binding: ActivityMainBinding
    lateinit var fm: FragmentManager
    lateinit var ft: FragmentTransaction
    lateinit var noteFragment: Fragment
    lateinit var managerStyle: LayoutManagerStyle

    companion object {
        var NOTE_LIST_FRAGMENT_TAG: String = "NOTES" //Fragment TAG
        var CREATE_NOTE_FRAGMENT_TAG: String = "CREATE" //Fragment TAG
        var EDIT_NOTE_FRAGMENT_TAG: String = "EDIT" //Fragment TAG
        var NOTE_PREVIEW_FRAGMENT_TAG: String = "PREVIEW" //Fragment TAG
        var menuItemGrid: MenuItem? = null //Toolbar menu item (set recycler view layout as staggered layout)
        var menuItemLinear: MenuItem? = null //Toolbar menu item (set recycler view layout as linear layout)
        var twoPaneMode: Boolean = false //Flag - portrait or landscape mode
    }

    /*
    mLayoutListener - it is a interface between MainActivity and NoteListFragment. Used to
    change layout manager
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
        After when user rotate phone the onCreate is called again and we save screen orientation state to this value
         */
        twoPaneMode = resources.getBoolean(R.bool.twoPaneMode)

        /*
        Set main fragment  (NoteListFragment.kt) manually  only once when saveInstanceState is null
        At the same time we getting layout manager type from SharedPreferences
         */
        if (savedInstanceState == null) {
            setNotesListFragment()
        }

        floatingActionButtonListener() //Listen for floating action button actions and click

    } //END OF onCreate(...)

    /*
    This function replace another fragment in FrameLayout container by our main Fragment (NoteListFragment.kt - display our notes)
     */
    fun setNotesListFragment() {
        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val notesListFragment: NotesListFragment = NotesListFragment()

        noteFragment = notesListFragment
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(binding.mainContainer.id, notesListFragment, NOTE_LIST_FRAGMENT_TAG)

        if (!twoPaneMode) //Off fragment transition when user is in landscape mode
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        ft.addToBackStack(NOTE_LIST_FRAGMENT_TAG)

        ft.commit()
        fm.executePendingTransactions()

    }

    /*
    Fragment used to create new note
     */
    fun setCreateNoteFragment() {
        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val createNoteFragment: CreateNoteFragment = CreateNoteFragment()

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(binding.mainContainer.id, createNoteFragment, CREATE_NOTE_FRAGMENT_TAG)
        ft.addToBackStack(CREATE_NOTE_FRAGMENT_TAG)
        ft.commit()
    }

    /*
    This fragment is setting only from preview mode (NotePreviewFragment.kt)
     */
    fun setEditNoteFragment(title: String, note: String, position: Int) {
        val args = Bundle()

        args.putString("title", title)
        args.putString("note", note)

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val editNoteFragment = EditNoteFragment()
        editNoteFragment.arguments = args


        var containerId = binding.mainContainer.id
        if (twoPaneMode)
            containerId = binding.mainContainerDetails!!.id

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(containerId, editNoteFragment, EDIT_NOTE_FRAGMENT_TAG)

        ft.addToBackStack(EDIT_NOTE_FRAGMENT_TAG)

        ft.commit()
    }

    /*
    This fragment is setting when user click RecyclerView item
     */
    private fun setNotePreviewFragment(title: String, note: String, position: Int) {

        binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_mode_edit_white_24dp))

        val args = Bundle()
        args.putString("title", title)
        args.putString("note", note)
        args.putInt("position", position)

        val previewFragment = NotePreviewFragment()
        previewFragment.arguments = args

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        var containerID = binding.mainContainer.id
        if (twoPaneMode)
            containerID = binding.mainContainerDetails!!.id

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(containerID, previewFragment, NOTE_PREVIEW_FRAGMENT_TAG)
        if (!twoPaneMode)
            ft.addToBackStack(NOTE_PREVIEW_FRAGMENT_TAG)
        ft.commit()
    }

    /*
    Create menu at Toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        menuItemGrid = menu!!.findItem(R.id.main_menu_grid)
        menuItemLinear = menu.findItem(R.id.main_menu_linear)

        return super.onCreateOptionsMenu(menu)
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
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
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
            When user press back button then we change icon at floatingActionButton

            Example: Current screen is "Edit Mode" (EditNoteFragment.kt) and user press back button.
            In result we switch to "Preview Mode" (NotePreviewFragment.kt) and changing icon at floatingActionButton
            from "Apply" to "Edit"
             */
            val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

            if (frag is CreateNoteFragment && frag.tag.equals(CREATE_NOTE_FRAGMENT_TAG)) {
                binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_add_white_24dp))

            } else if (frag is EditNoteFragment) { //Update RecyclerView item and return to NoteListFragment
                binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_mode_edit_white_24dp))
            } else if (frag is NotePreviewFragment) {
                binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_add_white_24dp))
            }
        } else if (supportFragmentManager.backStackEntryCount <= 1) this.finish()
    }

    fun editItem(frag: NotesListFragment) {
        /*
        When user have a one or more item at list and then switch to landscape mode then we
        set "Preview Mode" fragment in neighboring container
         */

        if (resources.getBoolean(R.bool.twoPaneMode) && frag.itemsObjectsArray.size != 0) {
            setNotePreviewFragment(frag.itemsObjectsArray.get(0).title, frag.itemsObjectsArray.get(0).note, 0)
        }

        /*
        Listen item click and switch to "Preview Mode"
         */
        frag.setOnEditModeListener(object : NotesListFragment.OnEditModeListener {
            override fun switch(title: String, note: String, position: Int) {
                setNotePreviewFragment(title, note, position)
            }
        })
    }

    fun clearPreviewList(frag: NotesListFragment) {
        frag.setOnClearPreviewList(object : NotesListFragment.OnClearPreviewList {
            override fun clear() {
                val fragment = supportFragmentManager.findFragmentByTag(NOTE_PREVIEW_FRAGMENT_TAG)
                if (fragment != null && fragment.isVisible)
                    supportFragmentManager.beginTransaction().remove(fragment).commitNow()
            }
        })
    }

    /*
    Listen floatingActionButton
     */
    fun floatingActionButtonListener() {
        binding.mainFab.setOnClickListener {
            CurrentFragmentState.backPressed = false //Set flag at false. Necessary in onDestroyView() method in CreateNoteFragment.kt
            if (twoPaneMode) {

            } else {
                /*
                Depending on the current fragment switch to another define below
                 */
                val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

                if (frag is NotesListFragment) {
                    binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_done_white_24dp))
                    setCreateNoteFragment()
                } else if (frag is CreateNoteFragment && frag.tag.equals(CREATE_NOTE_FRAGMENT_TAG)) {
                    binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_add_white_24dp))
                    supportFragmentManager.popBackStack()
                } else if (frag is EditNoteFragment) { //Update RecyclerView item and return to NoteListFragment
                    binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_add_white_24dp))
                    for (x in 1..2) {
                        supportFragmentManager.popBackStack()
                    }
                    //mUpdateListener.passData(frag.title, frag.note, frag.position)
                } else if (frag is NotePreviewFragment) {
                    binding.mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_done_white_24dp))
                    setEditNoteFragment(frag.binding.previewTitleField.text.toString(),
                            frag.binding.previewNoteField.text.toString(), frag.itemPosition)
                }
            }
        }
    }
}

