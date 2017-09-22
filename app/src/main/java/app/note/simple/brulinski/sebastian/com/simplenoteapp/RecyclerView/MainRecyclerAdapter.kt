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
    var deletedItem: ItemsHolder? = null
    var position = 0
    var filterListSize = 0
    var undoClicked = false

    interface OnEditItemListener {
        fun itemDetails(itemId: String, title: String, note: String, position: Int)
    }

    fun setOnEditItemListener(onEditItemListener: OnEditItemListener) {
        this.onEditItemListener_ = onEditItemListener
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val itemsHolder: ItemsHolder = itemsHolder[position]

        var title = itemsHolder.title
        var note = itemsHolder.note


        if (title.length > 30)
            title = title.substring(0, 30) + "..."
        if (note.length > 260)
            note = note.substring(0, 260) + "..."

        holder!!.title?.text = title
        holder.note.text = note

        var pos: Int

        holder.itemView?.setOnClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)
            onEditItemListener_.itemDetails(this.itemsHolder.get(pos).id, this.itemsHolder.get(pos).title, this.itemsHolder.get(pos).note, pos)
        }

        holder.itemView?.setOnLongClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)

            val itemId: String = this.itemsHolder.get(pos).id

            @Suppress("DEPRECATION")
            Snackbar.make((ctx as MainActivity).binding.root, ctx.getString(R.string.note_deleted), Snackbar.LENGTH_LONG).setAction(ctx.getString(R.string.undo), {
                undoClicked = true
                this.itemsHolder.add(pos, deletedItem!!)

                //Execute this if statement only when deleting item is call from search mode
                if (filterListSize != 0)
                    NotesListFragment.itemsObjectsArray.add(this.position, deletedItem!!)

                notifyItemInserted(pos)

            }).setCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    notifyDataSetChanged()
                    if (!undoClicked) {
                        database.use {
                            delete(
                                    LocalSQLAnkoDatabase.TABLE_NOTES, "_id=?", arrayOf(itemId)
                            )
                        }
                        database.use {
                            delete(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, "note_id=?", arrayOf(itemId))
                        }
                    }
                    undoClicked = false
                }
            }).show()


            deletedItem = this.itemsHolder[pos]
            this.position = pos
            this.itemsHolder.removeAt(pos)
            this.position = NotesListFragment.itemsObjectsArray.indexOf(deletedItem!!)
            //Execute this if statement only when deleting item is call from search mode
            if (filterListSize != 0)
                NotesListFragment.itemsObjectsArray.removeAt(this.position)
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

    fun setFilter(newItemList: ArrayList<ItemsHolder>) {
        filterListSize = newItemList.size
        itemsHolder = ArrayList()
        itemsHolder.addAll(newItemList)
        notifyDataSetChanged()
    }


}