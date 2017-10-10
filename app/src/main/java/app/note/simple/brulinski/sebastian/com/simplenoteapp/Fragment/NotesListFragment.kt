package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNoteProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NotesProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.MainRecyclerAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import com.labo.kaji.fragmentanimations.MoveAnimation
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import org.jetbrains.anko.db.select
import java.util.*

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class NotesListFragment : Fragment() {

    companion object {
        lateinit var itemsObjectsArray: ArrayList<ItemsHolder>
    }

    lateinit var binding: NotesListFragmentBinding
    lateinit var myRecycler: MainRecyclerAdapter
    lateinit var database: LocalSQLAnkoDatabase
    lateinit var layoutStyle: LayoutManagerStyle
    var styleFlag: Boolean = true

    lateinit var mScrollCallback: OnListenRecyclerScroll

    interface OnListenRecyclerScroll {
        fun recyclerScrolling(dx: Int?, dy: Int?, newState: Int?)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)
        //Get notes
        database = LocalSQLAnkoDatabase(context)

        if (savedInstanceState == null) {
            getAndSetNotes()
        } else {
            itemsObjectsArray = savedInstanceState.getParcelableArrayList<ItemsHolder>("notes")
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

        myRecycler = MainRecyclerAdapter(itemsObjectsArray, binding.recyclerView, database, context)
        binding.recyclerView.adapter = myRecycler
    }


    private fun getAndSetNotes() {
        itemsObjectsArray = ArrayList()

        val notesPropertiesArray: ArrayList<NotesProperties> = ArrayList()

        database.use {
            val properties = select(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES).parseList(MyRowParserNoteProperties())
            val size = properties.size - 1

            for (x in 0..size) {
                notesPropertiesArray.add(NotesProperties(properties.get(x).get(x).id, properties.get(x).get(x).bgColor,
                        properties.get(x).get(x).textColor, properties.get(x).get(x).fontColor))
            }
        }

        database.use {
            val notes = select(LocalSQLAnkoDatabase.TABLE_NOTES).parseList(MyRowParserNotes())
            val size = notes.size

            for (x in 0 until size) {
                itemsObjectsArray.add(ItemsHolder(notes.get(x).get(x).id!!, notes.get(x).get(x).title!!, notes.get(x).get(x).note!!,
                        notes.get(x).get(x).date!!, notesPropertiesArray.get(x).bgColor!!, notesPropertiesArray.get(x).textColor!!,
                        notesPropertiesArray.get(x).fontColor!!))
            }
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


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("flag", layoutStyle.flag)
        /*
        Save fragment state do Bundle and restore notes from it instead of
        getting this data from database
         */
        outState?.putParcelableArrayList("notes", itemsObjectsArray)
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
        else
            return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
    }
}