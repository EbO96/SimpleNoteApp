package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MainRecyclerAdapter(var itemsHolder: ArrayList<ItemsHolder>, var recyclerView: RecyclerView, var database: LocalDatabase) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val itemsHolder: ItemsHolder = itemsHolder[position]

        holder?.title?.text = itemsHolder.title
        holder?.note?.text = itemsHolder.note

        holder?.itemView?.setOnLongClickListener {
            val pos: Int = recyclerView.getChildAdapterPosition(holder.itemView)

            database.deleteRow(this.itemsHolder.get(pos).title, this.itemsHolder.get(pos).note)

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