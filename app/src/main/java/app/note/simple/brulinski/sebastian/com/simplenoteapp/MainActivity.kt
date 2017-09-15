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
    lateinit var NOTE_LIST_FRAGMENT_TAG: String
    lateinit var CREATE_NOTE_FRAGMENT_TAG: String
    lateinit var currentFragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        NOTE_LIST_FRAGMENT_TAG = "NOTES"
        CREATE_NOTE_FRAGMENT_TAG = "CREATE"

        setNotesListFragment()

        binding.mainFab.setOnClickListener {
            //Switch to add note fragment
            setCreateNoteFragment()
        }
    }

    fun setNotesListFragment() {
        binding.mainFab.visibility = View.VISIBLE
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        var notesListFragment: NotesListFragment = NotesListFragment()
        currentFragment = notesListFragment
        ft.replace(binding.mainContainer.id, notesListFragment, NOTE_LIST_FRAGMENT_TAG).commit()
        fm.executePendingTransactions()
    }

    fun setCreateNoteFragment() {
        binding.mainFab.visibility = View.GONE
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        var createNoteFragment: CreateNoteFragment = CreateNoteFragment()
        currentFragment = createNoteFragment
        ft.replace(binding.mainContainer.id, createNoteFragment, CREATE_NOTE_FRAGMENT_TAG).commit()
        fm.executePendingTransactions()
    }

    fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if(currentFragment.equals(supportFragmentManager.findFragmentByTag(NOTE_LIST_FRAGMENT_TAG)))
            this.finish()
        else setNotesListFragment()
    }
}

