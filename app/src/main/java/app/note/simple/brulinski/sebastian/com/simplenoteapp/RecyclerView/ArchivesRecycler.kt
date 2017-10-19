package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.ArchivedNotesFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ArchivedNotesItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class ArchivesRecycler(private var notesArrayList: ArrayList<ArchivedNotesItemsHolder>, private val recycler: RecyclerView, private val ctx: Context, private val fragment: ArchivedNotesFragment) : RecyclerView.Adapter<ArchivesRecycler.MyViewHolder>() {

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

        for (x in 0 until notesArrayList.size) {
            Log.i("checkbox", "item position in array: $x and item isChecked = ${notesArrayList[x].isSelected}")
        }
        if (checkedObject) {
            holder.checkBox.isChecked = true
            holder.buttonsCard.visibility = View.INVISIBLE
        } else {
            holder.checkBox.isChecked = false
            holder.buttonsCard.visibility = View.VISIBLE
        }

        EditorManager.FontStyleManager.recogniseAndSetFont(noteObject.fontStyle, holder.titleTextView, holder.noteTextView)
        val bg = EditorManager.ColorManager(ctx)

        bg.recogniseAndSetColor(noteObject.bgColor, arrayListOf(holder.card), "BG") //Change note color
        bg.recogniseAndSetColor(noteObject.textColor, arrayListOf(holder.titleTextView, holder.noteTextView), "FONT")

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

        holder.deleteImageButton.setOnClickListener {
            positionToDelete = recycler.getChildAdapterPosition(holder.itemView)
            val alert = AlertDialog.Builder(ctx).create()

            alert.setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_delete_black_24dp))
            alert.setTitle(ctx.getString(R.string.delete_this_note))

            alert.setButton(AlertDialog.BUTTON_POSITIVE, ctx.getString(R.string.yes), { _, i ->
                ctx.database.use {
                    delete(LocalSQLAnkoDatabase.TABLE_NOTES, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(noteObject.id))
                    delete(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, "${LocalSQLAnkoDatabase.NOTE_ID}=?", arrayOf(noteObject.id))
                }

                notesArrayList.removeAt(positionToDelete)
                notifyItemRemoved(positionToDelete)

                mSizeCallback.recyclerSize(notesArrayList.size)
            })

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, ctx.getString(R.string.no), { _, i ->
                //Do nothing
            })

            alert.show()
        }

        holder.restoreImageButton.setOnClickListener {
            positionToDelete = recycler.getChildAdapterPosition(holder.itemView)
            val alert = AlertDialog.Builder(ctx).create()

            alert.setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_restore_black_24dp))
            alert.setTitle(ctx.getString(R.string.restore_this_note))

            alert.setButton(AlertDialog.BUTTON_POSITIVE, ctx.getString(R.string.yes), { _, i ->
                val isDeletedValue = ContentValues()
                isDeletedValue.put(LocalSQLAnkoDatabase.IS_DELETED, false.toString())

                ctx.database.use {
                    update(LocalSQLAnkoDatabase.TABLE_NOTES, isDeletedValue, "${LocalSQLAnkoDatabase.ID}=?", arrayOf(noteObject.id))
                    update(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, isDeletedValue, "${LocalSQLAnkoDatabase.NOTE_ID}=?", arrayOf(noteObject.id))
                }
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
        val size = notesArrayList.size
        return size
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
        val buttonsCard = itemView.findViewById<CardView>(R.id.archived_note_card_buttons_card)
        val checkBox = itemView.findViewById<CheckBox>(R.id.archived_note_card_checkBox)
    }

    fun getArray(): ArrayList<ArchivedNotesItemsHolder> {

        return notesArrayList
    }
}