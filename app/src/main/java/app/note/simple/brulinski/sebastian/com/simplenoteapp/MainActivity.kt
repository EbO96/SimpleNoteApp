package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
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
        var menuItemGrid: MenuItem? = null
        var menuItemLinear: MenuItem? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        binding.mainFab.setOnClickListener {
            //Switch to add note fragment
            setCreateNoteFragment()
        }

        if (savedInstanceState == null) {
            val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
            val flag: Boolean = sharedPref.getBoolean(getString(R.string.layout_manager_key), true)
            setNotesListFragment(flag, false)
        }
    }

    fun setNotesListFragment(flag: Boolean, changeLayoutManager: Boolean) {
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
        ft.addToBackStack(null)
        if (!changeLayoutManager)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
        fm.executePendingTransactions()

    }

    fun setCreateNoteFragment() {
        supportActionBar?.setTitle(getString(R.string.create))
        binding.mainFab.visibility = View.GONE

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val createNoteFragment: CreateNoteFragment = CreateNoteFragment()

        ft.replace(binding.mainContainer.id, createNoteFragment, CREATE_NOTE_FRAGMENT_TAG)
        ft.addToBackStack(null)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
        fm.executePendingTransactions()
    }

    fun setEditNoteFragment(title: String, note: String) {
        supportActionBar?.setTitle(getString(R.string.edit))
        binding.mainFab.visibility = View.GONE
        val args: Bundle = Bundle()

        args.putString("title", title)
        args.putString("note", note)

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val editNoteFragment = EditNoteFragment()
        editNoteFragment.arguments = args

        ft.replace(binding.mainContainer.id, editNoteFragment, EDIT_NOTE_FRAGMENT_TAG)
        ft.addToBackStack(null)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
        fm.executePendingTransactions()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        menuItemGrid = menu!!.findItem(R.id.main_menu_grid)
        menuItemLinear = menu!!.findItem(R.id.main_menu_linear)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.main_menu_grid -> {
                saveLayoutManagerStyle(false)
                setNotesListFragment(false, true)
            }
            R.id.main_menu_linear -> {
                saveLayoutManagerStyle(true)
                setNotesListFragment(true, true)
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
        if (CurrentFragmentState.CURRENT.equals(NOTE_LIST_FRAGMENT_TAG)) {
            supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit()
            this.finish()
        } else setNotesListFragment(NotesListFragment.flag, false)
    }

    fun editItem(frag: NotesListFragment) {
        frag.setOnEditModeListener(object : NotesListFragment.OnEditModeListener {
            override fun switch(title: String, note: String) {
                setEditNoteFragment(title, note)
            }
        })
    }

    fun setToolbarItemsVisibility(visible: Boolean) {
        if(menuItemGrid != null && menuInflater != null){
            menuItemGrid!!.setVisible(visible)
            menuItemLinear!!.setVisible(visible)
        }
    }
}

