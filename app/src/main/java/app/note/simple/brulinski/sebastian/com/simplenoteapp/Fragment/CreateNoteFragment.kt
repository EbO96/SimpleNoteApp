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
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.ColorCreator
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments.BottomSheetColorFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments.BottomSheetFontFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FragmentAndObjectStates
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*


open class CreateNoteFragment : Fragment() {

    /**
     * Keys and final fields values
     */
    private val STATUS_BAR_COLOR_KEY = "status_bar_color"
    private val NOTE_OBJECT_SAVE_INSTANCE_KEY = "note_object_save_instance_key"
    private val noteCharactersLimit = 1000
    private val titleCharactersLimit = 50
    /**
     * Others
     */
    lateinit var binding: CreateNoteFragmentBinding
    private var actualLimit = titleCharactersLimit
    private lateinit var colorManager: EditorManager.ColorManager
    private var showToastFlag = true
    /**
     * Layout
     */
    private lateinit var titleView: EditText
    private lateinit var noteView: EditText
    private lateinit var cardView: CardView
    private var actionBar = EditorManager.ColorManager.ACTION_BAR_COLOR
    /**
     * Toasty toast values
     */
    @ColorInt
    private val INFO_COLOR = Color.parseColor("#3F51B5")
    private var infoToastShowedAtStart: Boolean = false
    private val delayBetweenToasts = 1500L

    /**
     * Static fields
     */
    companion object {
        var noteObject: NoteItem? = null //Singe note object

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateAndTime(): String { //Get current time from system
            val calendar = Calendar.getInstance()

            return SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss a").format(calendar.time)
        }

        /**
         * KEY values
         */
        val NOTE_OBJECT_TO_COLOR_CREATOR = "note_object_to_color_creator"
    }

    fun getNoteObject(): NoteItem? {
        return noteObject
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.create_note_fragment, container, false)
        //init color manager
        colorManager = EditorManager.ColorManager(activity)

        titleView = binding.createNoteTitleField
        noteView = binding.createNoteNoteField
        cardView = binding.createNoteParentCard
        actionBar = EditorManager.ColorManager.ACTION_BAR_COLOR

        val noteStyleEditor = EditorManager.ColorManager(activity)

        //Create empty note object
        if (savedInstanceState == null) {
            noteObject = NoteItem(null, "", "", "", ContextCompat.getColor(context, R.color.material_white), ContextCompat.getColor(context, R.color.material_black), EditorManager.FontStyleManager.DEFAULT_FONT, false, false)
        } else {
            noteObject = savedInstanceState.getParcelable(NOTE_OBJECT_SAVE_INSTANCE_KEY)
            noteStyleEditor.changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR), Color.parseColor("#000000"))
        }

        EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(titleView, noteView, cardView, actionBar), arrayListOf(noteObject!!))

        editListener()

        Toasty.Config.getInstance().setInfoColor(INFO_COLOR).apply()

        infoToastShowedAtStart = true

        onTitleAndNoteFieldFocusListener()

        //Listen FloatingActionButton action
        binding.createFab.setOnClickListener {
            val viewPager = (activity as MainActivity).getViewPager()
            val pagerAdapter = (activity as MainActivity).getPagerAdapter()
            val updateChannel = (activity as MainActivity).updateChannel

            val position = viewPager.currentItem

            //viewPager.currentItem = 1
            when (position) {
                0 -> { //Create
                    updateChannel.setupUpdate(context, noteObject)
//                    FragmentAndObjectStates.currentNote = noteObject
//                    ObjectToDatabaseOperations.insertObject(context, noteObject)
//                    pagerAdapter.notifyDataSetChanged()
                }
                3 -> { //Edit
                    updateChannel.setupUpdate(context, prepareAndGetNoteObject(), true)
                    FragmentAndObjectStates.currentNote = prepareAndGetNoteObject()
                    FragmentAndObjectStates.refreshPreview = true
//                    val note = prepareAndGetNoteObject()
//                    FragmentAndObjectStates.currentNote = note
//                    ObjectToDatabaseOperations.updateObject(context, arrayListOf(note))
//                    pagerAdapter.notifyDataSetChanged()
                }
            }
        }
        return binding.root
    }

    /*
    Make object after edit
     */
    fun prepareAndGetNoteObject(): NoteItem {
        return NoteItem(
                FragmentAndObjectStates.currentNote!!.id, binding.createNoteTitleField.text.toString().trim(), binding.createNoteNoteField.text.toString().trim(),
                getCurrentDateAndTime(), ColorCreator.getColorFromCard(activity, binding.createNoteParentCard), ColorCreator.getColorIntFromColorStateList(binding.createNoteTitleField.textColors),
                noteObject!!.fontStyle, false, false
        )
    }

    private fun resetLayout() {//Reset all values to default
        noteObject = FragmentAndObjectStates.getDefaultNote(context)
        EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(titleView, noteView, cardView, actionBar), arrayListOf(noteObject!!))
        titleView.setText("")
        noteView.setText("")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(NOTE_OBJECT_SAVE_INSTANCE_KEY, noteObject)
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

    /**
     * Text operations
     */
    private fun onTitleAndNoteFieldFocusListener() {//Listen for changes in note and title EditText
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

//        binding.fontStyle.setOnClickListener {
////            if (!bottomSheetFonts.isAdded) {
////                bottomSheetFonts.show(activity.supportFragmentManager, bottomSheetFonts.tag)
////            }
//        }

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
                if (checkLength(noteObject!!.title!!.length)) {
                    if (!infoToastShowedAtStart)
                        showLimitCharacterToast() //Show Toast when user type under 1000 characters
                    infoToastShowedAtStart = false
                }
            }
        }

        note.textChangedListener {
            onTextChanged { p0, _, _, _ ->
                if (actualLimit == noteCharactersLimit)
                    incrementCharacterCounter(p0!!.length)
            }

            afterTextChanged { text ->
                noteObject!!.note = text.toString().trim()
                if (checkLength(noteObject!!.note!!.length)) {
                    if (!infoToastShowedAtStart)
                        showLimitCharacterToast() //Show Toast when user type under 1000 characters
                    infoToastShowedAtStart = false
                }
            }
        }
    }

    private fun showLimitCharacterToast() { //To display character limit toast when user type under 1000 characters
        if (showToastFlag)
            showInfoToast("${getString(R.string.max_note_size_toast)} $actualLimit")
        showToastFlag = false

        Handler().postDelayed({
            showToastFlag = true
        }, delayBetweenToasts)
    }

    private fun incrementCharacterCounter(charLength: Int) {
        setCounterText(charLength)
        changeCounterColor(charLength)
    }


    private fun setCounterText(text: Int) {
        binding.charactersCounterTextView.text = "$text/$actualLimit"
    }

    private fun checkLength(length: Int): Boolean {
        return when (length) {
            actualLimit -> {
                true
            }
            else -> false
        }
    }

    private fun showInfoToast(message: String) { //Show info toast
        Toasty.info(activity, message, Toast.LENGTH_SHORT, true).show()
    }

    /**
     * Apply note theme
     */
//    fun changeFontStyle(whichFont: String) { //This is method brought by interface
//        applyFont(whichFont)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    fun changeNoteOrFontColors(colorOfWhat: String, color: Int) {
//        //Change color of font or background (CardView)
//        val viewsArray: ArrayList<Any>
//
//        if (colorOfWhat == (EditorManager.ColorManager.COLOR_OF_TEXT)) {
//            viewsArray = arrayListOf(binding.createNoteTitleField, binding.createNoteNoteField)
//            noteObject!!.TXTColor = color
//        } else {
//            viewsArray = arrayListOf(binding.createNoteParentCard, EditorManager.ColorManager.ACTION_BAR_COLOR)
//            noteObject!!.BGColor = color
//        }
//        EditorManager.ColorManager(activity).applyNoteTheme(viewsArray, arrayListOf(noteObject!!))
//    }

//    private fun applyFont(whichFont: String) { //TODO implement better solution later
//        var fontStyle = ""
//        val editor = EditorManager.FontStyleManager
//
//        when (whichFont) {
//            editor.DEFAULT_FONT -> {
//                EditorManager.FontStyleManager.setUpFontStyle(Typeface.DEFAULT, binding.createNoteNoteField, binding.createNoteTitleField)
//                fontStyle = EditorManager.FontStyleManager.DEFAULT_FONT
//            }
//            editor.ITALIC_FONT -> {
//                EditorManager.FontStyleManager.setUpFontStyle(Typeface.ITALIC, binding.createNoteNoteField, binding.createNoteTitleField)
//                fontStyle = EditorManager.FontStyleManager.ITALIC_FONT
//            }
//            editor.BOLD_ITALIC_FONT -> {
//                EditorManager.FontStyleManager.setUpFontStyle(Typeface.BOLD_ITALIC, binding.createNoteNoteField, binding.createNoteTitleField)
//                fontStyle = EditorManager.FontStyleManager.BOLD_ITALIC_FONT
//            }
//            editor.SERIF_FONT -> {
//                EditorManager.FontStyleManager.setUpFontStyle(Typeface.SERIF, binding.createNoteNoteField, binding.createNoteTitleField)
//                fontStyle = EditorManager.FontStyleManager.SERIF_FONT
//            }
//            editor.SANS_SERIF_FONT -> {
//                EditorManager.FontStyleManager.setUpFontStyle(Typeface.SANS_SERIF, binding.createNoteNoteField, binding.createNoteTitleField)
//                fontStyle = EditorManager.FontStyleManager.SANS_SERIF_FONT
//            }
//            editor.MONOSPACE_FONT -> {
//                EditorManager.FontStyleManager.setUpFontStyle(Typeface.MONOSPACE, binding.createNoteNoteField, binding.createNoteTitleField)
//                fontStyle = EditorManager.FontStyleManager.MONOSPACE_FONT
//            }
//        }
//        noteObject!!.fontStyle = fontStyle
//    }

    private fun changeCounterColor(charLength: Int) {
        //Method change counter color depending on the character characters limit
        if (charLength >= actualLimit)
            binding.charactersCounterTextView.textColor = ContextCompat.getColor(context, R.color.material_red)
        else binding.charactersCounterTextView.textColor = ContextCompat.getColor(context, R.color.material_blue_grey)
    }

}