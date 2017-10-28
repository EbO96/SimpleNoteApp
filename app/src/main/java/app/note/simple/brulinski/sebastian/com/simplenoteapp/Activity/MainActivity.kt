package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.*
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.FragmentPagerAdapter.FragmentAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.*
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.MainRecyclerAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding
import es.dmoral.toasty.Toasty


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),
        EditNoteFragment.OnEditDestroy, RecyclerMainInterface, NotesListFragment.OnGetNotesFromParentActivity, MainRecyclerSizeListener,
        OnNotePropertiesClickListener, AfterEditListener {

    /**
     * ViewPager object and Fragment Adapter object
     */
    private lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var mViewPager: ViewPager
    private var actuallyPosition: Int = 0


    lateinit var binding: ActivityMainBinding
    lateinit var fm: FragmentManager
    lateinit var ft: FragmentTransaction
    lateinit var noteFragment: Fragment
    lateinit var managerStyle: LayoutManagerStyle
    lateinit var mChangeNoteLookCallback: ChangeNoteLookInterface
    private val ADAPTER_SIZE_KEY = "adapter size"
    var doubleTapToExit = false

    /*
    Toasty Toasts colors
     */
    @ColorInt
    private val ERROR_COLOR = Color.parseColor("#D50000")
    private var infoToastShowedAtStart: Boolean = false

    companion object {
        var NOTE_LIST_FRAGMENT_TAG: String = "NOTES" //Fragment TAG
        var CREATE_NOTE_FRAGMENT_TAG: String = "CREATE" //Fragment TAG
        var EDIT_NOTE_FRAGMENT_TAG: String = "EDIT" //Fragment TAG
        var NOTE_PREVIEW_FRAGMENT_TAG: String = "PREVIEW" //Fragment TAG'
        var NO_NOTES_FRAGMENT_TAG: String = "NO_NOTES" //Fragment TAG'
        val DATABASE_NOTES_ARRAY = "notes array"
        val NOTE_TO_EDIT_EXTRA_KEY = "note_to_edit_key"
        val BACK_FROM_COLOR_CREATOR_VALUE = "back_from_color_creator_value"
        val BACK_FROM_COLOR_CREATOR_KEY = "back_from_color_creator_key"
        lateinit var menuItemSearch: MenuItem //Toolbar menu item (set recycler view layout as linear layout)
        var adapterSize = 0
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
        //TODO
//        if (savedInstanceState == null) {
//            val array = getNotesFromDatabase()
//            if (array.size != 0)
//                setNotesListFragment(getNotesFromDatabase())
//            else setNoNotesFragment()
//
//            val v: NoteItem? = intent.getParcelableExtra("noteObject")
//
//            if (v != null) {
//                onNoteClicked(v)
//                intent.removeExtra("noteObject")
//            }
//        } else adapterSize = savedInstanceState.getInt(ADAPTER_SIZE_KEY)


        //floatingActionButtonListener() //Listen for floating action button actions and click

        Toasty.Config.getInstance().setErrorColor(ERROR_COLOR).apply()

        infoToastShowedAtStart = true
        //TODO
//        //Listen for event when keyboard is open
//        KeyboardVisibilityEvent.setEventListener(
//                this
//        ) { isOpen ->
//            if (isOpen) binding.mainFab.hide()
//            else binding.mainFab.show()
//        }

        setupViewPager()
        setViewPager()

    } //END OF onCreate(...)

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putInt(ADAPTER_SIZE_KEY, adapterSize)
        super.onSaveInstanceState(outState)
    }

    private fun getNotesFromDatabase(): ArrayList<NoteItem> {
        return ObjectToDatabaseOperations.getObjects(this, false)
    }

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

        setFragmentInViewPager(1) //switch to notes list
    }

    private fun setupViewPager() {
        mViewPager = binding.mainContainer

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.i("abcd", "onPageScrolled, pos = $position")
                actuallyPosition = position

                when (actuallyPosition) {
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
                Log.i("abcd", "onPageScrollSatateChanged, state = $state")

            }

            override fun onPageSelected(position: Int) {
                Log.i("abcd", "onPageSelected, position = $position")
            }
        })
    }

    fun setFragmentInViewPager(fragmentNumber: Int) {
        mViewPager.currentItem = fragmentNumber

    }

    /*
    This function replace another fragment in FrameLayout container by our main Fragment (NoteListFragment.kt - display our notes)
     */

    private fun setNoNotesFragment() {
        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val noNotesFragment = NoNotesFragment()

        ft.replace(binding.mainContainer.id, noNotesFragment, NO_NOTES_FRAGMENT_TAG)

        ft.addToBackStack(NO_NOTES_FRAGMENT_TAG)
        ft.commit()

        fm.executePendingTransactions()

    }

    private fun setNotesListFragment(array: ArrayList<NoteItem>) {
        Log.i("abcd", "set list")

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val args = Bundle()
        args.putParcelableArrayList(DATABASE_NOTES_ARRAY, array)

        val notesListFragment = NotesListFragment()
        notesListFragment.arguments = args

        noteFragment = notesListFragment
        ft.replace(binding.mainContainer.id, notesListFragment, NOTE_LIST_FRAGMENT_TAG)

        ft.addToBackStack(NOTE_LIST_FRAGMENT_TAG)

        ft.commit()
    }

    /*
    Fragment used to create new note
     */
    private fun setCreateNoteFragment() {
        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val createNoteFragment = CreateNoteFragment()

        ft.replace(binding.mainContainer.id, createNoteFragment, CREATE_NOTE_FRAGMENT_TAG)
        ft.addToBackStack(CREATE_NOTE_FRAGMENT_TAG)
        ft.commit()
    }

    /*
    This fragment is setting only from preview mode (NotePreviewFragment.kt)
     */
    private fun setEditNoteFragment(noteObject: Bundle) {

        fm = supportFragmentManager
        ft = fm.beginTransaction()

        val editNoteFragment = EditNoteFragment()

        editNoteFragment.arguments = noteObject

        val containerId = binding.mainContainer.id

        ft.replace(containerId, editNoteFragment, EDIT_NOTE_FRAGMENT_TAG)

        ft.addToBackStack(EDIT_NOTE_FRAGMENT_TAG)

        ft.commit()
    }
//TODO
//    override fun setEditFragment(noteItem: NoteItem) { //Called from OwnColorCreatorActivity.kt
//        val args = Bundle()
//        args.putParcelable(NOTE_TO_EDIT_EXTRA_KEY, noteItem)
//        setEditNoteFragment(args)
//        Log.i("abcd", "set edit")
//    }

    //Update edited note in database
    override fun updateEditedNoteItemObject(noteItem: NoteItem?) {
        if (noteItem != null) {
            ObjectToDatabaseOperations.updateObject(context = this, noteObjects = arrayListOf(noteItem))
        }
    }

    /*
    This fragment is setting when user click RecyclerView item
     */
    private fun setNotePreviewFragment(noteObject: NoteItem) {

        val previewFragment = NotePreviewFragment()

        val args = Bundle()

        args.putParcelable(NOTE_TO_EDIT_EXTRA_KEY, noteObject)

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

        menuItemSearch = menu!!.findItem(R.id.search_main)
        val menuItemArchives = menu.findItem(R.id.archives)

        MainRecyclerAdapter.setOnSnackbarDismissListener(object : MainRecyclerAdapter.OnSnackbarDismissListener {
            override fun snackState(isDismiss: Boolean) {
                menuItemArchives.isVisible = isDismiss
            }
        })
//TODO
//        val frag = supportFragmentManager.findFragmentById(findViewById<FrameLayout>(R.id.main_container).id)
//
//        var drawIcon: Drawable? = null
//        var title = resources.getString(R.string.notes)
//
//
//        if (frag is NotesListFragment || frag is NoNotesFragment) {
//            drawIcon = resources.getDrawable(R.drawable.ic_add_white_24dp)
//            title = resources.getString(R.string.notes)
//            //supportActionBar!!.setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher))
//
//        } else if (frag is NotePreviewFragment) {
//            drawIcon = resources.getDrawable(R.drawable.ic_mode_edit_white_24dp)
//            title = resources.getString(R.string.preview)
//        } else if (frag is EditNoteFragment && frag.tag.equals(EDIT_NOTE_FRAGMENT_TAG)) {
//            drawIcon = resources.getDrawable(R.drawable.ic_done_white_24dp)
//            title = resources.getString(R.string.edit)
//        } else if (frag is CreateNoteFragment) {
//            drawIcon = resources.getDrawable(R.drawable.ic_done_white_24dp)
//            title = resources.getString(R.string.create)
//        }
//
//        supportActionBar!!.title = title
//        findViewById<FloatingActionButton>(R.id.main_fab).setImageDrawable(drawIcon)

        return true
    }
    //TODO
//    fun setTitleAndFab(icon: Drawable?, title: String) {
//        try {
//            supportActionBar!!.title = title
//            findViewById<FloatingActionButton>(R.id.main_fab).setImageDrawable(icon)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    /*
    Select menu item at Toolbar and execute action
     */

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_main -> {
                val intent = Intent(this, SearchActivity::class.java)
                val myArray = getNotesFromDatabase()

                for (x in 0 until myArray.size) {
                    myArray[x].isDeleted = false
                }
                intent.putParcelableArrayListExtra("notesArray", myArray)
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
//        CurrentFragmentState.backPressed = true //Set flag at true. Necessary in onDestroyView() method in CreateNoteFragment.kt
//        val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)
//
//        if (frag is NotesListFragment || frag is NoNotesFragment) {
//            doubleTapBackToExit()
//        } else {
//            if (supportFragmentManager.backStackEntryCount > 1)
//                supportFragmentManager.popBackStack()
//        }
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

    /*
    Listen floatingActionButton
     */
    //TODO
//    private fun floatingActionButtonListener() {
//        binding.mainFab.setOnClickListener {
//            CurrentFragmentState.backPressed = false //Set flag at false. Necessary in onDestroyView() method in CreateNoteFragment.kt
//
//            /*
//            Depending on the current fragment switch to another define below
//             */
//            val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)
//
//            if (frag is NotesListFragment || frag is NoNotesFragment) {
//                setCreateNoteFragment()
//            } else if (frag is CreateNoteFragment && frag.tag.equals(CREATE_NOTE_FRAGMENT_TAG)) {
//                frag.onSaveNote()
//                supportFragmentManager.popBackStack()
//                adapterSize = 1
//            } else if (frag is EditNoteFragment) { //Update RecyclerView item and return to NoteListFragment
//                for (x in 1..2) {
//                    supportFragmentManager.popBackStack()
//                }
//            } else if (frag is NotePreviewFragment) {
//                setEditNoteFragment(frag.arguments)
//            }
//        }
//    }

    //TODO
    /*
    Listener recycler scroll via interface between this activity and NoteListFragment.
    When recycler is scrolling then floating action button is hiden
     */
//    override fun recyclerScrolling(dx: Int?, dy: Int?, newState: Int?) {
//        if (newState == null) {
//            if (dy!! > 0 || dy < 0 && binding.mainFab.isShown)
//                binding.mainFab.hide()
//        } else {
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                binding.mainFab.show()
//            }
//        }
    //}

    /*
    Listen form fragmnt destroyed
     */

    override fun editDestroy(noteObject: NoteItem?) { //Update object in database after edit
        ObjectToDatabaseOperations.updateObject(context = this, noteObjects = arrayListOf(noteObject))
    }

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

        val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id)

        when (frag) {
            is EditNoteFragment -> {
                intent.putParcelableArrayListExtra(CreateNoteFragment.NOTE_OBJECT_TO_COLOR_CREATOR, arrayListOf(frag.setNoteObject()))
            }
            is CreateNoteFragment -> {
                intent.putParcelableArrayListExtra(CreateNoteFragment.NOTE_OBJECT_TO_COLOR_CREATOR, arrayListOf(frag.getNoteObject()))
            }
        }
        startActivity(intent)
    }

    override fun getRecyclerAdapterSize(recyclerAdapterSize: Int) {
        adapterSize = recyclerAdapterSize
        val frag = supportFragmentManager.findFragmentById(binding.mainContainer.id) is NotesListFragment
        if (frag && recyclerAdapterSize == 0)
            setNoNotesFragment()
        else if (!frag && recyclerAdapterSize != 0)
            setNotesListFragment(getNotesFromDatabase())
    }

    override fun getNotes(): ArrayList<NoteItem> {
        return getNotesFromDatabase()
    }

    override fun onNoteClicked(noteObject: NoteItem) {
        CurrentFragmentState.backPressed = false //Set flag
        setNotePreviewFragment(noteObject)
    }
}