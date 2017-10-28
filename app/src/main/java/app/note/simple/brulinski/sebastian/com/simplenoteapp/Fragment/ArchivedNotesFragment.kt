package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.ArchivesActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
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

        myRecycler = ArchivesRecycler(itemsHolderArrayList, recycler, activity, this)

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

    /**
     * Recycler
     */
    class ArchivesRecycler(private var notesArrayList: ArrayList<NoteItem>, private val recycler: RecyclerView, private val activity: Activity, private val fragment: ArchivedNotesFragment) : RecyclerView.Adapter<ArchivesRecycler.MyViewHolder>() {

        lateinit var mSizeCallback: OnRecyclerSizeListener

        interface OnRecyclerSizeListener {
            fun recyclerSize(size: Int)
        }

        fun setOnRecyclerSizeListener(mSizeCallback: OnRecyclerSizeListener) {
            this.mSizeCallback = mSizeCallback
        }

        override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
            val noteObject = notesArrayList[position]
            var positionToDelete: Int

            val title = noteObject.title
            val note = noteObject.note

            val checkedObject = notesArrayList[position].isSelected

            holder!!.checkBox.setOnCheckedChangeListener(null)

            if (checkedObject!!) {
                holder.checkBox.isChecked = true
                holder.buttonsCard.visibility = View.INVISIBLE
            } else {
                holder.checkBox.isChecked = false
                holder.buttonsCard.visibility = View.VISIBLE
            }

            EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(holder.titleTextView, holder.noteTextView, holder.card), arrayListOf(noteObject))

            holder.titleTextView.text = title
            holder.noteTextView.text = note

            holder.checkBox.setOnClickListener {
                val checked = holder.checkBox.isChecked

                if (checked) {
                    holder.buttonsCard.visibility = View.INVISIBLE
                } else if (!checked) {
                    holder.buttonsCard.visibility = View.VISIBLE
                }
                notesArrayList[position].isSelected = checked
                fragment.onCheckBoxesListener(notesArrayList)
            }

            holder.deleteImageButton.setOnClickListener {
                positionToDelete = recycler.getChildAdapterPosition(holder.itemView)
                val alert = android.support.v7.app.AlertDialog.Builder(activity).create()

                alert.setIcon(ContextCompat.getDrawable(activity, R.drawable.ic_delete_black_24dp))
                alert.setTitle(activity.getString(R.string.delete_this_note))

                alert.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.yes), { _, i ->
                    ObjectToDatabaseOperations.deleteObjects(context = activity, noteObjects = arrayListOf(noteObject))//Update object ( delete flag )

                    notesArrayList.removeAt(positionToDelete)
                    notifyItemRemoved(positionToDelete)

                    mSizeCallback.recyclerSize(notesArrayList.size)
                })

                alert.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, activity.getString(R.string.no), { _, _ ->
                    //Do nothing
                })

                alert.show()
            }

            holder.restoreImageButton.setOnClickListener {
                positionToDelete = recycler.getChildAdapterPosition(holder.itemView)
                val alert = android.support.v7.app.AlertDialog.Builder(activity).create()

                alert.setIcon(ContextCompat.getDrawable(activity, R.drawable.ic_restore_black_24dp))
                alert.setTitle(activity.getString(R.string.restore_this_note))

                alert.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.yes), { _, _ ->
                    ObjectToDatabaseOperations.addDeleteFlag(context = activity, noteObjects = arrayListOf(noteObject), flag = false) //Update object ( delete flag )
                    notesArrayList.removeAt(positionToDelete)
                    notifyItemRemoved(positionToDelete)

                    mSizeCallback.recyclerSize(notesArrayList.size)
                })

                alert.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, activity.getString(R.string.no), { _, i ->
                    //Do nothing
                })
                alert.show()
            }
        }

        override fun getItemCount(): Int {
            return notesArrayList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.archived_note_card, parent, false)
            return MyViewHolder(view)
        }

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.titleTextViewArch)
            val noteTextView: TextView = itemView.findViewById(R.id.noteTextViewArch)
            val deleteImageButton: ImageButton = itemView.findViewById(R.id.deleteArchivedNoteImageButtonArch)
            val restoreImageButton: ImageButton = itemView.findViewById(R.id.restoreArchivedNoteImageButtonArch)
            val card = itemView.findViewById<CardView>(R.id.archives_card)
            val buttonsCard: CardView = itemView.findViewById(R.id.archived_note_card_buttons_card)
            val checkBox: CheckBox = itemView.findViewById(R.id.archived_note_card_checkBox)
        }

        fun getArray(): ArrayList<NoteItem> {
            return notesArrayList
        }
    }
}