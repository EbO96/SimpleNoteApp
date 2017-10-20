package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding
import com.labo.kaji.fragmentanimations.MoveAnimation

class NotePreviewFragment : Fragment() {

    lateinit var database: LocalSQLAnkoDatabase
    lateinit var binding: PreviewCardBinding
    var noteObj: ItemsHolder? = null
    lateinit var colorManager: EditorManager.ColorManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)

        return binding.root
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)

        } else {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).setTitleAndFab(ContextCompat.getDrawable(context, R.drawable.ic_mode_edit_white_24dp),
                resources.getString(R.string.preview))

        database = LocalSQLAnkoDatabase(context)
        noteObj = MainActivity.noteToEdit

        val titleView = binding.previewTitleField
        val noteView = binding.previewNoteField
        val cardView = binding.previewCardParentCard

        val title = noteObj!!.title
        val note = noteObj!!.note

        val fontStyle = noteObj!!.fontStyle
        val textColor = noteObj!!.textColor
        val bgColor = noteObj!!.bgColor

        EditorManager.FontStyleManager.recogniseAndSetFont(fontStyle, titleView, noteView)

        colorManager = EditorManager.ColorManager(context)
        colorManager.changeStatusBarColor(activity, bgColor, null)

        colorManager.recogniseAndSetColor(textColor, arrayListOf(titleView, noteView), "FONT") //set font color
        colorManager.recogniseAndSetColor(bgColor, arrayListOf(cardView), "BG") //set background color

        titleView.text = title
        noteView.text = note
    }

    override fun onDestroyView() {
        MainActivity.noteToEdit
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.findItem(R.id.main_menu_grid).isVisible = false
        menu.findItem(R.id.main_menu_linear).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDestroy() {
        colorManager.changeStatusBarColor(activity, EditorManager.ColorManager.BLACK, null)
        super.onDestroy()
    }
}