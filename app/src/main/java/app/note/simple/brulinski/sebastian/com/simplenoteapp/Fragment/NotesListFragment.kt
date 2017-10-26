package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.*
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.MainRecyclerAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import com.labo.kaji.fragmentanimations.MoveAnimation
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import java.util.*

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class NotesListFragment : Fragment() {

    companion object {
        lateinit var itemsObjectsArray: ArrayList<NoteItem>
    }

    lateinit var binding: NotesListFragmentBinding
    lateinit var myRecycler: MainRecyclerAdapter
    lateinit var layoutStyle: LayoutManagerStyle
    var styleFlag: Boolean = true
    private val NOTES_ARRAY_KEY = "notes"
    private val LAYOUT_STYLE_KEY = "layout style"

    lateinit var mScrollCallback: OnListenRecyclerScroll

    interface OnListenRecyclerScroll {
        fun recyclerScrolling(dx: Int?, dy: Int?, newState: Int?)
    }

    lateinit var mGetNotesCallback: OnGetNotesFromParentActivity

    interface OnGetNotesFromParentActivity {
        fun getNotes(): ArrayList<NoteItem>
    }

    fun setOnGetNotesFromParentActivity(mGetNotesCallback: OnGetNotesFromParentActivity) {
        this.mGetNotesCallback = mGetNotesCallback
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)
        setHasOptionsMenu(true)

        /*
        Change color of status bar
         */
        val colorMng = EditorManager.ColorManager(activity)
        colorMng.changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR), Color.BLACK)

        if (savedInstanceState == null) {
            itemsObjectsArray = arguments.getParcelableArrayList(MainActivity.DATABASE_NOTES_ARRAY)
        } else {
            itemsObjectsArray = savedInstanceState.getParcelableArrayList<NoteItem>(NOTES_ARRAY_KEY)
        }

        initRecyclerAdapter()

        sortNotes() //Sort notes by date and time

        //Listen for recycler scrolling
        recyclerScrollingListener()
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(LAYOUT_STYLE_KEY, layoutStyle.flag)
        /*
        Save fragment state do Bundle and restore notes from it instead of
        getting this data from database
         */
        itemsObjectsArray = myRecycler.getArray()
        outState?.putParcelableArrayList(NOTES_ARRAY_KEY, itemsObjectsArray)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).setTitleAndFab(ContextCompat.getDrawable(context, R.drawable.ic_add_white_24dp),
                resources.getString(R.string.notes))
    }

    /*
  Save Layout Manager style in SharedPreference file
  true -> Linear layout
  false -> StaggeredGridLayout
   */
    private fun setAndSaveLayoutManagerStyle(flag: Boolean) {
        val sharedPref: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.layout_manager_key), flag)
        editor.apply()

        if (flag) {
            styleFlag = flag
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        } else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun initRecyclerAdapter() { //Init recycler view
        binding.recyclerView.setHasFixedSize(true)

        layoutStyle = LayoutManagerStyle(this.activity)

        styleFlag = layoutStyle.flag


        if (styleFlag)
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.recyclerView.itemAnimator = SlideInRightAnimator()

        myRecycler = MainRecyclerAdapter(itemsObjectsArray, binding.recyclerView, context)
        binding.recyclerView.adapter = myRecycler
    }

    private fun sortNotes() {
        try {
            Collections.sort(itemsObjectsArray, object : Comparator<NoteItem> {
                override fun compare(o1: NoteItem, o2: NoteItem): Int {
                    if (o1.date == null || o2.date == null) {
                        return 0
                    }
                    return o2.date!!.compareTo(o1.date!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        myRecycler.notifyDataSetChanged()
    }

    private fun recyclerScrollingListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                mScrollCallback.recyclerScrolling(dx, dy, null)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                mScrollCallback.recyclerScrolling(null, null, newState)
            }
        })
    }

    /*
    MENU
     */

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val menuInflater: MenuInflater = activity.menuInflater
        menuInflater.inflate(R.menu.items_layout_style_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var flag = true

        when (item!!.itemId) {
            R.id.main_menu_grid -> {
                flag = false
            }
            R.id.main_menu_linear -> {
                flag = true
            }
        }
        setAndSaveLayoutManagerStyle(flag)
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mScrollCallback = (activity as OnListenRecyclerScroll)
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnListenRecyclerScroll and OnChangeItemVisible")
        }
    }

    override fun onAttach(context: Context?) {
        try {
            mGetNotesCallback = (context as OnGetNotesFromParentActivity)
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnGetNotesFromParentActivity")
        }
        super.onAttach(context)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed)
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        else return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
    }

    override fun onStart() {
        itemsObjectsArray = mGetNotesCallback.getNotes()

        initRecyclerAdapter()
        sortNotes()
        recyclerScrollingListener()

        super.onStart()
    }
}