package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class SearchResultRecycler(private var notesArray: ArrayList<ItemsHolder>, private var ctx: Context, private var recycler: RecyclerView) : RecyclerView.Adapter<SearchResultRecycler.MyViewHolder>() {
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val singleNote: ItemsHolder = notesArray[position]

        var title = singleNote.title
        var note = singleNote.note


        if (title.length > 30)
            title = title.substring(0, 30) + "..."
        if (note.length > 260)
            note = note.substring(0, 260) + "..."

        EditorManager.FontStyleManager.recogniseAndSetFont(singleNote.fontStyle, holder!!.title, holder.note)
        val bg = EditorManager.ColorManager(ctx)

        bg.recogniseAndSetColor(singleNote.bgColor, arrayListOf(holder.card), "BG") //Change note color
        bg.recogniseAndSetColor(singleNote.textColor, arrayListOf(holder.title, holder.note), "FONT")

        holder.title?.text = title
        holder.note.text = note
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notesArray.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleTextView)
        val note = itemView.findViewById<TextView>(R.id.noteTextView)
        val card = itemView.findViewById<CardView>(R.id.item_card_parent_card)
    }

    fun setFilter(filter: ArrayList<ItemsHolder>) {
        notesArray = filter
        notifyDataSetChanged()
    }
}