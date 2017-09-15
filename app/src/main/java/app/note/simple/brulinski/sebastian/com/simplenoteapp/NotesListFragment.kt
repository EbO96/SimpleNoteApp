package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding

/**
 * Created by sebas on 15.09.2017.
 */
class NotesListFragment : Fragment() {

    lateinit var binding: NotesListFragmentBinding
    lateinit var itemsObject: ItemsHolder
    lateinit var itemsObjectsArray: ArrayList<ItemsHolder>
    lateinit var myRecycler: MainRecyclerAdapter
    lateinit var database: LocalDatabase

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)

        try {
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

            itemsObjectsArray = ArrayList()

            myRecycler = MainRecyclerAdapter(itemsObjectsArray)
            binding.recyclerView.adapter = myRecycler

            database = LocalDatabase(context)

            itemsObject = ItemsHolder()
            itemsObject.note = "test 1"
            itemsObject.title = "test 2"
            itemsObjectsArray.add(itemsObject)

            myRecycler.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }
}