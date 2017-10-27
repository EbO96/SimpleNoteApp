package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding
import com.labo.kaji.fragmentanimations.MoveAnimation

class NotePreviewFragment : Fragment() {

    lateinit var binding: PreviewCardBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setTitleAndFab(ContextCompat.getDrawable(context, R.drawable.ic_mode_edit_white_24dp),
                resources.getString(R.string.preview))

        val noteObj: NoteItem = arguments.getParcelable(MainActivity.NOTE_TO_EDIT_EXTRA_KEY)

        val titleView = binding.previewTitleField
        val noteView = binding.previewNoteField
        val cardView = binding.previewCardParentCard

        val title = noteObj.title
        val note = noteObj.note

        EditorManager.ColorManager(activity).applyNoteTheme(arrayListOf(titleView, noteView, cardView, EditorManager.ColorManager.ACTION_BAR_COLOR), arrayListOf(noteObj))

        titleView.text = title
        noteView.text = note
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        return if (CurrentFragmentState.backPressed) {
            MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)

        } else {
            MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        }
    }
}