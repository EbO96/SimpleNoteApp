package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import org.jetbrains.anko.db.insert
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment() {

    lateinit var bindingFrag: CreateNoteFragmentBinding
    lateinit var database: LocalSQLAnkoDatabase


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        bindingFrag = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)
        CurrentFragmentState.CURRENT = MainActivity.CREATE_NOTE_FRAGMENT_TAG


        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.create))

        database = LocalSQLAnkoDatabase(context)

        return bindingFrag.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenBarOptions(bindingFrag.createNoteTitleField, bindingFrag.createNoteNoteField)
    }

    fun saveNote(title: String, note: String) {
        try {

            database.use {
                val titleCol = Pair<String, String>("title", title.trim())
                val noteCol = Pair<String, String>("note", note.trim())
                val dateCol = Pair<String, String>("date", getCurrentDateAndTime())

                insert("notes", titleCol, noteCol, dateCol)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateAndTime(): String { //Get current time from system
            val calendar = Calendar.getInstance()

            return SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a").format(calendar.getTime())
        }
    }

    override fun onDestroyView() {
        if ((!TextUtils.isEmpty(bindingFrag.createNoteTitleField.text.toString().trim()) || !TextUtils.isEmpty(bindingFrag.createNoteNoteField.text.toString().trim())) &&
                CurrentFragmentState.CURRENT.equals(MainActivity.CREATE_NOTE_FRAGMENT_TAG) && !CurrentFragmentState.backPressed)
            saveNote(bindingFrag.createNoteTitleField.text.toString(), bindingFrag.createNoteNoteField.text.toString())
        super.onDestroyView()
    }

    fun listenBarOptions(titleView: View, noteView: View) {
        bindingFrag.selectAll.setOnClickListener {

        }

        bindingFrag.copyAll.setOnClickListener {

        }

        bindingFrag.paste.setOnClickListener {

        }

        bindingFrag.undo.setOnClickListener {

        }

        bindingFrag.deleteAll.setOnClickListener {
            deleteAllOption()
        }

        bindingFrag.fontStyle.setOnClickListener {
            showFontMenu()
        }

        bindingFrag.textColor.setOnClickListener {

        }

        bindingFrag.noteColor.setOnClickListener {

        }
    }

    /*
    Options bar
     */

    private fun deleteAllOption() {
        bindingFrag.createNoteTitleField.text = null
        bindingFrag.createNoteNoteField.text = null
    }

    private fun showFontMenu() {
        val popupMenu = PopupMenu(context, bindingFrag.fontStyle)
        popupMenu.menuInflater.inflate(R.menu.font_menu, popupMenu.menu)
        popupMenu.show()

        //Listen for actions
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                when (p0!!.itemId) {
                    R.id.default_font -> {
                        setUpFontStyle(Typeface.DEFAULT)
                    }
                    R.id.italic_font -> {
                        setUpFontStyle(Typeface.ITALIC)
                    }
                    R.id.bold_italic_font -> {
                        setUpFontStyle(Typeface.BOLD_ITALIC)
                    }
                    R.id.serif_font -> {
                        setUpFontStyle(Typeface.SERIF)
                    }
                    R.id.sans_serif_font -> {
                        setUpFontStyle(Typeface.SANS_SERIF)
                    }
                    R.id.monospace_font -> {
                        setUpFontStyle(Typeface.MONOSPACE)
                    }
                }
                return true
            }
        })
    }

    private fun setUpFontStyle(typeInt: Int) {
        bindingFrag.createNoteTitleField.typeface = Typeface.defaultFromStyle(typeInt)
        bindingFrag.createNoteNoteField.typeface = Typeface.defaultFromStyle(typeInt)
    }

    private fun setUpFontStyle(type: Typeface) {
        bindingFrag.createNoteTitleField.typeface = type
        bindingFrag.createNoteNoteField.typeface = type
    }
}


