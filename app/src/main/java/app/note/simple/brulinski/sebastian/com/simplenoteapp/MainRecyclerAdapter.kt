package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainRecyclerAdapter(var itemsHolder: ArrayList<ItemsHolder>) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.titleText.text = itemsHolder.get(position).title
        holder!!.noteText.text = itemsHolder.get(position).note
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsHolder.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleTextView)
        val noteText: TextView = view.findViewById(R.id.noteTextView)

        init {

        }
    }
}