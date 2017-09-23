package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import com.cocosw.bottomsheet.BottomSheet
import com.labo.kaji.fragmentanimations.MoveAnimation
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
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
        var bgColor = ""
        var fontStyle = ""

        if (savedInstanceState != null) {
            bgColor = savedInstanceState.getString("bg_color")
            fontStyle = savedInstanceState.getString("font_style")
        } else {
            bgColor = EditorManager.BackgroundColorManager.currentBgColor
            fontStyle = EditorManager.FontStyleManager.currentFontStyle
        }

        EditorManager.FontStyleManager.recogniseAndSetFont(fontStyle, bindingFrag.createNoteTitleField, bindingFrag.createNoteNoteField)
        val bg = EditorManager.BackgroundColorManager(context)

        bg.recogniseAndSetBackgroundColor(bgColor, bindingFrag.createNoteParentCard)

        return bindingFrag.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenBarOptions()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("bg_color", EditorManager.BackgroundColorManager.currentBgColor)
        outState.putString("font_style", EditorManager.FontStyleManager.currentFontStyle)
    }

    fun saveNote(title: String, note: String) {
        try {
            val titleCol = Pair<String, String>("title", title.trim())
            val noteCol = Pair<String, String>("note", note.trim())
            val dateCol = Pair<String, String>("date", getCurrentDateAndTime())
            var idList: List<List<Notes.Note>>? = null
            var size = 0

            database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES, titleCol, noteCol, dateCol)
            }

            database.use {
                idList = select(LocalSQLAnkoDatabase.TABLE_NOTES).whereArgs("_id", titleCol, noteCol).
                        parseList(MyRowParserNotes())
                size = idList!!.size
            }

            val noteIdCol = Pair<String, String>("note_id", idList!!.get(size - 1).get(size - 1).id!!)
            val bgColorCol = Pair<String, String>("bg_color", EditorManager.BackgroundColorManager.currentBgColor)
            val textColorCol = Pair<String, String>("text_color", EditorManager.FontColorManager.currentFontColor)
            val fontStyleCol = Pair<String, String>("font_style", EditorManager.FontStyleManager.currentFontStyle)

            database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, noteIdCol, bgColorCol, textColorCol, fontStyleCol)
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
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.DEFAULT, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontStyleManager.currentFontStyle = EditorManager.FontStyleManager.DEFAULT_FONT
                    }
                    R.id.italic_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.ITALIC, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontStyleManager.currentFontStyle = EditorManager.FontStyleManager.ITALIC_FONT
                    }
                    R.id.bold_italic_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.BOLD_ITALIC, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontStyleManager.currentFontStyle = EditorManager.FontStyleManager.BOLD_ITALIC_FONT
                    }
                    R.id.serif_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.SERIF, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontStyleManager.currentFontStyle = EditorManager.FontStyleManager.SERIF_FONT
                    }
                    R.id.sans_serif_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.SANS_SERIF, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontStyleManager.currentFontStyle = EditorManager.FontStyleManager.SANS_SERIF_FONT
                    }
                    R.id.monospace_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.MONOSPACE, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        EditorManager.FontStyleManager.currentFontStyle = EditorManager.FontStyleManager.MONOSPACE_FONT
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
                        EditorManager.BackgroundColorManager.currentBgColor = EditorManager.BackgroundColorManager.RED
                    }
                    R.id.col_blue -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_blue))
                        EditorManager.BackgroundColorManager.currentBgColor = EditorManager.BackgroundColorManager.BLUE
                    }
                    R.id.col_green -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_green))
                        EditorManager.BackgroundColorManager.currentBgColor = EditorManager.BackgroundColorManager.GREEN
                    }
                    R.id.col_yellow -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_yellow))
                        EditorManager.BackgroundColorManager.currentBgColor = EditorManager.BackgroundColorManager.YELLOW
                    }
                    R.id.col_white -> {
                        EditorManager.BackgroundColorManager.changeColor(viewArray, resources.getColor(R.color.material_white))
                        EditorManager.BackgroundColorManager.currentBgColor = EditorManager.BackgroundColorManager.WHITE
                    }
                }
            }
        }).show()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        } else {
            if (enter) {
                return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            } else {
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            }
        }
    }

}


