package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R


class MainRecyclerAdapter(var itemsHolder: ArrayList<ItemsHolder>, var recyclerView: RecyclerView, var database: LocalSQLAnkoDatabase, var ctx: Context) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    lateinit var onEditItemListener_: OnEditItemListener

    interface OnEditItemListener {
        fun itemDetails(title: String, note: String, position: Int)
    }

    fun setOnEditItemListener(onEditItemListener: OnEditItemListener) {
        this.onEditItemListener_ = onEditItemListener
    }

    lateinit var onDeleteItemListener_: OnDeleteItemListener

    interface OnDeleteItemListener {
        fun deletedItemDetails(title: String, note: String, date: String)
    }

    fun setOnDeleteItemListener(onDeleteItemListener: OnDeleteItemListener) {
        this.onDeleteItemListener_ = onDeleteItemListener
    }


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val itemsHolder: ItemsHolder = itemsHolder[position]

        var title = itemsHolder.title
        var note = itemsHolder.note

        if (title.length > 30)
            title = title.substring(0, 30) + "..."
        if (note.length > 260)
            note = note.substring(0, 260) + "..."

        holder?.title?.text = title
        holder?.note?.text = note

        var pos: Int

        holder?.itemView?.setOnClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)
            onEditItemListener_.itemDetails(this.itemsHolder.get(pos).title, this.itemsHolder.get(pos).note, pos)
        }

        holder?.itemView?.setOnLongClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)

            val title: String = this.itemsHolder.get(pos).title
            val note: String = this.itemsHolder.get(pos).note
            val date: String = this.itemsHolder.get(pos).date

            val item = this.itemsHolder[pos]
            var undo = false

            Snackbar.make((ctx as MainActivity).binding.root, ctx.getString(R.string.note_deleted), Snackbar.LENGTH_LONG).setAction(ctx.getString(R.string.undo), {
                undo = true
                this.itemsHolder.add(pos, item)
                notifyItemInserted(pos)

            }).setCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if(!undo){
                        database.use {
                            delete(
                                    "notes", "title=? AND note=? AND date=?", arrayOf(title, note, date)
                            )
                        }
                    }
                }
            }).show()

            undo = false

            onDeleteItemListener_.deletedItemDetails(title, note, date)
            this.itemsHolder.removeAt(pos)
            notifyItemRemoved(pos)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return itemsHolder.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleTextView)
        val note = itemView.findViewById<TextView>(R.id.noteTextView)
    }

}