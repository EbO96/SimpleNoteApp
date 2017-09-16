package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MainRecyclerAdapter(var itemsHolder: ArrayList<ItemsHolder>, var recyclerView: RecyclerView, var database: LocalDatabase) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    lateinit var onEditItemListener_: OnEditItemListener

    interface OnEditItemListener {
        fun itemDetails(title: String, note: String)
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

        holder?.title?.text = itemsHolder.title
        holder?.note?.text = itemsHolder.note

        var pos: Int

        holder?.itemView?.setOnClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)
            onEditItemListener_.itemDetails(this.itemsHolder.get(pos).title, this.itemsHolder.get(pos).note)
        }

        holder?.itemView?.setOnLongClickListener {
            pos = recyclerView.getChildAdapterPosition(holder.itemView)

            val title: String = this.itemsHolder.get(pos).title
            val note: String = this.itemsHolder.get(pos).note
            val date: String = this.itemsHolder.get(pos).date

            database.deleteRow(title, note, date)

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