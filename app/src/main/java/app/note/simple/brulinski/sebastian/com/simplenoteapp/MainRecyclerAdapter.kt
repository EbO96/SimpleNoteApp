package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MainRecyclerAdapter(var itemsHolder: ArrayList<ItemsHolder>) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(itemsHolder.get(position))

        holder?.itemView?.setOnClickListener {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return itemsHolder.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(itemsHolder: ItemsHolder) {
            val title = itemView.findViewById<TextView>(R.id.titleTextView)
            val note = itemView.findViewById<TextView>(R.id.noteTextView)
            title.text = itemsHolder.title
            note.text = itemsHolder.note
        }
    }
}