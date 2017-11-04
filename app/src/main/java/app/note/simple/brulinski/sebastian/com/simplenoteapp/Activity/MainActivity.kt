package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments.BottomSheetColorFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
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
class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

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
    private lateinit var searchViewMenuItem: MenuItem //SearchView menu item
    private val OPEN_ARCHIVES_ACTIVITY = 1
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
    private lateinit var mOnRefreshNoteList: OnRefreshNoteList //To refresh note list
    private lateinit var mOnSetEditMode: OnSetEditMode //To set object to edition in edit mode (CreateNoteFragment)
    private lateinit var mOnSetupPreview: OnSetupPreview //To setup note preview screen
    private lateinit var mOnSetFilter: OnSetFilter //To set filter at notes list
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OPEN_ARCHIVES_ACTIVITY) //Detect back from ArchivesActivity and in NoteListFragment load data to recycler again
            loadDataToRecycler()
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
        binding.mainTabLayout.setupWithViewPager(mViewPager, true)
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
    //Note list
    fun refreshNoteList(noteItem: NoteItem) {
        val fragmentNoteList = mViewPager.adapter.instantiateItem(mViewPager, 1)
        mOnRefreshNoteList = (fragmentNoteList as NotesListFragment)
        mOnRefreshNoteList.onRefreshList(noteItem)
    }

    fun loadDataToRecycler() {
        val fragmentNoteList = mViewPager.adapter.instantiateItem(mViewPager, 1)
        mOnRefreshNoteList = (fragmentNoteList as NotesListFragment)
        mOnRefreshNoteList.loadDataToRecycler()
    }

    //Edit mode
    fun setEditMode(noteItem: NoteItem) {
        val fragmentCreate = mViewPager.adapter.instantiateItem(mViewPager, 3)
        mOnSetEditMode = (fragmentCreate as CreateNoteFragment)
        mOnSetEditMode.onSetNoteObjectInEditMode(noteItem)
    }

    //Preview mode
    fun setupPreview(noteItem: NoteItem) {
        val fragmentSetup = mViewPager.adapter.instantiateItem(mViewPager, 2)
        mOnSetupPreview = (fragmentSetup as NotePreviewFragment)
        mOnSetupPreview.onSetup(noteItem)
    }

    /**
     * SearchView
     */
    fun setFilterAtRecycler(query: String?) {
        val fragmentNoteList = mViewPager.adapter.instantiateItem(mViewPager, 1)
        mOnSetFilter = fragmentNoteList as NotesListFragment
        if (query != null)
            mOnSetFilter.setFilter(query)
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

        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        Log.i("searchItemLog", "onCreateOptionsMenu")

        //Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        //Find search menu item
        searchViewMenuItem = menu!!.findItem(R.id.search_view)
        val searchView = searchViewMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(this)

        //Listen for current page in ViewPager and set menu item SearchView visible or not
        when (mViewPager.currentItem) {
            1 -> searchViewMenuItem.isVisible = true//Make search item invisible default
            else -> searchViewMenuItem.isVisible = false//Make search item invisible default
        }
        return true
    }

    /*
    Listen for search query
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
//        val intent = Intent(this, SearchResultActivity::class.java)
//        intent.action = Intent.ACTION_SEARCH
//        intent.putExtra(SearchManager.QUERY, query)
//        startActivity(intent)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        setFilterAtRecycler(newText)
        return true
    }

    /*
    Select menu item at Toolbar and execute action
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
//            R.id.search_main -> {
//                val intent = Intent(this, SearchActivity::class.java)
//                startActivity(intent)
//            }
            R.id.archives -> {
                val intent = Intent(this, ArchivesActivity::class.java)
                startActivityForResult(intent, OPEN_ARCHIVES_ACTIVITY)
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