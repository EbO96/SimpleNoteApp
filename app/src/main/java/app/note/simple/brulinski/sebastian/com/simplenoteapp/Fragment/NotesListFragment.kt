package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.*
import android.view.*
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FragmentAndObjectStates
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import java.util.*

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class NotesListFragment : Fragment() {

    lateinit var binding: NotesListFragmentBinding //Bind layout
    /**
    Values related with recycler
     */
    lateinit var myRecycler: NoteListRecyclerAdapter //Notes recycler
    lateinit var layoutStyle: LayoutManagerStyle //Recycler items layout manager
    var styleFlag: Boolean = true //Flag define what type of layout can be apply
    lateinit var noteItemArrayList: ArrayList<NoteItem> //Array of note object
    /**
     * Keys
     */
    private val NOTES_ARRAY_KEY = "notes"
    private val LAYOUT_STYLE_KEY = "layout style"

    /**
     * START
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)
        setHasOptionsMenu(true)
        /*
        Change color of status bar
         */
        val colorMng = EditorManager.ColorManager(activity)
        colorMng.changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR), Color.BLACK)

        noteItemArrayList = if (savedInstanceState == null) {
            ObjectToDatabaseOperations.getObjects(context, false)
        } else {
            savedInstanceState.getParcelableArrayList<NoteItem>(NOTES_ARRAY_KEY)
        }

        /*
        Recycler related witch recycler
         */
        initRecyclerAdapter()
        sortNotes() //Sort notes by date and time

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(LAYOUT_STYLE_KEY, layoutStyle.flag) //Save layout style
        /*
        Save fragment state to Bundle and restore notes from it instead of
        getting this data from database
         */
        outState?.putParcelableArrayList(NOTES_ARRAY_KEY, noteItemArrayList)
    }

    /*
  Save Layout Manager style in SharedPreference file
  true -> Linear layout
  false -> StaggeredGridLayout
   */
    private fun setAndSaveLayoutManagerStyle(flag: Boolean) {
        val sharedPref: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.layout_manager_key), flag)
        editor.apply()

        if (flag) {
            styleFlag = flag
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        } else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    /*
    Recycler
     */
    private fun initRecyclerAdapter() { //Init recycler view
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        layoutStyle = LayoutManagerStyle(this.activity)
        styleFlag = layoutStyle.flag

        //Recognize and set layout style
        if (styleFlag)
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        else binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        myRecycler = NoteListRecyclerAdapter(noteItemArrayList, binding.recyclerView, activity)
        binding.recyclerView.adapter = myRecycler
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

    fun refreshRecyclerAfterCreate(noteItem: NoteItem?) {
        if (noteItem != null)
            noteItemArrayList.add(noteItem)
        myRecycler.notifyDataSetChanged()
    }

    fun refreshRecyclerAfterEdit(noteItem: NoteItem?, position: Int?) {
        if (noteItem != null && position != null) {
            ObjectToDatabaseOperations.updateObject(context, arrayListOf(noteItem))
            noteItemArrayList[position] = noteItem
            myRecycler.notifyItemChanged(position)
        }
    }

    /*
    MENU
     */
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val menuInflater: MenuInflater = activity.menuInflater
        menuInflater.inflate(R.menu.items_layout_style_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.main_menu_grid -> {
                setAndSaveLayoutManagerStyle(false)
            }
            R.id.main_menu_linear -> {
                setAndSaveLayoutManagerStyle(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Recycler
     */
    class NoteListRecyclerAdapter(private var noteItemArray: ArrayList<NoteItem>, var recyclerView: RecyclerView, var activity: Activity) : RecyclerView.Adapter<NoteListRecyclerAdapter.ViewHolder>() {

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
                val recyclerPosition = recyclerView.getChildAdapterPosition(holder.itemView)
                val noteItem = noteItemArray[recyclerPosition]
                FragmentAndObjectStates.currentNote = noteItem
                FragmentAndObjectStates.itemPositionInRecycler = recyclerPosition
                (activity as MainActivity).setFragmentInViewPager(2, null)
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
                    this.noteItemArray.add(positionToDelete, deletedItem!!)
                    notifyItemInserted(positionToDelete)
                    recyclerView.scrollToPosition(positionToDelete)
                    FragmentAndObjectStates.itemPositionInRecycler = positionToDelete
                    FragmentAndObjectStates.currentNote = deletedItem

                    if (flag)
                        ObjectToDatabaseOperations.addDeleteFlag(context = activity, noteObjects = arrayListOf(deletedItem!!), flag = false)
                    else ObjectToDatabaseOperations.insertObject(activity, deletedItem!!)

                }).show()

                deletedItem = noteItemArray.removeAt(positionToDelete)
                FragmentAndObjectStates.itemPositionInRecycler = null
                FragmentAndObjectStates.currentNote = null
                notifyItemRemoved(positionToDelete)
                if (flag)
                    ObjectToDatabaseOperations.addDeleteFlag(context = activity, noteObjects = arrayListOf(deletedItem!!), flag = true)
                else ObjectToDatabaseOperations.deleteObjects(activity, arrayListOf(deletedItem!!))

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

