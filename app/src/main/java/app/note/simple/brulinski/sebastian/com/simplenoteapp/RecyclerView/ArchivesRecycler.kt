package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class ArchivesRecycler(private val notesArrayList: ArrayList<ItemsHolder>, private val recycler: RecyclerView, private val ctx: Context) : RecyclerView.Adapter<ArchivesRecycler.MyViewHolder>() {
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val noteObject = notesArrayList[position]

        val title = noteObject.title
        val note = noteObject.note

        EditorManager.FontStyleManager.recogniseAndSetFont(noteObject.fontStyle, holder!!.titleTextView, holder.noteTextView)
        val bg = EditorManager.ColorManager(ctx)

        bg.recogniseAndSetColor(noteObject.bgColor, arrayListOf(holder.card), "BG") //Change note color
        bg.recogniseAndSetColor(noteObject.textColor, arrayListOf(holder.titleTextView, holder.noteTextView), "FONT")

        holder!!.titleTextView.text = title
        holder.noteTextView.text = note

        holder.deleteImageButton.setOnClickListener {
            //TODO delete confirmation alert dialog
        }

        holder.restoreImageButton.setOnClickListener {
            //TODO just insert note
        }
    }

    override fun getItemCount(): Int {
        return notesArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.archived_note_card, parent, false)
        return MyViewHolder(view)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.titleTextViewArch)
        val noteTextView = itemView.findViewById<TextView>(R.id.noteTextViewArch)
        val deleteImageButton = itemView.findViewById<ImageButton>(R.id.deleteArchivedNoteImageButtonArch)
        val restoreImageButton = itemView.findViewById<ImageButton>(R.id.restoreArchivedNoteImageButtonArch)
        val card = itemView.findViewById<CardView>(R.id.archives_card)
    }
}