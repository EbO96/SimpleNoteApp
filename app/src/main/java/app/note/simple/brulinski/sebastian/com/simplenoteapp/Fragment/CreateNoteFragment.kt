package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.*
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.UndoRedo
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.SaveNoteInterface
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.Notes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import com.cocosw.bottomsheet.BottomSheet
import com.labo.kaji.fragmentanimations.MoveAnimation
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment(), SaveNoteInterface {

    lateinit var bindingFrag: CreateNoteFragmentBinding
    private lateinit var database: LocalSQLAnkoDatabase
    private lateinit var undoRedo: UndoRedo
    private val maxTextLength: Int = 1000
    @ColorInt
    private val INFO_COLOR = Color.parseColor("#3F51B5")
    private var infoToastShowedAtStart: Boolean = false
    private val showFabAfterTime = 2000L


    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateAndTime(): String { //Get current time from system
            val calendar = Calendar.getInstance()

            return SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a").format(calendar.getTime())
        }

        var noteObject: ItemsHolder? = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingFrag = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)

        database = LocalSQLAnkoDatabase(context)

        val titleView = bindingFrag.createNoteTitleField
        val noteView = bindingFrag.createNoteNoteField
        val cardView = bindingFrag.createNoteParentCard

        if (savedInstanceState == null) {
            noteObject = ItemsHolder("", "", "", "", EditorManager.ColorManager.WHITE,
                    EditorManager.ColorManager.BLACK, EditorManager.FontStyleManager.DEFAULT_FONT)
        } else {
            noteObject = savedInstanceState.getParcelable("note_object")
        }

        val bgColor = noteObject!!.bgColor
        val fontStyle = noteObject!!.fontStyle
        val fontColor = noteObject!!.textColor

        EditorManager.FontStyleManager.recogniseAndSetFont(fontStyle, titleView, noteView)
        val bg = EditorManager.ColorManager(context)

        bg.recogniseAndSetColor(bgColor, arrayListOf(cardView), "BG") //Change note color

        bg.recogniseAndSetColor(fontColor, arrayListOf(titleView, noteView), "FONT") //Change text color

        editListener()

        Toasty.Config.getInstance().setInfoColor(INFO_COLOR).apply()

        infoToastShowedAtStart = true
        return bindingFrag.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
        undoRedo = UndoRedo(bindingFrag) //Setup UndoRedo class to handle operations at undo and redo actions

        listenBarOptions()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val frag = activity.supportFragmentManager.findFragmentById(activity.findViewById<FrameLayout>(R.id.main_container).id)
        if (frag is EditNoteFragment && frag.tag.equals(MainActivity.EDIT_NOTE_FRAGMENT_TAG)) {
            (activity as MainActivity).setTitleAndFab(ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp),
                    resources.getString(R.string.edit))
        } else {
            (activity as MainActivity).setTitleAndFab(ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp),
                    resources.getString(R.string.create))
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable("note_object", noteObject)
    }

    override fun onSaveNote() {
        try {
            val titleCol = Pair<String, String>(LocalSQLAnkoDatabase.TITLE, bindingFrag.createNoteTitleField.text.toString().trim())
            val noteCol = Pair<String, String>(LocalSQLAnkoDatabase.NOTE, bindingFrag.createNoteNoteField.text.toString().trim())
            val dateCol = Pair<String, String>(LocalSQLAnkoDatabase.DATE, getCurrentDateAndTime())
            var idList: List<List<Notes.Note>>? = null
            var size = 0

            database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES, titleCol, noteCol, dateCol)
            }

            database.use {
                idList = select(LocalSQLAnkoDatabase.TABLE_NOTES).whereArgs(LocalSQLAnkoDatabase.ID, titleCol, noteCol).
                        parseList(MyRowParserNotes())
                size = idList!!.size
            }

            val noteIdCol = Pair<String, String>(LocalSQLAnkoDatabase.NOTE_ID, idList!!.get(size - 1).get(size - 1).id!!)
            val bgColorCol = Pair<String, String>(LocalSQLAnkoDatabase.BG_COLOR, noteObject!!.bgColor)
            val textColorCol = Pair<String, String>(LocalSQLAnkoDatabase.TEXT_COLOR, noteObject!!.textColor)
            val fontStyleCol = Pair<String, String>(LocalSQLAnkoDatabase.FONT_STYLE, noteObject!!.fontStyle)

            database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, noteIdCol, bgColorCol, textColorCol, fontStyleCol)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            undoRedo.block(true)
            undoRedo.getUndo()
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
    private fun showFontMenu() {
        var fontStyle = ""

        BottomSheet.Builder(activity).title(getString(R.string.fonts)).sheet(R.menu.font_menu).listener(object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    R.id.default_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.DEFAULT, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.DEFAULT_FONT
                    }
                    R.id.italic_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.ITALIC, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.ITALIC_FONT
                    }
                    R.id.bold_italic_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.BOLD_ITALIC, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.BOLD_ITALIC_FONT
                    }
                    R.id.serif_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.SERIF, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.SERIF_FONT
                    }
                    R.id.sans_serif_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.SANS_SERIF, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.SANS_SERIF_FONT
                    }
                    R.id.monospace_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.MONOSPACE, bindingFrag.createNoteNoteField, bindingFrag.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.MONOSPACE_FONT
                    }
                }
                val frag = activity.supportFragmentManager.findFragmentById(activity.findViewById<FrameLayout>(R.id.main_container).id)
                val editMode = frag != null && frag.isVisible && frag.tag.equals(MainActivity.EDIT_NOTE_FRAGMENT_TAG)

                if (editMode)
                    MainActivity.noteToEdit!!.fontStyle = fontStyle
                else noteObject!!.fontStyle = fontStyle
            }
        }).show()
    }

    private fun changeColorOf(viewsArray: ArrayList<Any>, sheetTitle: String, colorOf: String) { //Change color of background
        var resColor = ContextCompat.getColor(context, R.color.material_white)
        var currentColor = noteObject!!.bgColor

        BottomSheet.Builder(activity).title(sheetTitle).sheet(R.menu.color_menu).listener(object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    R.id.col_red -> {
                        resColor = ContextCompat.getColor(context, R.color.material_red)
                        currentColor = EditorManager.ColorManager.RED
                    }
                    R.id.col_pink -> {
                        resColor = ContextCompat.getColor(context, R.color.material_pink)
                        currentColor = EditorManager.ColorManager.PINK
                    }
                    R.id.col_purple -> {
                        resColor = ContextCompat.getColor(context, R.color.material_purple)
                        currentColor = EditorManager.ColorManager.PURPLE
                    }
                    R.id.col_blue -> {
                        resColor = ContextCompat.getColor(context, R.color.material_blue)
                        currentColor = EditorManager.ColorManager.BLUE
                    }
                    R.id.col_indigo -> {
                        resColor = ContextCompat.getColor(context, R.color.material_indigo)
                        currentColor = EditorManager.ColorManager.INDIGO
                    }
                    R.id.col_green -> {
                        resColor = ContextCompat.getColor(context, R.color.material_green)
                        currentColor = EditorManager.ColorManager.GREEN
                    }
                    R.id.col_teal -> {
                        resColor = ContextCompat.getColor(context, R.color.material_teal)
                        currentColor = EditorManager.ColorManager.TEAL
                    }
                    R.id.col_yellow -> {
                        resColor = ContextCompat.getColor(context, R.color.material_yellow)
                        currentColor = EditorManager.ColorManager.YELLOW
                    }
                    R.id.col_white -> {
                        resColor = ContextCompat.getColor(context, R.color.material_white)
                        currentColor = EditorManager.ColorManager.WHITE
                    }
                    R.id.col_blue_grey -> {
                        resColor = ContextCompat.getColor(context, R.color.material_blue_grey)
                        currentColor = EditorManager.ColorManager.BLUE_GRAY
                    }
                    R.id.col_black -> {
                        resColor = ContextCompat.getColor(context, R.color.material_black)
                        currentColor = EditorManager.ColorManager.BLACK
                    }
                    R.id.col_brown -> {
                        resColor = ContextCompat.getColor(context, R.color.material_brown)
                        currentColor = EditorManager.ColorManager.BROWN
                    }
                }

                val frag = activity.supportFragmentManager.findFragmentById(activity.findViewById<FrameLayout>(R.id.main_container).id)
                val editMode = frag != null && frag.isVisible && frag.tag.equals(MainActivity.EDIT_NOTE_FRAGMENT_TAG)

                /*
                Change color
                 */
                if (colorOf.equals("BG")) {
                    EditorManager.ColorManager.changeBgColor(viewsArray, resColor)
                    if (editMode)
                        MainActivity.noteToEdit!!.bgColor = currentColor
                    else noteObject!!.bgColor = currentColor
                } else if (colorOf.equals("FONT")) {
                    EditorManager.ColorManager.changeFontColor(viewsArray, resColor)
                    if (editMode)
                        MainActivity.noteToEdit!!.textColor = currentColor
                    else noteObject!!.textColor = currentColor
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
            Toasty.info(context, getString(R.string.copied), Toast.LENGTH_SHORT, false).show()
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

            afterTextChanged { text ->
                if (checkNoteLenghth()) {
                    if (!infoToastShowedAtStart)
                        showInfoToast(R.string.max_note_size_toast.toString() + maxTextLength)
                    infoToastShowedAtStart = false
                } else undoRedo.addUndo(text = text.toString())
            }

            onTextChanged{p0, p1, p2, p3 ->
                if(bindingFrag.createNoteNoteField.isFocused){
                (activity as MainActivity).binding.mainFab.hide()
                Handler().postDelayed({
                    (activity as MainActivity).binding.mainFab.show()
                }, showFabAfterTime)
            }}
        }
    }

    private fun checkNoteLenghth(): Boolean {
        return bindingFrag.createNoteNoteField.text.length == maxTextLength //1000 characters
    }

    private fun showInfoToast(message: String) { //Show info toast
        Toasty.info(activity, getString(R.string.max_note_size_toast) + maxTextLength, Toast.LENGTH_SHORT, true).show()
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.findItem(R.id.main_menu_grid).isVisible = false
        menu.findItem(R.id.main_menu_linear).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }
}


