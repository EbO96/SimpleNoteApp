package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R


class MainRecyclerAdapter(private var itemsHolderArray: ArrayList<ItemsHolder>, var recyclerView: RecyclerView, var ctx: Context) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    private var deletedItem: ItemsHolder? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var undoSnack: Snackbar


    interface OnSnackbarDismissListener {
        fun snackState(isDismiss: Boolean)
    }

    companion object {
        lateinit var mSnackCallback: OnSnackbarDismissListener

        fun setOnSnackbarDismissListener(mSnackCallback: OnSnackbarDismissListener) {
            this.mSnackCallback = mSnackCallback
        }
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        var title = itemsHolderArray[position].title
        var note = itemsHolderArray[position].note
        var positionToDelete: Int

        if (title.length > 30)
            title = title.substring(0, 30) + "..."
        if (note.length > 260)
            note = note.substring(0, 260) + "..."

        EditorManager.FontStyleManager.recogniseAndSetFont(itemsHolderArray[position].fontStyle, holder!!.title, holder.note)
        val bg = EditorManager.ColorManager(ctx)

        bg.recogniseAndSetColor(itemsHolderArray[position].bgColor, arrayListOf(holder.card), "BG") //Change note color
        bg.recogniseAndSetColor(itemsHolderArray[position].textColor, arrayListOf(holder.title, holder.note), "FONT")

        holder.title?.text = title
        holder.note.text = note


        holder.itemView?.setOnClickListener {
            (ctx as MainActivity).onNoteClicked(this.itemsHolderArray[position])
        }

        holder.itemView?.setOnLongClickListener {
            positionToDelete = holder.adapterPosition
            preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
            val flag = preferences.getBoolean(ctx.getString(R.string.pref_key_archives), true)

            /*
           Make UNDO snackbar after delete
             */

            if (flag) {
                undoSnack = Snackbar.make((ctx as MainActivity).binding.root, ctx.getString(R.string.note_archived), Snackbar.LENGTH_SHORT)
                undoSnack.setActionTextColor(ContextCompat.getColor(ctx, R.color.material_white))
            } else if (!flag) {
                undoSnack = Snackbar.make((ctx as MainActivity).binding.root, ctx.getString(R.string.note_deleted), Snackbar.LENGTH_LONG)
                undoSnack.setActionTextColor(ContextCompat.getColor(ctx, R.color.material_red))
            }


            @Suppress("DEPRECATION")
            undoSnack.setAction(ctx.getString(R.string.undo), {
                this.itemsHolderArray.add(positionToDelete, deletedItem!!)
                notifyItemInserted(positionToDelete)
                recyclerView.scrollToPosition(positionToDelete)

                if (flag)
                    addDeleteFlag(deletedItem!!.id, false)
                else ObjectToDatabaseOperations.insertNoteObject(deletedItem!!, ctx)

            }).setCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    mSnackCallback.snackState(true)
                }

                override fun onShown(sb: Snackbar?) {
                    mSnackCallback.snackState(false)
                    super.onShown(sb)
                }
            }).show()

            deletedItem = itemsHolderArray.removeAt(positionToDelete)
            notifyItemRemoved(positionToDelete)

            if (flag)
                addDeleteFlag(deletedItem!!.id, true)
            else ObjectToDatabaseOperations.deleteNoteObject(deletedItem!!, ctx)
            true
        }
    }

    fun getArray(): ArrayList<ItemsHolder>{
        return itemsHolderArray
    }

    private fun addDeleteFlag(itemId: String, flag: Boolean) {
        val isDeletedValue = ContentValues()
        isDeletedValue.put(LocalSQLAnkoDatabase.IS_DELETED, flag.toString())

        ctx.database.use {
            //Delete from database
            update(
                    LocalSQLAnkoDatabase.TABLE_NOTES, isDeletedValue, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(itemId)
            )
            update(
                    LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, isDeletedValue, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(itemId)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsHolderArray.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleTextView)
        val note = itemView.findViewById<TextView>(R.id.noteTextView)
        val card = itemView.findViewById<CardView>(R.id.item_card_parent_card)
    }
}