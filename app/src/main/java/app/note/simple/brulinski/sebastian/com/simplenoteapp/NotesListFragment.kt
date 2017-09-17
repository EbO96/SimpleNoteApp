package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.database.Cursor
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import java.text.FieldPosition


/**
 * Created by sebas on 15.09.2017.
 */
class NotesListFragment : Fragment() {

    lateinit var binding: NotesListFragmentBinding
    lateinit var itemsObjectsArray: ArrayList<ItemsHolder>
    lateinit var myRecycler: MainRecyclerAdapter
    lateinit var database: LocalDatabase

    companion object {
        var flag: Boolean = true
    }

    lateinit var onEditModeListener_: OnEditModeListener

    interface OnEditModeListener {
        fun switch(title: String, note: String, position: Int)
    }

    fun setOnEditModeListener(onEditModeListener: OnEditModeListener) {
        this.onEditModeListener_ = onEditModeListener
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)

        CurrentFragmentState.CURRENT = MainActivity.NOTE_LIST_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.notes))

        (activity as MainActivity).editItem(this)

        (activity as MainActivity).setToolbarItemsVisibility(true)

        //Database
        database = LocalDatabase(context)

        //Recycler
        binding.recyclerView.setHasFixedSize(true)

        if (savedInstanceState == null)
            flag = arguments.getBoolean("flag", true)
        else flag = savedInstanceState.getBoolean("flag")

        if (flag)
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.recyclerView.itemAnimator = SlideInUpAnimator()

        itemsObjectsArray = ArrayList()
        myRecycler = MainRecyclerAdapter(itemsObjectsArray, binding.recyclerView, database)
        binding.recyclerView.adapter = myRecycler

        //Get notes
        getAndSetNotes()

        //Edit note listener
        editNote()

        //Deleted items listener
        listenDeletedItems()

        //Listen for update
        updateNoteList()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
        outState?.putBoolean("flag", flag)
    }

    fun listenDeletedItems() {
        myRecycler.setOnDeleteItemListener(object : MainRecyclerAdapter.OnDeleteItemListener {
            override fun deletedItemDetails(title: String, note: String, date: String) {

            }
        })
    }

    fun updateNoteList() {
        (activity as MainActivity).setOnUpdateListListener(object : MainActivity.OnUpdateListListener {
            override fun passData(title: String, note: String, position: Int) {
                itemsObjectsArray.removeAt(position)
                itemsObjectsArray.add(ItemsHolder(title, note, CreateNoteFragment.getCurrentDateAndTime()))
                myRecycler.notifyDataSetChanged()
            }
        })
    }
}