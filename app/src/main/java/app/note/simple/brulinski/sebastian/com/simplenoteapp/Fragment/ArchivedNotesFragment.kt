package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.ArchivesActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.ArchivesRecycler
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ArchivedNotesFragmentBinding
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator

class ArchivedNotesFragment : Fragment() {

    lateinit var mChangeScreenCallback: OnChangeScreenListener
    lateinit var archivedNotesArrayList: ArrayList<NoteItem>
    private lateinit var deleteMenuItem: MenuItem
    private lateinit var restoreMenuItem: MenuItem
    private lateinit var selectAllMenuItem: MenuItem
    private var itemsToDeleteOrRestore = ArrayList<NoteItem>()
    private val SELECTED_ALL_KEY = "selected_all"
    private var isSelectedAll = true

    companion object {
        val BUNDLE_KEY = "my_bundle_array"
    }

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

        val bundleData = arguments.getParcelableArrayList<NoteItem>(BUNDLE_KEY)
        archivedNotesArrayList = bundleData


        initRecycler(archivedNotesArrayList)
        listenRecyclerSize()

        return binding.root
    }

    private fun initRecycler(itemsHolderArrayList: ArrayList<NoteItem>) {
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

    private fun deleteSelectedItems(itemsArrayList: ArrayList<NoteItem>) {
        ObjectToDatabaseOperations.deleteObjects(context, itemsArrayList)
        removeSelectedItems()
        resetInterfaceAndValuesAfterMultipleDelete()
    }

    private fun restoreSelectedItems(itemsToRestore: ArrayList<NoteItem>) {
        ObjectToDatabaseOperations.addDeleteFlag(context, itemsToRestore, false)
            removeSelectedItems()
            resetInterfaceAndValuesAfterMultipleDelete()
        }

    private fun removeSelectedItems() {
        for (x in 0 until itemsToDeleteOrRestore.size) {
            archivedNotesArrayList.removeAt(archivedNotesArrayList.indexOf(itemsToDeleteOrRestore[x]))
        }
        myRecycler.notifyDataSetChanged()
    }


    private fun resetInterfaceAndValuesAfterMultipleDelete() {
        //Reset toolbar and arrays
        (activity as ArchivesActivity).supportActionBar!!.title = getString(R.string.archives)
        deleteMenuItem.isVisible = false
        restoreMenuItem.isVisible = false
        itemsToDeleteOrRestore = ArrayList()

        if (archivedNotesArrayList.size == 0)
            mChangeScreenCallback.replaceFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val menuInflater: MenuInflater = activity.menuInflater
        menuInflater.inflate(R.menu.archives_menu, menu)
        deleteMenuItem = menu!!.findItem(R.id.archives_delete).setVisible(false)
        restoreMenuItem = menu.findItem(R.id.archives_restore).setVisible(false)
        selectAllMenuItem = menu.findItem(R.id.archives_select_all).setVisible(true)
        onCheckBoxesListener(archivedNotesArrayList)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var message = ""
        val itemsSize = itemsToDeleteOrRestore.size

        when (item!!.itemId) {
            R.id.archives_delete -> {

                message = "${getString(R.string.delete)} ${getString(R.string.notesLowerCase)}? (${itemsSize})"

                makeDeleteOrRestoreConfirmDialog(message,
                        R.drawable.ic_delete_black_24dp, true) //True means that method delete items
            }
            R.id.archives_restore -> {

                message = "${getString(R.string.restore)} ${getString(R.string.notesLowerCase)}? (${itemsSize})"

                makeDeleteOrRestoreConfirmDialog(message,
                        R.drawable.ic_restore_black_24dp, false) //False means that method delete items
            }

            R.id.archives_select_all -> {

                for (x in 0 until archivedNotesArrayList.size) {
                    archivedNotesArrayList[x].isSelected = isSelectedAll
                }

                myRecycler.notifyDataSetChanged()
                onCheckBoxesListener(archivedNotesArrayList)
                isSelectedAll = !isSelectedAll
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeDeleteOrRestoreConfirmDialog(title: String, icon: Int, isDeleteOrRestore: Boolean) {
        val alert = AlertDialog.Builder(context).create()

        alert.setIcon(ContextCompat.getDrawable(context, icon))
        alert.setTitle(title)

        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), { _, i ->
            if (isDeleteOrRestore) //Delete items
                deleteSelectedItems(itemsToDeleteOrRestore)
            else restoreSelectedItems(itemsToDeleteOrRestore) //Restore items
        })

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), { _, i ->
            //Do nothing
        })
        alert.show()
    }

    fun onCheckBoxesListener(itemsIdArrayList: ArrayList<NoteItem>) {
        itemsToDeleteOrRestore.clear()

        for (x in 0 until itemsIdArrayList.size) {
            if (itemsIdArrayList[x].isSelected!!)
                itemsToDeleteOrRestore.add(itemsIdArrayList[x])
        }

        val arraySize = itemsToDeleteOrRestore.size

        if (itemsToDeleteOrRestore.size > 0) {
            deleteMenuItem.isVisible = true
            restoreMenuItem.isVisible = true
            (activity as ArchivesActivity).setToolbarTitle("$arraySize ${getString(R.string.items_selected)}")
        } else {
            deleteMenuItem.isVisible = false
            restoreMenuItem.isVisible = false
            (activity as ArchivesActivity).setToolbarTitle(getString(R.string.archives))
        }
    }
}