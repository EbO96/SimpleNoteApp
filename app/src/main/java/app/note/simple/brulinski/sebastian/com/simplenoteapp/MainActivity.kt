package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
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
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        binding.mainFab.setOnClickListener {
            //Switch to add note fragment
            setCreateNoteFragment()
        }

        if (savedInstanceState == null)
            setNotesListFragment()

    }

    fun setNotesListFragment() {
        supportActionBar?.setTitle(getString(R.string.notes))
        binding.mainFab.visibility = View.VISIBLE

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val notesListFragment: NotesListFragment = NotesListFragment()
        noteFragment = notesListFragment
        ft.replace(binding.mainContainer.id, notesListFragment, NOTE_LIST_FRAGMENT_TAG)
        ft.addToBackStack(null)
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

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (CurrentFragmentState.CURRENT.equals(NOTE_LIST_FRAGMENT_TAG)) {
            supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit()
            this.finish()
        } else setNotesListFragment()
    }

    fun editItem(frag: NotesListFragment) {
        frag.setOnEditModeListener(object : NotesListFragment.OnEditModeListener {
            override fun switch(title: String, note: String) {
                setEditNoteFragment(title, note)
            }
        })
    }
}

