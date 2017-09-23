package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.UndoRedo
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import com.cocosw.bottomsheet.BottomSheet
import com.labo.kaji.fragmentanimations.MoveAnimation
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment() {

    lateinit var bindingFrag: CreateNoteFragmentBinding
    lateinit var database: LocalSQLAnkoDatabase
    lateinit var undoRedo: UndoRedo

    /*
    Fonts tags
     */

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingFrag = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)
        CurrentFragmentState.CURRENT = MainActivity.CREATE_NOTE_FRAGMENT_TAG

        database = LocalSQLAnkoDatabase(context)
        var bgColor = ""
        var fontStyle = ""
        var fontColor = ""

        if (savedInstanceState != null) {
            bgColor = savedInstanceState.getString("bg_color")
            fontStyle = savedInstanceState.getString("font_style")
            fontColor = savedInstanceState.getString("text_color")
        } else {
            bgColor = EditorManager.ColorManager.currentBgColor
            fontStyle = EditorManager.FontStyleManager.currentFontStyle
            fontColor = EditorManager.ColorManager.currentFontColor
        }

        EditorManager.FontStyleManager.recogniseAndSetFont(fontStyle, bindingFrag.createNoteTitleField, bindingFrag.createNoteNoteField)
        val bg = EditorManager.ColorManager(context)

        bg.recogniseAndSetColor(bgColor, arrayListOf(bindingFrag.createNoteParentCard), "BG") //Change note color

        bg.recogniseAndSetColor(fontColor, arrayListOf(bindingFrag.createNoteTitleField, bindingFrag.createNoteNoteField), "FONT") //Change text color
        editListener()
        return bindingFrag.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).refreshActivity(MainActivity.CREATE_NOTE_FRAGMENT_TAG)
        undoRedo = UndoRedo(bindingFrag) //Setup UndoRedo class to handle operations at undo and redo actions

        listenBarOptions()

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("bg_color", EditorManager.ColorManager.currentBgColor)
        outState.putString("font_style", EditorManager.FontStyleManager.currentFontStyle)
        outState.putString("text_color", EditorManager.ColorManager.currentFontColor)
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
            val bgColorCol = Pair<String, String>("bg_color", EditorManager.ColorManager.currentBgColor)
            val textColorCol = Pair<String, String>("text_color", EditorManager.ColorManager.currentFontColor)
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
            selectAllText()
        }

        bindingFrag.copyAll.setOnClickListener {
            copySelectedText()
        }

        bindingFrag.paste.setOnClickListener {
            pasteText()
        }

        bindingFrag.undo.setOnClickListener {
            undoRedo.block()
            undoRedo.getUndo()
        }

        bindingFrag.deleteAll.setOnClickListener {
            deleteAll()
        }

        bindingFrag.fontStyle.setOnClickListener {
            showFontMenu()
        }

        bindingFrag.textColor.setOnClickListener {
            changeColorOf(arrayListOf(bindingFrag.createNoteTitleField, bindingFrag.createNoteNoteField), getString(R.string.font_color), "FONT")

        }

        bindingFrag.noteColor.setOnClickListener {
            changeColorOf(arrayListOf(bindingFrag.createNoteParentCard), getString(R.string.note_color), "BG")
        }

    }

    /*
    Options bar
     */

    private fun deleteAll() {
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

    private fun changeColorOf(viewsArray: ArrayList<Any>, sheetTitle: String, colorOf: String) { //Change color of background
        var resColor = resources.getColor(R.color.material_white)
        var currentColor = EditorManager.ColorManager.currentBgColor

        BottomSheet.Builder(activity).title(sheetTitle).sheet(R.menu.color_menu).listener(object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    R.id.col_red -> {
                        resColor = resources.getColor(R.color.material_red)
                        currentColor = EditorManager.ColorManager.RED
                    }
                    R.id.col_pink -> {
                        resColor = resources.getColor(R.color.material_pink)
                        currentColor = EditorManager.ColorManager.PINK
                    }
                    R.id.col_purple -> {
                        resColor = resources.getColor(R.color.material_purple)
                        currentColor = EditorManager.ColorManager.PURPLE
                    }
                    R.id.col_blue -> {
                        resColor = resources.getColor(R.color.material_blue)
                        currentColor = EditorManager.ColorManager.BLUE
                    }
                    R.id.col_indigo -> {
                        resColor = resources.getColor(R.color.material_indigo)
                        currentColor = EditorManager.ColorManager.INDIGO
                    }
                    R.id.col_green -> {
                        resColor = resources.getColor(R.color.material_green)
                        currentColor = EditorManager.ColorManager.GREEN
                    }
                    R.id.col_teal -> {
                        resColor = resources.getColor(R.color.material_teal)
                        currentColor = EditorManager.ColorManager.TEAL
                    }
                    R.id.col_yellow -> {
                        resColor = resources.getColor(R.color.material_yellow)
                        currentColor = EditorManager.ColorManager.YELLOW
                    }
                    R.id.col_white -> {
                        resColor = resources.getColor(R.color.material_white)
                        currentColor = EditorManager.ColorManager.WHITE
                    }
                    R.id.col_blue_grey -> {
                        resColor = resources.getColor(R.color.material_blue_grey)
                        currentColor = EditorManager.ColorManager.BLUE_GRAY
                    }
                    R.id.col_black -> {
                        resColor = resources.getColor(R.color.material_black)
                        currentColor = EditorManager.ColorManager.BLACK
                    }
                    R.id.col_brown -> {
                        resColor = resources.getColor(R.color.material_brown)
                        currentColor = EditorManager.ColorManager.BROWN
                    }
                }
                /*
                Change color
                 */
                if (colorOf.equals("BG")) {
                    EditorManager.ColorManager.changeBgColor(viewsArray, resColor)
                    EditorManager.ColorManager.currentBgColor = currentColor
                } else if (colorOf.equals("FONT")) {
                    EditorManager.ColorManager.changeFontColor(viewsArray, resColor)
                    EditorManager.ColorManager.currentFontColor = currentColor
                }
            }
        }).show()
    }

    private fun selectAllText() {
        val title = bindingFrag.createNoteTitleField
        val note = bindingFrag.createNoteNoteField

        if (title.isFocused && !TextUtils.isEmpty(title.text)) {
            title.clearFocus()
            title.requestFocus()
            title.setSelection(0, title.length())
        } else if (note.isFocused && !TextUtils.isEmpty(note.text)) {
            note.clearFocus()
            note.requestFocus()
            note.setSelection(0, note.length())
        }
    }

    private fun copySelectedText() {
        val title = bindingFrag.createNoteTitleField
        val note = bindingFrag.createNoteNoteField

        val startIndex: Int
        val endIndex: Int

        var text = ""

        if (title.isFocused && !TextUtils.isEmpty(title.text) && (title.selectionStart - title.selectionEnd) < 0) {
            startIndex = title.selectionStart
            endIndex = title.selectionEnd

            text = title.text.substring(startIndex, endIndex)
        } else if (note.isFocused && !TextUtils.isEmpty(note.text) && (note.selectionStart - note.selectionEnd) < 0) {
            startIndex = note.selectionStart
            endIndex = note.selectionEnd

            text = note.text.substring(startIndex, endIndex)
        }

        if (!text.equals("")) {
            val clipboardManager = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            val clip = ClipData.newPlainText("text", text)
            clipboardManager.primaryClip = clip
            Toast.makeText(context, getString(R.string.copied), Toast.LENGTH_SHORT).show()
        }
    }

    /*
    Listen for changes in title and note editText's
     */
    private fun editListener() {
        val title = bindingFrag.createNoteTitleField
        val note = bindingFrag.createNoteNoteField

        title.textChangedListener {
            afterTextChanged { text -> undoRedo.addUndo(text = text.toString()) }
        }

        note.textChangedListener {
            afterTextChanged { text -> undoRedo.addUndo(text = text.toString()) }
        }
    }

    private fun pasteText() {
        val clipboardManager = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        val pasteData = clipboardManager.primaryClip.getItemAt(0).text

        val title = bindingFrag.createNoteTitleField
        val note = bindingFrag.createNoteNoteField

        val startIndex: Int

        if (title.isFocused && clipboardManager.hasPrimaryClip()) {
            startIndex = title.selectionStart
            title.text.insert(startIndex, pasteData)
        } else if (note.isFocused && clipboardManager.hasPrimaryClip()) {
            startIndex = note.selectionStart
            note.text.insert(startIndex, pasteData)
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)

        } else {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        }
    }

}


