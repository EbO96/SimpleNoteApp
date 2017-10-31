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
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnChangeColorListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnRefreshEditListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnRefreshNoteListListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnRefreshPreviewListener
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
        mViewPager.offscreenPageLimit = 4
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
        initListeners()
    }

    private fun initListeners() {
        val fragmentCreateNote = mViewPager.adapter.instantiateItem(mViewPager, 0)
        val fragmentNoteList = mViewPager.adapter.instantiateItem(mViewPager, 1)
        val fragmentPreview = mViewPager.adapter.instantiateItem(mViewPager, 2)
        val fragmentEdit = mViewPager.adapter.instantiateItem(mViewPager, 3)

        mOnRefreshNoteListListener = (fragmentNoteList as NotesListFragment)
        mOnRefreshPreviewListener = (fragmentPreview as NotePreviewFragment)
        mOnRefreshEditListener = (fragmentEdit as EditNoteFragment)
        mOnChangeColorListener = (fragmentCreateNote as CreateNoteFragment)
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
        when (mViewPager.currentItem) {
            0 -> {
                mOnRefreshNoteListListener.onNoteCreated(noteItem)
            }
            3 -> {
                mOnRefreshNoteListListener.onNoteEdited(noteItem)
            }
        }
    }

    fun refreshPreview(noteItem: NoteItem) {
        initListeners()
        mOnRefreshPreviewListener.onRefresh(noteItem)
    }

    fun refreshEdit(noteItem: NoteItem) {
        initListeners()
        mOnRefreshEditListener.onRefresh(noteItem)
    }

    fun changeNoteColors(colorOf: String, color: Int) {
        initListeners()
        val fragmentEdit = mViewPager.adapter.instantiateItem(mViewPager, 3)
        when (mViewPager.currentItem) {
            0 -> { //From create
                mOnChangeColorListener.onColorChange(colorOf, color)
            }
            3 -> { //From edit
                mOnChangeColorListener = (fragmentEdit as EditNoteFragment)
                mOnChangeColorListener.onColorChange(colorOf, color)
            }
        }
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