package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import com.cocosw.bottomsheet.BottomSheet
import com.labo.kaji.fragmentanimations.MoveAnimation
import org.jetbrains.anko.db.insert
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment() {

    lateinit var bindingFrag: CreateNoteFragmentBinding
    lateinit var database: LocalSQLAnkoDatabase

    /*
    Fonts tags
     */

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingFrag = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)
        CurrentFragmentState.CURRENT = MainActivity.CREATE_NOTE_FRAGMENT_TAG

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.create))

        database = LocalSQLAnkoDatabase(context)

        return bindingFrag.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenBarOptions()
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

    fun listenBarOptions() {
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
            changeBcgColor(arrayListOf(bindingFrag.createNoteParentCard))
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
        BottomSheet.Builder(activity).title(getString(R.string.fonts)).sheet(R.menu.font_menu).listener(object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    R.id.default_font -> {
                        EditorManager.FontManager.setUpFontStyle(Typeface.DEFAULT, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontManager.currentFont = EditorManager.FontManager.DEFAULT_FONT
                    }
                    R.id.italic_font -> {
                        EditorManager.FontManager.setUpFontStyle(Typeface.ITALIC, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontManager.currentFont = EditorManager.FontManager.ITALIC_FONT
                    }
                    R.id.bold_italic_font -> {
                        EditorManager.FontManager.setUpFontStyle(Typeface.BOLD_ITALIC, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontManager.currentFont = EditorManager.FontManager.BOLD_ITALIC_FONT
                    }
                    R.id.serif_font -> {
                        EditorManager.FontManager.setUpFontStyle(Typeface.SERIF, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontManager.currentFont = EditorManager.FontManager.SERIF_FONT
                    }
                    R.id.sans_serif_font -> {
                        EditorManager.FontManager.setUpFontStyle(Typeface.SANS_SERIF, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontManager.currentFont = EditorManager.FontManager.SANS_SERIF_FONT
                    }
                    R.id.monospace_font -> {
                        EditorManager.FontManager.setUpFontStyle(Typeface.MONOSPACE, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontManager.currentFont = EditorManager.FontManager.MONOSPACE_FONT
                    }
                }
            }
        }).show()
    }

    fun changeBcgColor(viewArray: ArrayList<Any>) { //Change color of background
        BottomSheet.Builder(activity).title(getString(R.string.colors)).sheet(R.menu.color_menu).listener(object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    R.id.col_red -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_red))
                        EditorManager.BackgroundColorManager.currentColor = EditorManager.BackgroundColorManager.RED
                    }
                    R.id.col_blue -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_blue))
                        EditorManager.BackgroundColorManager.currentColor = EditorManager.BackgroundColorManager.BLUE
                    }
                    R.id.col_green -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_green))
                        EditorManager.BackgroundColorManager.currentColor = EditorManager.BackgroundColorManager.GREEN
                    }
                    R.id.col_yellow -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_yellow))
                        EditorManager.BackgroundColorManager.currentColor = EditorManager.BackgroundColorManager.YELLOW
                    }
                    R.id.col_white -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_white))
                        EditorManager.BackgroundColorManager.currentColor = EditorManager.BackgroundColorManager.WHITE
                    }
                }
            }
        }).show()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, 500)
        } else {
            if (enter) {
                return MoveAnimation.create(MoveAnimation.LEFT, enter, 500)
            } else {
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, 500)
            }
        }
    }

}


