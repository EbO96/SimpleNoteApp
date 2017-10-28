package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.EditNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.FragmentPagerAdapter.FragmentAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotePreviewFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FragmentAndObjectStates
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.ChangeNoteLookInterface
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnNotePropertiesClickListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.RecyclerMainInterface
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding
import es.dmoral.toasty.Toasty


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), RecyclerMainInterface,
        OnNotePropertiesClickListener {
    /**
     * ViewPager object and Fragment Adapter object
     */
    private lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var mViewPager: ViewPager
    /**
     * Interfaces
     */
    lateinit var mChangeNoteLookCallback: ChangeNoteLookInterface
    /**
     * Others
     */
    var doubleTapToExit = false
    lateinit var binding: ActivityMainBinding
    /**
    Toasty Toasts colors
     */
    @ColorInt
    private val ERROR_COLOR = Color.parseColor("#D50000")
    private var infoToastShowedAtStart: Boolean = false
    /**
    There we starts...
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        Toasty.Config.getInstance().setErrorColor(ERROR_COLOR).apply()
        infoToastShowedAtStart = true
        setupViewPager()
        setViewPager()
    }

    /**
     *  END OF onCreate(...)
     */

    /*
    Method used to replace fragments in container (ViewPager)
     */
    private fun setViewPager() {
        fragmentAdapter = FragmentAdapter(supportFragmentManager)

        fragmentAdapter.addFragment(CreateNoteFragment(), getString(R.string.create))
        fragmentAdapter.addFragment(NotesListFragment(), getString(R.string.notes))
        fragmentAdapter.addFragment(NotePreviewFragment(), getString(R.string.preview))
        fragmentAdapter.addFragment(EditNoteFragment(), getString(R.string.edit))

        mViewPager.adapter = fragmentAdapter

        setFragmentInViewPager(1, null) //switch to notes list
    }

    fun getFragmentAdapter(): FragmentAdapter {
        return fragmentAdapter
    }

    fun getViewPager(): ViewPager{
        return mViewPager
    }

    private fun setupViewPager() {
        mViewPager = binding.mainContainer
        //Set listener on ViewPager
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                FragmentAndObjectStates.currentFragment = position
                when (position) {
                    0 -> {
                        supportActionBar!!.title = getString(R.string.create)
                    }
                    1 -> {
                        supportActionBar!!.title = getString(R.string.notes)
                    }
                    2 -> {
                        supportActionBar!!.title = getString(R.string.preview)
                    }
                    3 -> {
                        supportActionBar!!.title = getString(R.string.edit)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    fun setFragmentInViewPager(fragmentNumber: Int, bundleData: Bundle?) {
        mViewPager.currentItem = fragmentNumber
    }

    /**
     * MENU
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /*
    Select menu item at Toolbar and execute action
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_main -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.archives -> {
                val intent = Intent(this, ArchivesActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        doubleTapBackToExit()
    }

    private fun doubleTapBackToExit() {
        if (doubleTapToExit) {
            finish()
        }

        Toasty.error(this, getString(R.string.exit_toast), Toast.LENGTH_SHORT, true).show()
        doubleTapToExit = true

        Handler().postDelayed({
            doubleTapToExit = false
        }, 2000)
    }

    /**
     * Change color listener
     */
    /*
    Methods below are called in Editor in Create or Edit Mode when user want to change color of note and etc.
     */
    override fun inEditorColorClick(color: Int, colorOfWhat: String) {
        val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

        if (frag is CreateNoteFragment)
            mChangeNoteLookCallback = frag
        else if (frag is EditNoteFragment)
            mChangeNoteLookCallback = frag

        mChangeNoteLookCallback.changeNoteOrFontColors(colorOfWhat, color)
    }

    override fun inEditorFontClick(whichFont: String) {
        val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

        if (frag is CreateNoteFragment)
            mChangeNoteLookCallback = frag
        else if (frag is EditNoteFragment)
            mChangeNoteLookCallback = frag

        mChangeNoteLookCallback.changeFontStyle(whichFont)
    }

    override fun inEditorColorPickerClick() {
        val intent = Intent(this, OwnColorCreatorActivity::class.java)
        startActivity(intent)
    }

    override fun onNoteClicked(noteObject: NoteItem) {
        FragmentAndObjectStates.currentNote = noteObject
        setFragmentInViewPager(2, null)
    }
}