package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
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

    lateinit var mMenuItemsVisible: OnChangeItemVisible

    interface OnChangeItemVisible {
        fun changeMenuItemsVisibility(visible: Boolean)
    }

    lateinit var mScrollCallback: OnListenRecyclerScroll

    interface OnListenRecyclerScroll {
        fun recyclerScrolling(dx: Int?, dy: Int?, newState: Int?)
    }

    lateinit var onEditModeListener_: OnEditModeListener

    interface OnEditModeListener {
        fun switch(itemId: String, title: String, note: String, position: Int)
    }

    fun setOnEditModeListener(onEditModeListener: OnEditModeListener) {
        this.onEditModeListener_ = onEditModeListener
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("frag", "List Create")
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)

        CurrentFragmentState.CURRENT = MainActivity.NOTE_LIST_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.notes))

        if (savedInstanceState == null)
            mMenuItemsVisible.changeMenuItemsVisibility(true)

        //Database
        database = LocalSQLAnkoDatabase(context)

        //Get notes
        if (savedInstanceState == null) {
            getAndSetNotes()
        } else {
            itemsObjectsArray = savedInstanceState.getParcelableArrayList<ItemsHolder>("notes")
        }

        /*

         */
        initRecyclerAdapter()

        (activity as MainActivity).editItem(this)

        //Edit note listener
        editNote()

        //Deleted items listener
        //listenDeletedItems()

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

        //Listen for search
        updateListBySearch()

        return binding.root
    }

    private fun initRecyclerAdapter() { //Init recycler view
        //Recycler
        binding.recyclerView.setHasFixedSize(true)

        layoutStyle = LayoutManagerStyle(this.activity)

        styleFlag = layoutStyle.flag


        if (styleFlag)
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.recyclerView.itemAnimator = SlideInRightAnimator()

        myRecycler = MainRecyclerAdapter(itemsObjectsArray, binding.recyclerView, database, context)
        binding.recyclerView.adapter = myRecycler
        myRecycler.notifyDataSetChanged()
    }

    fun getAndSetNotes() {
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
        //debugNotesArray()
    }

//    fun debugNotesArray() {
//        for (i in 0 until itemsObjectsArray.size) {
//            Log.i("notesArray", itemsObjectsArray[i].id)
//            Log.i("notesArray", itemsObjectsArray[i].title)
//            Log.i("notesArray", itemsObjectsArray[i].note)
//            Log.i("notesArray", itemsObjectsArray[i].date)
//            Log.i("notesArray", itemsObjectsArray[i].bgColor)
//            Log.i("notesArray", itemsObjectsArray[i].textColor)
//            Log.i("notesArray", itemsObjectsArray[i].fontStyle)
//        }
//    }

    fun editNote() {
        myRecycler.setOnEditItemListener(object : MainRecyclerAdapter.OnEditItemListener {
            override fun itemDetails(itemId: String, title: String, note: String, position: Int) {
                onEditModeListener_.switch(itemId, title, note, position)
                CurrentFragmentState.backPressed = false
                CurrentFragmentState.PREVIOUS = MainActivity.NOTE_LIST_FRAGMENT_TAG
            }
        })
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
            mMenuItemsVisible = (activity as OnChangeItemVisible)

        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnListenRecyclerScroll and OnChangeItemVisible")
        }
    }

    fun recyclerScrollingListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                mScrollCallback.recyclerScrolling(dx, dy, null)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                mScrollCallback.recyclerScrolling(null, null, newState)
            }
        })
    }

    fun updateListBySearch() {
        (activity as MainActivity).setOnSearchResultListener(object : MainActivity.OnSearchResultListener {
            override fun passNewText(newText: String?) {
                val newList: ArrayList<ItemsHolder> = ArrayList()

                for (itemsHolder: ItemsHolder in itemsObjectsArray) {
                    val title = itemsHolder.title.toLowerCase()
                    val note = itemsHolder.note.toLowerCase()

                    if (title.contains(newText!!.toLowerCase()) || note.contains(newText.toLowerCase())) {
                        newList.add(itemsHolder)
                    }
                }
                myRecycler.setFilter(newList)
            }
        })
    }

    override fun onStop() {
        mMenuItemsVisible.changeMenuItemsVisibility(false)
        super.onStop()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        } else {
            if (enter) {
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            } else {
                return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            }
        }
    }
}