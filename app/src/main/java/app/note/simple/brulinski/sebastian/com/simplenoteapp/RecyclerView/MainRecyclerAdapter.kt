package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

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
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.MainRecyclerSizeListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R


class MainRecyclerAdapter(private var noteItemArray: ArrayList<NoteItem>, var recyclerView: RecyclerView, var ctx: Context) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    private var deletedItem: NoteItem? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var undoSnack: Snackbar
    val sizeCallback: MainRecyclerSizeListener = (ctx as MainActivity)

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

        var title = noteItemArray[position].title
        var note = noteItemArray[position].note
        var positionToDelete: Int

        if (title!!.length > 30)
            title = title.substring(0, 30) + "..."
        if (note!!.length > 260)
            note = note.substring(0, 260) + "..."

        //TODO change card color's
        holder!!.title?.text = title
        holder.note.text = note


        holder.itemView?.setOnClickListener {
            (ctx as MainActivity).onNoteClicked(this.noteItemArray[position])
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
                this.noteItemArray.add(positionToDelete, deletedItem!!)
                notifyItemInserted(positionToDelete)
                recyclerView.scrollToPosition(positionToDelete)
                sizeCallback.getRecyclerAdapterSize(noteItemArray.size)

                if (flag)
                    ObjectToDatabaseOperations.addDeleteFlag(context = ctx, noteObjects = arrayListOf(deletedItem!!), flag = false)
                else ObjectToDatabaseOperations.insertObject(ctx, deletedItem!!)

            }).setCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    mSnackCallback.snackState(true)
                }

                override fun onShown(sb: Snackbar?) {
                    mSnackCallback.snackState(false)
                    super.onShown(sb)
                }
            }).show()

            deletedItem = noteItemArray.removeAt(positionToDelete)
            notifyItemRemoved(positionToDelete)

            if (flag)
                ObjectToDatabaseOperations.addDeleteFlag(context = ctx, noteObjects = arrayListOf(deletedItem!!), flag = true)
            else ObjectToDatabaseOperations.deleteObjects(ctx, arrayListOf(deletedItem!!))

            Log.i("recyclerSize", "recycler size is ${noteItemArray.size}")
            sizeCallback.getRecyclerAdapterSize(noteItemArray.size)
            true
        }
    }

    fun getArray(): ArrayList<NoteItem> {
        return noteItemArray
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noteItemArray.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleTextView)
        val note = itemView.findViewById<TextView>(R.id.noteTextView)
        val card = itemView.findViewById<CardView>(R.id.item_card_parent_card)
    }
}