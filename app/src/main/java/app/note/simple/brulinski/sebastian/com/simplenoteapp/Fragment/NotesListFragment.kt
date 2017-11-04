package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnRefreshNoteList
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnSetFilter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class NotesListFragment : Fragment(), OnRefreshNoteList, OnSetFilter {

    lateinit var binding: NotesListFragmentBinding //Bind layout
    /**
    Values related with recycler
     */
    lateinit var myRecycler: NoteListRecyclerAdapter //Notes recycler
    lateinit var noteItemArrayList: ArrayList<NoteItem> //Array of note object

    /**
     * Keys
     */
    private val NOTES_ARRAY_KEY = "notes"
    private val LAYOUT_STYLE_KEY = "layout style"

    /**
     * Others values
     */
    companion object {
        private var searchQuery = ""
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("interLog", "NOTELIST CREATE!")
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)
        setHasOptionsMenu(true)
        noteItemArrayList = ObjectToDatabaseOperations.getObjects(context, false)
        initRecyclerAdapter()
        sortNotes()
        return binding.root
    }

    /*
    Recycler
     */
    private fun initRecyclerAdapter() { //Init recycler view
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        myRecycler = NoteListRecyclerAdapter(noteItemArrayList, binding.recyclerView, activity)
        binding.recyclerView.adapter = myRecycler
    }

    //Set filter at recycler
    override fun setFilter(query: String) {
        searchQuery = query
        val filterArrayList = ArrayList<NoteItem>()

        (0 until noteItemArrayList.size)
                .filter {
                    noteItemArrayList[it].title!!.toLowerCase().contains(query.toLowerCase()) ||
                            noteItemArrayList[it].note!!.toLowerCase().contains(query.toLowerCase())
                }
                .mapTo(filterArrayList) { noteItemArrayList[it] }

        myRecycler.noteItemArray = filterArrayList
        myRecycler.notifyDataSetChanged()
    }

    //Sort items in recycler by date
    private fun sortNotes() {
        try {
            Collections.sort(noteItemArrayList, Comparator<NoteItem> { o1, o2 ->
                if (o1.date == null || o2.date == null) {
                    return@Comparator 0
                }
                o2.date!!.compareTo(o1.date!!)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        myRecycler.notifyDataSetChanged()
    }

    /*
    Refresh note list
     */
    override fun onRefreshList(noteItem: NoteItem) {
        if (noteItem.id != null)
            ObjectToDatabaseOperations.updateObject(context, arrayListOf(noteItem))
        else ObjectToDatabaseOperations.insertObject(context, noteItem)

        loadDataToRecycler()
    }

    override fun loadDataToRecycler() {
        noteItemArrayList = ObjectToDatabaseOperations.getObjects(context, false)
        initRecyclerAdapter()
        sortNotes()
        setFilter(searchQuery)
    }

    /**
     * Recycler
     */
    class NoteListRecyclerAdapter(var noteItemArray: ArrayList<NoteItem>, var recyclerView: RecyclerView, var activity: Activity) : RecyclerView.Adapter<NoteListRecyclerAdapter.ViewHolder>() {

        private var deletedItem: NoteItem? = null
        private lateinit var preferences: SharedPreferences
        private lateinit var undoSnack: Snackbar

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            var title = noteItemArray[position].title
            var note = noteItemArray[position].note
            var positionToDelete: Int

            if (title!!.length > 30)
                title = title.substring(0, 30) + "..."
            if (note!!.length > 260)
                note = note.substring(0, 260) + "..."

            EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(holder!!.title, holder.note, holder.card), arrayListOf(noteItemArray[position]))

            holder.title.text = title
            holder.note.text = note

            holder.itemView.setOnClickListener {
                val noteItem = noteItemArray[recyclerView.getChildAdapterPosition(holder.itemView)] //Get clicked object

                val main = (activity as MainActivity)//main statement
                main.setupPreview(noteItem) //Setup preview screen
                main.setEditMode(noteItem) //Fill title field, note field and background color in edit mode (CreateNoteFragment)
                main.getViewPager().setCurrentItem(2, true) //Switch to preview
            }

            holder.itemView.setOnLongClickListener {
                positionToDelete = holder.adapterPosition
                preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                val flag = preferences.getBoolean(activity.getString(R.string.pref_key_archives), true)
                /*
               Make UNDO Snackbar after delete
                 */

                //Customize Snackbar
                if (flag) {
                    undoSnack = Snackbar.make((activity as MainActivity).binding.root, activity.getString(R.string.note_archived), Snackbar.LENGTH_SHORT)
                    undoSnack.setActionTextColor(ContextCompat.getColor(activity, R.color.material_white))
                } else if (!flag) {
                    undoSnack = Snackbar.make((activity as MainActivity).binding.root, activity.getString(R.string.note_deleted), Snackbar.LENGTH_LONG)
                    undoSnack.setActionTextColor(ContextCompat.getColor(activity, R.color.material_red))
                }


                @Suppress("DEPRECATION")
                undoSnack.setAction(activity.getString(R.string.undo), {

                    //Check whether note already exist in array
                    val notExistInArrayFlag = (0 until noteItemArray.size).none { noteItemArray[it] == deletedItem }

                    if (notExistInArrayFlag) { //If deletedItem not exist in array then put deleted into array and notify recycler 
                        this.noteItemArray.add(positionToDelete, deletedItem!!)
                        notifyItemInserted(positionToDelete)
                        recyclerView.scrollToPosition(positionToDelete)

                        if (flag)
                            ObjectToDatabaseOperations.addDeleteFlag(context = activity, noteObjects = arrayListOf(deletedItem!!), flag = false)
                        else ObjectToDatabaseOperations.insertObject(activity, deletedItem!!)

                        if (NotesListFragment.searchQuery.isNotEmpty())
                            (activity as MainActivity).loadDataToRecycler()

                        val main = activity as MainActivity
                        main.setupPreview(deletedItem!!)
                        main.setEditMode(deletedItem!!)
                    }

                }).show()

                deletedItem = noteItemArray.removeAt(positionToDelete)
                notifyItemRemoved(positionToDelete)
                if (flag)
                    ObjectToDatabaseOperations.addDeleteFlag(context = activity, noteObjects = arrayListOf(deletedItem!!), flag = true)
                else ObjectToDatabaseOperations.deleteObjects(activity, arrayListOf(deletedItem!!))

                if (NotesListFragment.searchQuery.isNotEmpty())
                    (activity as MainActivity).loadDataToRecycler()

                val main = activity as MainActivity
                main.setupPreview(Notes.Note.default)
                main.setEditMode(Notes.Note.default)

                true
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return noteItemArray.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title = itemView.findViewById<TextView>(R.id.titleTextView)!!
            val note = itemView.findViewById<TextView>(R.id.noteTextView)!!
            val card = itemView.findViewById<CardView>(R.id.item_card_parent_card)!!
        }
    }
}

