package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNoteProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NotesProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.MainRecyclerAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import com.labo.kaji.fragmentanimations.MoveAnimation
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import org.jetbrains.anko.db.select
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class NotesListFragment : Fragment() {

    companion object {
        lateinit var itemsObjectsArray: ArrayList<ItemsHolder>
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
        fun getNotes(): ArrayList<ItemsHolder>
    }

    fun setOnGetNotesFromParentActivity(mGetNotesCallback: OnGetNotesFromParentActivity) {
        this.mGetNotesCallback = mGetNotesCallback
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)

        EditorManager.ColorManager(context).changeStatusBarColor(activity, EditorManager.ColorManager.BLACK, null)
        if (savedInstanceState == null) {
            itemsObjectsArray = arguments.getParcelableArrayList(MainActivity.DATABASE_NOTES_ARRAY)
        } else {
            Log.i("startLog", "get notes from saved instance state")
            itemsObjectsArray = savedInstanceState.getParcelableArrayList<ItemsHolder>(NOTES_ARRAY_KEY)
        }

        initRecyclerAdapter()

        sortNotes() //Sort notes by date and time

        //Change layout manager
        (activity as MainActivity).setOnChangeLayoutListener(object : MainActivity.OnChangeLayoutListener {
            override fun passData(flag: Boolean) {
                if (flag) {
                    styleFlag = flag
                    binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                } else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        })

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


    private fun getNotesFromDatabase() {
        Log.d("ResumeLog", "Resume")
        itemsObjectsArray = ArrayList()

        val notesPropertiesArray: ArrayList<NotesProperties> = ArrayList()

        context.database.use {
            val properties = select(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", "false").parseList(MyRowParserNoteProperties())
            val size = properties.size - 1

            for (x in 0..size) {
                notesPropertiesArray.add(NotesProperties(properties[x].get(x).id, properties[x].get(x).bgColor,
                        properties[x].get(x).textColor, properties[x].get(x).fontStyle))
            }
        }
        var size = 0
        var notes: List<List<Notes.Note>>? = null

        context.database.use {
            notes = select(LocalSQLAnkoDatabase.TABLE_NOTES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", false.toString()).parseList(MyRowParserNotes())
            size = notes!!.size
        }

        for (x in 0 until size) {
            itemsObjectsArray.add(ItemsHolder(notes!![x].get(x).id!!, notes!![x].get(x).title!!, notes!![x].get(x).note!!,
                    notes!![x].get(x).date!!, notesPropertiesArray[x].bgColor!!, notesPropertiesArray[x].textColor!!,
                    notesPropertiesArray.get(x).fontStyle!!, false))
        }
    }

    private fun sortNotes() {
        try {
            Collections.sort(itemsObjectsArray, object : Comparator<ItemsHolder> {
                override fun compare(o1: ItemsHolder, o2: ItemsHolder): Int {
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

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mScrollCallback = (activity as OnListenRecyclerScroll)

        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnListenRecyclerScroll and OnChangeItemVisible")
        }
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

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed)
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        else return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
    }

    override fun onStart() {
//        Log.i("startLog", "onStart()")
        itemsObjectsArray = mGetNotesCallback.getNotes()
//        Log.i("startLog", "array log")
//
//        for (x in 0 until itemsObjectsArray.size) {
//            Log.i("startLog", itemsObjectsArray[x].bgColor)
//        }
        initRecyclerAdapter()
        sortNotes()
        recyclerScrollingListener()

        super.onStart()
    }
}