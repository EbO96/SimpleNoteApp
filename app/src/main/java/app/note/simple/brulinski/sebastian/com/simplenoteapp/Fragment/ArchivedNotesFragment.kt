package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.ArchivesRecycler
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ArchivedNotesFragmentBinding
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator

class ArchivedNotesFragment : Fragment() {

    lateinit var mChangeScreenCallback: OnChangeScreenListener
    lateinit var archivedNotesArrayList: ArrayList<ItemsHolder>

    interface OnChangeScreenListener {
        fun replaceFragment()
    }

    fun setOnChangeScreenListener(mChangeScreenCallback: OnChangeScreenListener) {
        this.mChangeScreenCallback = mChangeScreenCallback
    }

    lateinit var mActionModeCallback: OnActionModeListener

    interface OnActionModeListener {
        fun selectedItems(numberOfItems: Int, itemsIdArrayList: ArrayList<String>?)
    }

    fun setOnActionModeListener(mActionModeCallback: OnActionModeListener) {
        this.mActionModeCallback = mActionModeCallback
    }

    private lateinit var binding: ArchivedNotesFragmentBinding
    private lateinit var myRecycler: ArchivesRecycler

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.archived_notes_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        archivedNotesArrayList = arguments.getParcelableArrayList<ItemsHolder>("archivedNoteObject")
        initRecycler(archivedNotesArrayList)
        listenRecyclerSize()
    }

    private fun initRecycler(itemsHolderArrayList: ArrayList<ItemsHolder>) {
        val recycler = binding.archivesRecycler
        recycler.itemAnimator = SlideInRightAnimator()
        recycler.layoutManager = LinearLayoutManager(context)

        myRecycler = ArchivesRecycler(itemsHolderArrayList, recycler, context, this)

        recycler.adapter = myRecycler
    }

    private fun listenRecyclerSize() {
        myRecycler.setOnRecyclerSizeListener(object : ArchivesRecycler.OnRecyclerSizeListener {
            override fun recyclerSize(size: Int) {
                if (size == 0)
                    mChangeScreenCallback.replaceFragment()
            }
        })
    }

    fun deleteSelectedItems(itemsIdArrayList: ArrayList<String>) {

        context.database.use {
            for (x in 0 until itemsIdArrayList.size) {
                delete(
                        LocalSQLAnkoDatabase.TABLE_NOTES, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(itemsIdArrayList[x])
                )
                delete(
                        LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, "${LocalSQLAnkoDatabase.NOTE_ID}=?", arrayOf(itemsIdArrayList[x])
                )
            }
        }

        for (x in 0 until itemsIdArrayList.size) {
            var indexOfItem = 0
            for (y in 0 until archivedNotesArrayList.size) {
                if (archivedNotesArrayList[y].id.equals(itemsIdArrayList[x])) {
                    indexOfItem = y
                    break
                }
            }
            archivedNotesArrayList.removeAt(indexOfItem)
            myRecycler.notifyItemRemoved(indexOfItem)

            mActionModeCallback.selectedItems(0, null)
            myRecycler.selectedItemsIdArrayList = ArrayList<String>()
        }
    }
}