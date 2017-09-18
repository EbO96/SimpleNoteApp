package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.database.Cursor
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Holder.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.MainRecyclerAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


/**
 * Created by sebas on 15.09.2017.
 */
class NotesListFragment : Fragment() {

    lateinit var binding: NotesListFragmentBinding
    lateinit var itemsObjectsArray: ArrayList<ItemsHolder>
    lateinit var myRecycler: MainRecyclerAdapter
    lateinit var database: LocalDatabase
    lateinit var layoutStyle: LayoutManagerStyle
    var styleFlag: Boolean = true

    lateinit var onEditModeListener_: OnEditModeListener

    interface OnEditModeListener {
        fun switch(title: String, note: String, position: Int)
    }

    fun setOnEditModeListener(onEditModeListener: OnEditModeListener) {
        this.onEditModeListener_ = onEditModeListener
    }

    lateinit var mClear: OnClearPreviewList

    interface OnClearPreviewList {
        fun clear()
    }

    fun setOnClearPreviewList(mClear: OnClearPreviewList) {
        this.mClear = mClear
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)

        Log.i("frag", "create")
        CurrentFragmentState.CURRENT = MainActivity.NOTE_LIST_FRAGMENT_TAG

        if (!resources.getBoolean(R.bool.twoPaneMode))
            (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.notes))

        //(activity as MainActivity).changeRecyclerLayout()

        //Database
        database = LocalDatabase(context)

        //Recycler
        binding.recyclerView.setHasFixedSize(true)

        layoutStyle = LayoutManagerStyle(this.activity)

        styleFlag = layoutStyle.flag

        if (styleFlag)
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.recyclerView.itemAnimator = SlideInUpAnimator()

        itemsObjectsArray = ArrayList()
        myRecycler = MainRecyclerAdapter(itemsObjectsArray, binding.recyclerView, database)
        binding.recyclerView.adapter = myRecycler

        //Get notes
        getAndSetNotes()

        (activity as MainActivity).editItem(this)
        (activity as MainActivity).clearPreviewList(this)

        //Edit note listener
        editNote()

        //Listen for update
        updateNoteList()

        //Deleted items listener
        listenDeletedItems()

        //Change layout manager
        (activity as MainActivity).setOnChangeLayoutListener(object : MainActivity.OnChangeLayoutListener {
            override fun passData(flag: Boolean) {
                if (flag) {
                    styleFlag = flag
                    binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                } else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        })

        return binding.root
    }

    fun getAndSetNotes() {
        val data: Cursor = database.getAllNotes()
        var title: String
        var note: String
        var date: String

        if (data.count > 0) {
            while (data.moveToNext()) {
                title = data.getString(1)
                note = data.getString(2)
                date = data.getString(3)

                itemsObjectsArray.add(ItemsHolder(title, note, date))
            }
            myRecycler.notifyDataSetChanged()
        }
    }

    fun editNote() {
        myRecycler.setOnEditItemListener(object : MainRecyclerAdapter.OnEditItemListener {
            override fun itemDetails(title: String, note: String, position: Int) {
                onEditModeListener_.switch(title, note, position)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("flag", layoutStyle.flag)
    }

    fun listenDeletedItems() {
        myRecycler.setOnDeleteItemListener(object : MainRecyclerAdapter.OnDeleteItemListener {
            override fun deletedItemDetails(title: String, note: String, date: String) {
                mClear.clear()
            }
        })
    }

    fun updateNoteList() {
        (activity as MainActivity).setOnUpdateListListener(object : MainActivity.OnUpdateListListener {
            override fun passData(title: String, note: String, position: Int) {
                Log.i("frag", "update")
                itemsObjectsArray.set(position, ItemsHolder(title, note, CreateNoteFragment.getCurrentDateAndTime()))
                myRecycler.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }
}