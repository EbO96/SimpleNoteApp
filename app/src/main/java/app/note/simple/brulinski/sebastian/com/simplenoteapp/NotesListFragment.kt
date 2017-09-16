package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.database.Cursor
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)

        //Database
        database = LocalDatabase(context)

        //Recycler
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.itemAnimator = SlideInUpAnimator()

        itemsObjectsArray = ArrayList()
        myRecycler = MainRecyclerAdapter(itemsObjectsArray, binding.recyclerView, database)
        binding.recyclerView.adapter = myRecycler

        //Get notes
        getAndSetNotes()

        return binding.root
    }

    fun getAndSetNotes() {
        val data: Cursor = database.getAllNotes()
        var title: String
        var note: String

        if (data.count > 0) {
            while (data.moveToNext()) {
                title = data.getString(1)
                note = data.getString(2)

                itemsObjectsArray.add(ItemsHolder(title, note))
            }
            myRecycler.notifyDataSetChanged()

        }
    }
}