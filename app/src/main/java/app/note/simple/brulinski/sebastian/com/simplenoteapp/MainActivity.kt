package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
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
import android.view.View
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var fm: FragmentManager
    lateinit var ft: FragmentTransaction
    lateinit var noteFragment: Fragment

    companion object {
        var NOTE_LIST_FRAGMENT_TAG: String = "NOTES"
        var CREATE_NOTE_FRAGMENT_TAG: String = "CREATE"
        var EDIT_NOTE_FRAGMENT_TAG: String = "EDIT"
        var NOTE_PREVIEW_FRAGMENT_TAG: String = "PREVIEW"
        var menuItemGrid: MenuItem? = null
        var menuItemLinear: MenuItem? = null
        var menuItemCreateNote: MenuItem? = null
        var twoPaneMode: Boolean = false
    }

    lateinit var mUpdateListener: OnUpdateListListener

    interface OnUpdateListListener {
        fun passData(title: String, note: String, position: Int)
    }

    fun setOnUpdateListListener(mUpdateListener: OnUpdateListListener) {
        this.mUpdateListener = mUpdateListener
    }

    lateinit var mLayoutListener: OnChangeLayoutListener

    interface OnChangeLayoutListener {
        fun passData(flag: Boolean)
    }

    fun setOnChangeLayoutListener(mLayoutListener: OnChangeLayoutListener) {
        this.mLayoutListener = mLayoutListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        binding.mainFab.setOnClickListener {
            //Switch to add note fragment
            setCreateNoteFragment()
        }

        twoPaneMode = resources.getBoolean(R.bool.twoPaneMode)

        if (savedInstanceState == null) {
            val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
            val flag: Boolean = sharedPref.getBoolean(getString(R.string.layout_manager_key), true)
            setNotesListFragment(flag, false)
        }

    }

    fun setNotesListFragment(flag: Boolean, changeLayoutManager: Boolean) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER

        val args: Bundle = Bundle()
        args.putBoolean("flag", flag)
        supportActionBar?.setTitle(getString(R.string.notes))
        binding.mainFab.visibility = View.VISIBLE

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val notesListFragment: NotesListFragment = NotesListFragment()
        notesListFragment.arguments = args
        noteFragment = notesListFragment
        ft.replace(binding.mainContainer.id, notesListFragment, NOTE_LIST_FRAGMENT_TAG)

        if (!changeLayoutManager || !twoPaneMode)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        ft.commit()
        fm.executePendingTransactions()

    }

    fun setCreateNoteFragment() {
        if (!twoPaneMode)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        supportActionBar?.setTitle(getString(R.string.create))
        binding.mainFab.visibility = View.GONE

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val createNoteFragment: CreateNoteFragment = CreateNoteFragment()

        ft.replace(binding.mainContainer.id, createNoteFragment, CREATE_NOTE_FRAGMENT_TAG)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
        //fm.executePendingTransactions()
    }

    fun setEditNoteFragment(title: String, note: String, position: Int) {
        if (!twoPaneMode)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        supportActionBar?.setTitle(getString(R.string.edit))
        binding.mainFab.visibility = View.GONE
        val args: Bundle = Bundle()

        args.putString("title", title)
        args.putString("note", note)

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val editNoteFragment = EditNoteFragment()
        editNoteFragment.arguments = args

        var containerId = binding.mainContainer.id
        if (twoPaneMode)
            containerId = binding.mainContainerDetails!!.id

        ft.replace(containerId, editNoteFragment, EDIT_NOTE_FRAGMENT_TAG)

        if (!twoPaneMode)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        ft.commit()
        //fm.executePendingTransactions()

        listenEndOfEditing(editNoteFragment, position)

    }

    private fun setNotePreviewFragment(title: String, note: String, position: Int) {
        binding.mainFab.visibility = View.GONE

        val args: Bundle = Bundle()
        args.putString("title", title)
        args.putString("note", note)

        val previewFragment = NotePreviewFragment()
        previewFragment.arguments = args

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        ft.replace(binding.mainContainerDetails!!.id, previewFragment, NOTE_PREVIEW_FRAGMENT_TAG)
        ft.commit()

        //fm.executePendingTransactions()

        listenEditMode(previewFragment, position)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        menuItemGrid = menu!!.findItem(R.id.main_menu_grid)
        menuItemLinear = menu!!.findItem(R.id.main_menu_linear)
        menuItemCreateNote = menu!!.findItem(R.id.main_menu_create_note)

        val paneModeFlag = resources.getBoolean(R.bool.twoPaneMode)

        menuItemCreateNote!!.setVisible(paneModeFlag)

        if (paneModeFlag)
            binding.mainFab.visibility = View.GONE
        else binding.mainFab.visibility = View.VISIBLE

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.main_menu_grid -> {
                saveLayoutManagerStyle(false)
                mLayoutListener.passData(false)
            }
            R.id.main_menu_linear -> {
                saveLayoutManagerStyle(true)
                mLayoutListener.passData(true)
            }
            R.id.main_menu_settings -> {

            }
            R.id.main_menu_create_note -> {
                if (!CurrentFragmentState.CURRENT.equals(CREATE_NOTE_FRAGMENT_TAG))
                    setCreateNoteFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun saveLayoutManagerStyle(flag: Boolean) {
        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.layout_manager_key), flag)
        editor.apply()
    }

    override fun onBackPressed() {
        if (CurrentFragmentState.CURRENT.equals(NOTE_LIST_FRAGMENT_TAG) || twoPaneMode) {
            if (twoPaneMode) {
                //TODO
            } else {
                supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit()
                this.finish()
            }
        } else setNotesListFragment(NotesListFragment.flag, false)
    }

    fun editItem(frag: NotesListFragment) {
        if (resources.getBoolean(R.bool.twoPaneMode) && frag.itemsObjectsArray.size != 0) {
            setNotePreviewFragment(frag.itemsObjectsArray.get(0).title, frag.itemsObjectsArray.get(0).note, 0)
        }

        frag.setOnEditModeListener(object : NotesListFragment.OnEditModeListener {
            override fun switch(title: String, note: String, position: Int) {
                if (twoPaneMode)
                    setNotePreviewFragment(title, note, position)
                else setEditNoteFragment(title, note, position)
            }
        })
    }

    fun listenEditMode(frag: NotePreviewFragment, position: Int) {
        frag.setOnEditNoteListener(object : NotePreviewFragment.OnEditNoteListener {
            override fun passData(title: String, note: String) {
                setEditNoteFragment(title, note, position)
            }
        })
    }

    fun listenEndOfEditing(frag: EditNoteFragment, position: Int) {
        frag.setOnSaveListener(object : EditNoteFragment.OnSaveNoteListener {
            override fun passData(title: String, note: String) {
                mUpdateListener.passData(title, note, position)
                setNotePreviewFragment(title, note, position)
            }
        })
    }

    fun clearPreviewList(frag: NotesListFragment) {
        frag.setOnClearPreviewList(object : NotesListFragment.OnClearPreviewList {
            override fun clear() {
                Log.i("clear", "cleared")
                val fragment = supportFragmentManager.findFragmentByTag(NOTE_PREVIEW_FRAGMENT_TAG)
                if (fragment != null && fragment.isVisible)
                    supportFragmentManager.beginTransaction().remove(fragment).commitNow()

            }
        })
    }

    fun changeRecyclerLayout() {

    }
}

