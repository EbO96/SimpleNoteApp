package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.ArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class ArchivesRecycler(private var notesArrayList: ArrayList<NoteItem>, private val recycler: RecyclerView, private val ctx: Context, private val fragment: ArchivedNotesFragment) : RecyclerView.Adapter<ArchivesRecycler.MyViewHolder>() {

    lateinit var mSizeCallback: OnRecyclerSizeListener

    interface OnRecyclerSizeListener {
        fun recyclerSize(size: Int)
    }

    fun setOnRecyclerSizeListener(mSizeCallback: OnRecyclerSizeListener) {
        this.mSizeCallback = mSizeCallback
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val noteObject = notesArrayList[position]
        var positionToDelete: Int

        val title = noteObject.title
        val note = noteObject.note

        val checkedObject = notesArrayList[position].isSelected

        holder!!.checkBox.setOnCheckedChangeListener(null)

        if (checkedObject!!) {
            holder.checkBox.isChecked = true
            holder.buttonsCard.visibility = View.INVISIBLE
        } else {
            holder.checkBox.isChecked = false
            holder.buttonsCard.visibility = View.VISIBLE
        }


        holder.titleTextView.text = title
        holder.noteTextView.text = note

        holder.checkBox.setOnClickListener {
            val checked = holder.checkBox.isChecked

            if (checked) {
                holder.buttonsCard.visibility = View.INVISIBLE
            } else if (!checked) {
                holder.buttonsCard.visibility = View.VISIBLE
            }
            notesArrayList[position].isSelected = checked
            fragment.onCheckBoxesListener(notesArrayList)
        }

        //TODO new implementation of buttons (code below)
        holder.deleteImageButton.setOnClickListener {
            positionToDelete = recycler.getChildAdapterPosition(holder.itemView)
            val alert = AlertDialog.Builder(ctx).create()

            alert.setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_delete_black_24dp))
            alert.setTitle(ctx.getString(R.string.delete_this_note))

            alert.setButton(AlertDialog.BUTTON_POSITIVE, ctx.getString(R.string.yes), { _, i ->
                ObjectToDatabaseOperations.deleteObjects(context = ctx, noteObjects = arrayListOf(noteObject))//Update object ( delete flag )

                notesArrayList.removeAt(positionToDelete)
                notifyItemRemoved(positionToDelete)

                mSizeCallback.recyclerSize(notesArrayList.size)
            })

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, ctx.getString(R.string.no), { _, _ ->
                //Do nothing
            })

            alert.show()
        }

        holder.restoreImageButton.setOnClickListener {
            positionToDelete = recycler.getChildAdapterPosition(holder.itemView)
            val alert = AlertDialog.Builder(ctx).create()

            alert.setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_restore_black_24dp))
            alert.setTitle(ctx.getString(R.string.restore_this_note))

            alert.setButton(AlertDialog.BUTTON_POSITIVE, ctx.getString(R.string.yes), { _, _ ->
                ObjectToDatabaseOperations.addDeleteFlag(context = ctx, noteObjects = arrayListOf(noteObject), flag = false) //Update object ( delete flag )
                notesArrayList.removeAt(positionToDelete)
                notifyItemRemoved(positionToDelete)

                mSizeCallback.recyclerSize(notesArrayList.size)
            })

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, ctx.getString(R.string.no), { _, i ->
                //Do nothing
            })
            alert.show()
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
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextViewArch)
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextViewArch)
        val deleteImageButton: ImageButton = itemView.findViewById(R.id.deleteArchivedNoteImageButtonArch)
        val restoreImageButton: ImageButton = itemView.findViewById(R.id.restoreArchivedNoteImageButtonArch)
        val card = itemView.findViewById<CardView>(R.id.archives_card)
        val buttonsCard: CardView = itemView.findViewById(R.id.archived_note_card_buttons_card)
        val checkBox: CheckBox = itemView.findViewById(R.id.archived_note_card_checkBox)
    }

    fun getArray(): ArrayList<NoteItem> {
        return notesArrayList
    }
}