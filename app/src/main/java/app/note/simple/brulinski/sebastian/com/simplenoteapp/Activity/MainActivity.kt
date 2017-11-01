package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments.BottomSheetColorFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.EditNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.FragmentPagerAdapter.FragmentAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotePreviewFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.*
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding
import com.google.firebase.crash.FirebaseCrash
import es.dmoral.toasty.Toasty


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    /**
     * ViewPager object and Fragment Adapter object
     */
    private lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var mViewPager: ViewPager
    /**
     * Key's values
     */
    private val AFTER_EDIT = "AFTER EDIT"
    private val AFTER_CREATE = "AFTER CREATE"
    private val NOT_REFRESH = "NOT REFRESH"
    private var REFRESH_RECYCLER_AFTER = NOT_REFRESH
    private val UPDATE_CHANNEL_KEY = "update_channel_key"
    /**
     * Others
     */
    var doubleTapToExit = false
    lateinit var binding: ActivityMainBinding
    private lateinit var activityMain: MainActivity
    /**
    Toasty Toasts colors
     */
    @ColorInt
    private val ERROR_COLOR = Color.parseColor("#D50000")
    private var infoToastShowedAtStart: Boolean = false
    /**
     * Interfaces
     */
    private lateinit var mOnChangeColorListener: OnChangeColorListener //To change color in edit or create note
    private lateinit var mOnRefreshNoteListListener: OnRefreshNoteListListener //To refresh note list
    private lateinit var mOnRefreshPreviewListener: OnRefreshPreviewListener //To refresh preview screen
    private lateinit var mOnRefreshEditListener: OnRefreshEditListener //To refresh edit screen
    private lateinit var mOnResetCreateListener: OnResetCreateListener //To reset create screen
    /**
    There we starts...
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("interLog", "on create main")
        super.onCreate(savedInstanceState)
        FirebaseCrash.setCrashCollectionEnabled(false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        Toasty.Config.getInstance().setErrorColor(ERROR_COLOR).apply()
        infoToastShowedAtStart = true
        activityMain = this

        setupViewPager()
    }


    /**
     *  END OF onCreate(...)
     */

    fun getViewPager(): ViewPager {
        return mViewPager
    }

    fun getPagerAdapter(): FragmentAdapter {
        return fragmentAdapter
    }

    private fun setupViewPager() {
        mViewPager = binding.mainContainer
        fragmentAdapter = FragmentAdapter(supportFragmentManager, activityMain)
        mViewPager.adapter = fragmentAdapter
        mViewPager.currentItem = 1
        mViewPager.offscreenPageLimit = 3
        supportActionBar!!.title = getString(R.string.notes)

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        supportActionBar!!.title = getString(R.string.create)
                        EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
                                Color.BLACK)
                    }
                    1 -> {
                        supportActionBar!!.title = getString(R.string.notes)
                        EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
                                Color.BLACK)
                    }
                    2 -> {
                        supportActionBar!!.title = getString(R.string.preview)
                        EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
                                Color.BLACK)
                    }
                    3 -> {
                        supportActionBar!!.title = getString(R.string.edit)
                        EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
                                Color.BLACK)
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    fun setColorBottomSheet(forNoteBackground: Boolean) {
        val bottomSheetColors: BottomSheetDialogFragment = BottomSheetColorFragment()

        if (forNoteBackground) {
            val args = Bundle()
            args.putString(EditorManager.ColorManager.COLOR_OF_KEY, EditorManager.ColorManager.COLOR_OF_NOTE)
            bottomSheetColors.arguments = args
            if (!bottomSheetColors.isAdded) {
                bottomSheetColors.show(supportFragmentManager, bottomSheetColors.tag)
            }
        } else setColorBottomSheet()
    }

    fun setColorBottomSheet() {
        val bottomSheetColors: BottomSheetDialogFragment = BottomSheetColorFragment()
        val args = Bundle()
        args.putString(EditorManager.ColorManager.COLOR_OF_KEY, EditorManager.ColorManager.COLOR_OF_TEXT)
        bottomSheetColors.arguments = args
        if (!bottomSheetColors.isAdded) {
            bottomSheetColors.show(supportFragmentManager, bottomSheetColors.tag)
        }
    }

    /**
     * UPDATE PAGES
     */
    fun refreshNoteList(noteItem: NoteItem) {
        val fragmentNoteList = mViewPager.adapter.instantiateItem(mViewPager, 1)
        mOnRefreshNoteListListener = (fragmentNoteList as NotesListFragment)

        when (mViewPager.currentItem) {
            0 -> {
                mOnRefreshNoteListListener.onNoteCreated(noteItem)
            }
            3 -> {
                mOnRefreshNoteListListener.onNoteEdited(noteItem)
            }
        }
    }

    fun resetList() {
        val fragmentNoteList = mViewPager.adapter.instantiateItem(mViewPager, 1)
        mOnRefreshNoteListListener = (fragmentNoteList as NotesListFragment)
        mOnRefreshNoteListListener.onReset()
    }

    fun resetCreate() {
        val fragmentCreate = mViewPager.adapter.instantiateItem(mViewPager, 0)
        mOnResetCreateListener = fragmentCreate as CreateNoteFragment
        mOnResetCreateListener.onReset()
    }

    fun refreshPreview(noteItem: NoteItem) {
        val fragmentPreview = mViewPager.adapter.instantiateItem(mViewPager, 2)
        mOnRefreshPreviewListener = (fragmentPreview as NotePreviewFragment)
        mOnRefreshPreviewListener.onRefresh(noteItem)
    }

    fun refreshEdit(noteItem: NoteItem) {
        val fragmentEdit = mViewPager.adapter.instantiateItem(mViewPager, 3)
        mOnRefreshEditListener = (fragmentEdit as EditNoteFragment)
        mOnRefreshEditListener.onRefresh(noteItem)
    }

    fun resetPreview() {
        val fragmentPreview = mViewPager.adapter.instantiateItem(mViewPager, 2)
        mOnRefreshPreviewListener = (fragmentPreview as NotePreviewFragment)
        mOnRefreshPreviewListener.onReset()
    }

    fun resetEdit() {
        val fragmentEdit = mViewPager.adapter.instantiateItem(mViewPager, 3)
        mOnRefreshEditListener = (fragmentEdit as EditNoteFragment)
        mOnRefreshEditListener.onReset()
    }

    fun changeNoteColors(colorOf: String, color: Int) {

        when (mViewPager.currentItem) {
            0 -> { //From create
                val fragmentCreateNote = mViewPager.adapter.instantiateItem(mViewPager, 0)
                mOnChangeColorListener = (fragmentCreateNote as CreateNoteFragment)
                mOnChangeColorListener.onColorChange(colorOf, color)
            }
            3 -> { //From edit
                val fragmentCreateNote = mViewPager.adapter.instantiateItem(mViewPager, 3)
                mOnChangeColorListener = (fragmentCreateNote as CreateNoteFragment)
                mOnChangeColorListener.onColorChange(colorOf, color)
            }
        }
    }

    /**
     * MENU
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("interLog", "menu main")

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
        val position = mViewPager.currentItem

        when (position) {
            3 -> {
                mViewPager.setCurrentItem(2, true)
            }
            2 -> {
                mViewPager.setCurrentItem(1, true)
            }
            0 -> {
                mViewPager.setCurrentItem(1, true)
            }
            else -> doubleTapBackToExit()
        }
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
}