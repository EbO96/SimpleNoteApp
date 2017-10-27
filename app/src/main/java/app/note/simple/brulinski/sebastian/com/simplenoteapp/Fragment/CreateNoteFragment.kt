package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.ObjectToDatabaseOperations
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.ColorCreator
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments.BottomSheetColorFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments.BottomSheetFontFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.ChangeNoteLookInterface
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.SaveNoteInterface
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import com.labo.kaji.fragmentanimations.MoveAnimation
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment(), SaveNoteInterface, ChangeNoteLookInterface {

    override fun changeFontStyle(whichFont: String) {
        applyFont(whichFont)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun changeNoteOrFontColors(colorOfWhat: String, color: Int) {
        if (colorOfWhat == (EditorManager.ColorManager.COLOR_OF_TEXT)) {
            applyColor(arrayListOf(binding.createNoteTitleField, binding.createNoteNoteField), colorOfWhat, color)
        } else {
            applyColor(arrayListOf(binding.createNoteParentCard), colorOfWhat, color)
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

        var noteObject: NoteItem? = null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)
        //init color manager
        colorManager = EditorManager.ColorManager(activity)

        Log.i("save", savedInstanceState.toString())
        database = LocalSQLAnkoDatabase(context)

        val titleView = binding.createNoteTitleField
        val noteView = binding.createNoteNoteField
        val cardView = binding.createNoteParentCard
        val actionBar = EditorManager.ColorManager.ACTION_BAR_COLOR

        val noteStyleEditor = EditorManager.ColorManager(activity)

        //Create empty note object
        if (savedInstanceState == null) {
            noteObject = NoteItem(null, "", "", "", 0, 0, EditorManager.FontStyleManager.DEFAULT_FONT, false, false)
        } else {
            noteObject = savedInstanceState.getParcelable("note_object")
            noteStyleEditor.changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR), Color.parseColor("#000000"))
        }

        //TODO change card color's
        noteStyleEditor.changeColor(arrayListOf(cardView, actionBar), ColorCreator(255, 255, 255, activity).getColor())
        noteStyleEditor.changeColor(arrayListOf(titleView, noteView), ColorCreator(0, 0, 0, activity).getColor())

        editListener()

        Toasty.Config.getInstance().setInfoColor(INFO_COLOR).apply()

        infoToastShowedAtStart = true

        onTitleAndNoteFieldFocusListener()

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable("note_object", noteObject)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outState.putInt(STATUS_BAR_COLOR_KEY, activity.window.statusBarColor)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        listenBarOptions()
    }

    private fun onTitleAndNoteFieldFocusListener() {
        var textLength: Int

        binding.createNoteTitleField.setOnFocusChangeListener { _, _ ->
            textLength = binding.createNoteTitleField.text.length

            actualLimit = titleCharactersLimit
            setCounterText(textLength)
            changeCounterColor(textLength)
        }

        binding.createNoteNoteField.setOnFocusChangeListener { _, _ ->
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

    override fun onSaveNote() {
        //TODO new implementation of code below

        noteObject!!.date = getCurrentDateAndTime()

        ObjectToDatabaseOperations.insertObject(context, noteObject)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun listenBarOptions() {
        val bottomSheetColors: BottomSheetDialogFragment = BottomSheetColorFragment()
        val bottomSheetFonts: BottomSheetDialogFragment = BottomSheetFontFragment()

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
            if (!bottomSheetFonts.isAdded) {
                bottomSheetFonts.show(activity.supportFragmentManager, bottomSheetFonts.tag)
            }
        }

        binding.textColor.setOnClickListener {
            val args = Bundle()
            args.putString(EditorManager.ColorManager.COLOR_OF_KEY, EditorManager.ColorManager.COLOR_OF_TEXT)
            bottomSheetColors.arguments = args
            if (!bottomSheetColors.isAdded) {
                bottomSheetColors.show(activity.supportFragmentManager, bottomSheetColors.tag)
            }

        }

        binding.noteColor.setOnClickListener {
            val args = Bundle()
            args.putString(EditorManager.ColorManager.COLOR_OF_KEY, EditorManager.ColorManager.COLOR_OF_NOTE)
            bottomSheetColors.arguments = args
            if (!bottomSheetColors.isAdded) {
                bottomSheetColors.show(activity.supportFragmentManager, bottomSheetColors.tag)
            }
        }

    }


    /*
    Options bar
     */
    private fun applyFont(whichFont: String) {
        var fontStyle = ""
        val editor = EditorManager.FontStyleManager

        when (whichFont) {
            editor.DEFAULT_FONT -> {
                EditorManager.FontStyleManager.setUpFontStyle(Typeface.DEFAULT, binding.createNoteNoteField, binding.createNoteTitleField)
                fontStyle = EditorManager.FontStyleManager.DEFAULT_FONT
            }
            editor.ITALIC_FONT -> {
                EditorManager.FontStyleManager.setUpFontStyle(Typeface.ITALIC, binding.createNoteNoteField, binding.createNoteTitleField)
                fontStyle = EditorManager.FontStyleManager.ITALIC_FONT
            }
            editor.BOLD_ITALIC_FONT -> {
                EditorManager.FontStyleManager.setUpFontStyle(Typeface.BOLD_ITALIC, binding.createNoteNoteField, binding.createNoteTitleField)
                fontStyle = EditorManager.FontStyleManager.BOLD_ITALIC_FONT
            }
            editor.SERIF_FONT -> {
                EditorManager.FontStyleManager.setUpFontStyle(Typeface.SERIF, binding.createNoteNoteField, binding.createNoteTitleField)
                fontStyle = EditorManager.FontStyleManager.SERIF_FONT
            }
            editor.SANS_SERIF_FONT -> {
                EditorManager.FontStyleManager.setUpFontStyle(Typeface.SANS_SERIF, binding.createNoteNoteField, binding.createNoteTitleField)
                fontStyle = EditorManager.FontStyleManager.SANS_SERIF_FONT
            }
            editor.MONOSPACE_FONT -> {
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun applyColor(viewsArray: ArrayList<Any>, colorOf: String, color: Int) { //Change color of background


        val frag = activity.supportFragmentManager.findFragmentById(activity.findViewById<FrameLayout>(R.id.main_container).id)
        val editMode = frag != null && frag.isVisible && frag.tag.equals(MainActivity.EDIT_NOTE_FRAGMENT_TAG)
//
        /*
        Change color
         */
        if (colorOf == (EditorManager.ColorManager.COLOR_OF_NOTE)) {
            val colorMng = EditorManager.ColorManager(activity)
            colorMng.changeColor(viewsArray, color)
            colorMng.changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR), color)
            //TODO  if (editMode)
            //TODO  MainActivity.noteToEdit!!.bgColor = currentColor
            //TODO else noteObject!!.bgColor = currentColor
            noteObject!!.BGColor = color
        } else if (colorOf == (EditorManager.ColorManager.COLOR_OF_TEXT)) {
            EditorManager.ColorManager(activity).changeColor(viewsArray, color)
            //TODO if (editMode)
            //TODO  MainActivity.noteToEdit!!.textColor = currentColor
            //TODO else noteObject!!.textColor = currentColor
            noteObject!!.TXTColor = color
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

            onTextChanged { p0, _, _, _ ->
                if (actualLimit == titleCharactersLimit)
                    incrementCharacterCounter(p0!!.length)
            }

            afterTextChanged { text ->
                noteObject!!.title = text.toString().trim()
            }
        }

        note.textChangedListener {
            afterTextChanged { text ->
                if (checkNoteLenghth()) {
                    if (!infoToastShowedAtStart)
                        showInfoToast(R.string.max_note_size_toast.toString() + actualLimit)
                    infoToastShowedAtStart = false
                }
                noteObject!!.note = text.toString().trim()
            }

            onTextChanged { p0, _, _, _ ->
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


