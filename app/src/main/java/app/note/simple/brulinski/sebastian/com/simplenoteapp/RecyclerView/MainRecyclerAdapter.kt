package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.content.ContentValues
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R


class MainRecyclerAdapter(var itemsHolder: ArrayList<ItemsHolder>, var recyclerView: RecyclerView, var ctx: Context) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    var deletedItem: ItemsHolder? = null

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val itemsHolder: ItemsHolder = itemsHolder[position]

        var title = itemsHolder.title
        var note = itemsHolder.note


        if (title.length > 30)
            title = title.substring(0, 30) + "..."
        if (note.length > 260)
            note = note.substring(0, 260) + "..."

        EditorManager.FontStyleManager.recogniseAndSetFont(itemsHolder.fontStyle, holder!!.title, holder.note)
        val bg = EditorManager.ColorManager(ctx)

        bg.recogniseAndSetColor(itemsHolder.bgColor, arrayListOf(holder.card), "BG") //Change note color
        bg.recogniseAndSetColor(itemsHolder.textColor, arrayListOf(holder.title, holder.note), "FONT")

        holder.title?.text = title
        holder.note.text = note

        var pos: Int

        holder.itemView?.setOnClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)
            (ctx as MainActivity).onNoteClicked(this.itemsHolder[pos])
        }

        holder.itemView?.setOnLongClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)

            @Suppress("DEPRECATION")
            Snackbar.make((ctx as MainActivity).binding.root, ctx.getString(R.string.note_deleted), Snackbar.LENGTH_LONG).setAction(ctx.getString(R.string.undo), {
                this.itemsHolder.add(pos, deletedItem!!)
                notifyItemInserted(pos)
                recyclerView.scrollToPosition(pos)

                addDeleteFlag(deletedItem!!.id, false)

            }).show()

            deletedItem = this.itemsHolder.removeAt(pos)
            notifyItemRemoved(pos)

            addDeleteFlag(deletedItem!!.id, true)
            true
        }
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
        return itemsHolder.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleTextView)
        val note = itemView.findViewById<TextView>(R.id.noteTextView)
        val card = itemView.findViewById<CardView>(R.id.item_card_parent_card)
    }
}