package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.ArchivesActivity
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
    private lateinit var deleteMenuItem: MenuItem
    private lateinit var restoreMenuItem: MenuItem
    private lateinit var itemsToDelete: ArrayList<String>

    interface OnChangeScreenListener {
        fun replaceFragment()
    }

    fun setOnChangeScreenListener(mChangeScreenCallback: OnChangeScreenListener) {
        this.mChangeScreenCallback = mChangeScreenCallback
    }

    private lateinit var binding: ArchivedNotesFragmentBinding
    private lateinit var myRecycler: ArchivesRecycler

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.archived_notes_fragment, container, false)
        setHasOptionsMenu(true)

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

    private fun deleteSelectedItems(itemsIdArrayList: ArrayList<String>) {

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

            myRecycler.selectedItemsIdArrayList = ArrayList<String>()
            //Reset toolbar and arrays
            (activity as ArchivesActivity).supportActionBar!!.title = getString(R.string.archives)
            deleteMenuItem.isVisible = false
            restoreMenuItem.isVisible = false
            itemsToDelete = ArrayList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val menuInflater: MenuInflater = activity.menuInflater
        menuInflater.inflate(R.menu.archives_menu, menu)
        deleteMenuItem = menu!!.findItem(R.id.archives_delete).setVisible(false)
        restoreMenuItem = menu!!.findItem(R.id.archives_restore).setVisible(false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.archives_delete -> {
                makeDeleteConfirmDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeDeleteConfirmDialog() {
        val alert = AlertDialog.Builder(context).create()

        alert.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp))
        alert.setTitle("${getString(R.string.delete)} ${itemsToDelete.size} ${getString(R.string.notesLowerCase)}?")

        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), { _, i ->
            deleteSelectedItems(itemsToDelete)
        })

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), { _, i ->
            //Do nothing
        })
        alert.show()
    }

    fun onCheckBoxesListener(numberOfItems: Int, itemsIdArrayList: ArrayList<String>?) {
        if (numberOfItems > 0) {
            deleteMenuItem.isVisible = true
            restoreMenuItem.isVisible = true
            (activity as ArchivesActivity).setToolbarTitle("$numberOfItems ${getString(R.string.items_selected)}")
        } else {
            deleteMenuItem.isVisible = false
            restoreMenuItem.isVisible = false
            (activity as ArchivesActivity).setToolbarTitle(getString(R.string.archives))
        }
        if (itemsIdArrayList != null)  //After delete
            itemsToDelete = itemsIdArrayList
    }
}