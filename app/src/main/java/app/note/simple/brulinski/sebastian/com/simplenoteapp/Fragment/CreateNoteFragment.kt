package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments.BottomSheetColorFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.ChangeColorInterface
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
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment(), SaveNoteInterface, ChangeColorInterface {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun changeNoteColors(colorOfWhat: String, color: String) {
        if (colorOfWhat.equals(EditorManager.ColorManager.COLOR_OF_TEXT)) {
            changeColorOf(arrayListOf(binding.createNoteTitleField, binding.createNoteNoteField), colorOfWhat, color)
        } else {
            changeColorOf(arrayListOf(binding.createNoteParentCard), colorOfWhat, color)
        }
    }

    lateinit var binding: CreateNoteFragmentBinding
    private lateinit var database: LocalSQLAnkoDatabase
    private val noteCharactersLimit = 1000
    private val titleCharactersLimit = 50
    private var actualLimit = titleCharactersLimit
    private val STATUS_BAR_COLOR_KEY = "status_bar_color"
    private lateinit var colorManager: EditorManager.ColorManager

    @ColorInt
    private val INFO_COLOR = Color.parseColor("#3F51B5")
    private var infoToastShowedAtStart: Boolean = false

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateAndTime(): String { //Get current time from system
            val calendar = Calendar.getInstance()

            return SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a").format(calendar.time)
        }

        var noteObject: ItemsHolder? = null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)
        //init color manager
        colorManager = EditorManager.ColorManager(context)

        Log.i("save", savedInstanceState.toString())
        database = LocalSQLAnkoDatabase(context)

        val titleView = binding.createNoteTitleField
        val noteView = binding.createNoteNoteField
        val cardView = binding.createNoteParentCard

        if (savedInstanceState == null) {
            noteObject = ItemsHolder("", "", "", "", EditorManager.ColorManager.WHITE,
                    EditorManager.ColorManager.BLACK, EditorManager.FontStyleManager.DEFAULT_FONT, false)
        } else {
            noteObject = savedInstanceState.getParcelable("note_object")
            colorManager.changeStatusBarColor(activity, EditorManager.ColorManager.BLACK, savedInstanceState.getInt(STATUS_BAR_COLOR_KEY))
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

        onTitleAndNoteFieldFocusListener()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        listenBarOptions()
    }

    private fun onTitleAndNoteFieldFocusListener() {
        var textLength: Int

        binding.createNoteTitleField.setOnFocusChangeListener { _, p1 ->
            textLength = binding.createNoteTitleField.text.length

            actualLimit = titleCharactersLimit
            setCounterText(textLength)
            changeCounterColor(textLength)
        }

        binding.createNoteNoteField.setOnFocusChangeListener { _, p1 ->
            textLength = binding.createNoteNoteField.text.length

            actualLimit = noteCharactersLimit
            setCounterText(textLength)
            changeCounterColor(textLength)
        }
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable("note_object", noteObject)
        outState.putInt(STATUS_BAR_COLOR_KEY, activity.window.statusBarColor)
    }

    override fun onSaveNote() {
        try {
            val titleCol = Pair<String, String>(LocalSQLAnkoDatabase.TITLE, binding.createNoteTitleField.text.toString().trim())
            val noteCol = Pair<String, String>(LocalSQLAnkoDatabase.NOTE, binding.createNoteNoteField.text.toString().trim())
            val dateCol = Pair<String, String>(LocalSQLAnkoDatabase.DATE, getCurrentDateAndTime())
            val isDeletedCol = Pair<String, String>(LocalSQLAnkoDatabase.IS_DELETED, false.toString())
            var idList: List<List<Notes.Note>>? = null
            var size = 0

            database.use {
                insert(LocalSQLAnkoDatabase.TABLE_NOTES, titleCol, noteCol, dateCol, isDeletedCol)
                select(LocalSQLAnkoDatabase.TABLE_NOTES)
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
                insert(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES, noteIdCol, bgColorCol, textColorCol, fontStyleCol, isDeletedCol)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun listenBarOptions() {
        val bottomSheet: BottomSheetDialogFragment = BottomSheetColorFragment()

        binding.selectAll.setOnClickListener {
            selectAllText()
        }

        binding.copyAll.setOnClickListener {
            copySelectedText()
        }

        binding.paste.setOnClickListener {
            pasteText()
        }

        binding.fontStyle.setOnClickListener {
            showFontMenu()
        }

        binding.textColor.setOnClickListener {
            val args = Bundle()
            args.putString(EditorManager.ColorManager.COLOR_OF_KEY, EditorManager.ColorManager.COLOR_OF_TEXT)
            bottomSheet.arguments = args
            bottomSheet.show(activity.supportFragmentManager, bottomSheet.tag)

        }

        binding.noteColor.setOnClickListener {
            val args = Bundle()
            args.putString(EditorManager.ColorManager.COLOR_OF_KEY, EditorManager.ColorManager.COLOR_OF_NOTE)
            bottomSheet.arguments = args
            bottomSheet.show(activity.supportFragmentManager, bottomSheet.tag)
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
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.DEFAULT, binding.createNoteNoteField, binding.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.DEFAULT_FONT
                    }
                    R.id.italic_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.ITALIC, binding.createNoteNoteField, binding.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.ITALIC_FONT
                    }
                    R.id.bold_italic_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.BOLD_ITALIC, binding.createNoteNoteField, binding.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.BOLD_ITALIC_FONT
                    }
                    R.id.serif_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.SERIF, binding.createNoteNoteField, binding.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.SERIF_FONT
                    }
                    R.id.sans_serif_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.SANS_SERIF, binding.createNoteNoteField, binding.createNoteTitleField)
                        fontStyle = EditorManager.FontStyleManager.SANS_SERIF_FONT
                    }
                    R.id.monospace_font -> {
                        EditorManager.FontStyleManager.setUpFontStyle(Typeface.MONOSPACE, binding.createNoteNoteField, binding.createNoteTitleField)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeColorOf(viewsArray: ArrayList<Any>, colorOf: String, color: String) { //Change color of background
        var resColor = ContextCompat.getColor(context, R.color.material_white)
        var currentColor = noteObject!!.bgColor


        when (color) {
            EditorManager.ColorManager.RED -> {
                resColor = ContextCompat.getColor(context, R.color.material_red)
                currentColor = EditorManager.ColorManager.RED
            }
            EditorManager.ColorManager.PINK -> {
                resColor = ContextCompat.getColor(context, R.color.material_pink)
                currentColor = EditorManager.ColorManager.PINK
            }
            EditorManager.ColorManager.PURPLE -> {
                resColor = ContextCompat.getColor(context, R.color.material_purple)
                currentColor = EditorManager.ColorManager.PURPLE
            }
            EditorManager.ColorManager.BLUE -> {
                resColor = ContextCompat.getColor(context, R.color.material_blue)
                currentColor = EditorManager.ColorManager.BLUE
            }
            EditorManager.ColorManager.INDIGO -> {
                resColor = ContextCompat.getColor(context, R.color.material_indigo)
                currentColor = EditorManager.ColorManager.INDIGO
            }
            EditorManager.ColorManager.GREEN -> {
                resColor = ContextCompat.getColor(context, R.color.material_green)
                currentColor = EditorManager.ColorManager.GREEN
            }
            EditorManager.ColorManager.TEAL -> {
                resColor = ContextCompat.getColor(context, R.color.material_teal)
                currentColor = EditorManager.ColorManager.TEAL
            }
            EditorManager.ColorManager.YELLOW -> {
                resColor = ContextCompat.getColor(context, R.color.material_yellow)
                currentColor = EditorManager.ColorManager.YELLOW
            }
            EditorManager.ColorManager.WHITE -> {
                resColor = ContextCompat.getColor(context, R.color.material_white)
                currentColor = EditorManager.ColorManager.WHITE
            }
            EditorManager.ColorManager.BLUE_GRAY -> {
                resColor = ContextCompat.getColor(context, R.color.material_blue_grey)
                currentColor = EditorManager.ColorManager.BLUE_GRAY
            }
            EditorManager.ColorManager.BLACK -> {
                resColor = ContextCompat.getColor(context, R.color.material_black)
                currentColor = EditorManager.ColorManager.BLACK
            }
            EditorManager.ColorManager.BROWN -> {
                resColor = ContextCompat.getColor(context, R.color.material_brown)
                currentColor = EditorManager.ColorManager.BROWN
            }
        }


        val frag = activity.supportFragmentManager.findFragmentById(activity.findViewById<FrameLayout>(R.id.main_container).id)
        val editMode = frag != null && frag.isVisible && frag.tag.equals(MainActivity.EDIT_NOTE_FRAGMENT_TAG)

        /*
        Change color
         */
        if (colorOf.equals(EditorManager.ColorManager.COLOR_OF_NOTE)) {
            colorManager.changeStatusBarColor(activity, EditorManager.ColorManager.BLACK, resColor)
            EditorManager.ColorManager.changeBgColor(viewsArray, resColor)
            if (editMode)
                MainActivity.noteToEdit!!.bgColor = currentColor
            else noteObject!!.bgColor = currentColor
        } else if (colorOf.equals(EditorManager.ColorManager.COLOR_OF_TEXT)) {
            EditorManager.ColorManager.changeFontColor(viewsArray, resColor)
            if (editMode)
                MainActivity.noteToEdit!!.textColor = currentColor
            else noteObject!!.textColor = currentColor
        }
    }

    private fun selectAllText() {
        val title = binding.createNoteTitleField
        val note = binding.createNoteNoteField

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
        val title = binding.createNoteTitleField
        val note = binding.createNoteNoteField

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
        val title = binding.createNoteTitleField
        val note = binding.createNoteNoteField

        title.textChangedListener {

            onTextChanged { p0, p1, p2, p3 ->
                if (actualLimit == titleCharactersLimit)
                    incrementCharacterCounter(p0!!.length)
            }
        }

        note.textChangedListener {
            afterTextChanged { text ->
                if (checkNoteLenghth()) {
                    if (!infoToastShowedAtStart)
                        showInfoToast(R.string.max_note_size_toast.toString() + actualLimit)
                    infoToastShowedAtStart = false
                }
            }

            onTextChanged { p0, p1, p2, p3 ->
                if (actualLimit == noteCharactersLimit)
                    incrementCharacterCounter(p0!!.length)
            }
        }
    }

    private fun incrementCharacterCounter(charLength: Int) {
        setCounterText(charLength)
        changeCounterColor(charLength)
    }

    private fun changeCounterColor(charLength: Int) {
        if (charLength >= actualLimit)
            binding.charactersCounterTextView.textColor = ContextCompat.getColor(context, R.color.material_red)
        else binding.charactersCounterTextView.textColor = ContextCompat.getColor(context, R.color.material_blue_grey)
    }

    private fun setCounterText(text: Int) {
        binding.charactersCounterTextView.text = "$text/$actualLimit"
    }

    private fun checkNoteLenghth(): Boolean {
        return binding.createNoteNoteField.text.length == actualLimit
    }

    private fun showInfoToast(message: String) { //Show info toast
        Toasty.info(activity, message, Toast.LENGTH_SHORT, true).show()
    }

    private fun pasteText() {
        val clipboardManager = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        if (clipboardManager.primaryClip != null) {
            val pasteData = clipboardManager.primaryClip.getItemAt(0).text

            val title = binding.createNoteTitleField
            val note = binding.createNoteNoteField

            val startIndex: Int

            if (title.isFocused && clipboardManager.hasPrimaryClip()) {
                startIndex = title.selectionStart
                title.text.insert(startIndex, pasteData)
            } else if (note.isFocused && clipboardManager.hasPrimaryClip()) {
                startIndex = note.selectionStart
                note.text.insert(startIndex, pasteData)
            }
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


