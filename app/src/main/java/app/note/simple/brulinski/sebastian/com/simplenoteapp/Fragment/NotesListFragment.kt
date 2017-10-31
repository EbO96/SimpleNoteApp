package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.*
import android.util.Log
import android.view.*
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.LayoutManagerStyle
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnRefreshNoteListListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.NotesListFragmentBinding
import java.util.*

@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class NotesListFragment : Fragment(), OnRefreshNoteListListener {

    override fun onNoteEdited(noteItem: NoteItem?) {
        if (noteItem != null) {
            for (x in 0 until noteItemArrayList.size) {
                if (noteItemArrayList[x].id == noteItem.id) {
                    noteItemArrayList[x] = noteItem
                    myRecycler.notifyDataSetChanged()
                    break
                }
            }
            ObjectToDatabaseOperations.updateObject(context, arrayListOf(noteItem))
            sortNotes()
        }
    }

    override fun onNoteCreated(noteItem: NoteItem?) {
        if (noteItem != null) {
            ObjectToDatabaseOperations.insertObject(context, noteItem)
            noteItemArrayList.add(noteItem)
            myRecycler.notifyDataSetChanged()
            sortNotes()
        }

    }

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
     *
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("FragOverMet", "$this - onCreateView()")
        binding = DataBindingUtil.inflate(inflater, R.layout.notes_list_fragment, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        noteItemArrayList = ObjectToDatabaseOperations.getObjects(context, false)
        initRecyclerAdapter()
        sortNotes()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        //outState?.putBoolean(LAYOUT_STYLE_KEY, layoutStyle.flag) //Save layout style
        /*
        Save fragment state to Bundle and restore notes from it instead of
        getting this data from database
         */
        //outState?.putParcelableArrayList(NOTES_ARRAY_KEY, noteItemArrayList)
    }

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        if (savedInstanceState != null)
//            noteItemArrayList = savedInstanceState.getParcelableArrayList<NoteItem>(NOTES_ARRAY_KEY)
//        super.onViewStateRestored(savedInstanceState)
//    }

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

    //Refresh recycler methods
    fun refreshRecyclerAfterCreate(noteItem: NoteItem?) {

    }

    //Refresh recycler methods
    fun refreshRecyclerAfterEdit(noteItem: NoteItem?, position: Int?) {

    }

    /**
     * Recycler
     */
    class NoteListRecyclerAdapter(private var noteItemArray: ArrayList<NoteItem>, var recyclerView: RecyclerView, var activity: Activity) : RecyclerView.Adapter<NoteListRecyclerAdapter.ViewHolder>() {

        private var deletedItem: NoteItem? = null
        private lateinit var preferences: SharedPreferences
        private lateinit var undoSnack: Snackbar

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            Log.i("interLog", "onBindViewHolder - Recycler")

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

                (activity as MainActivity).refreshPreview(noteItem)
                (activity as MainActivity).refreshEdit(noteItem)
                (activity as MainActivity).getViewPager().setCurrentItem(2, true) //Switch to preview
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

                    if (flag)
                        ObjectToDatabaseOperations.addDeleteFlag(context = activity, noteObjects = arrayListOf(deletedItem!!), flag = false)
                    else ObjectToDatabaseOperations.insertObject(activity, deletedItem!!)

                }).addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }
                }).show()

                deletedItem = noteItemArray.removeAt(positionToDelete)
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

