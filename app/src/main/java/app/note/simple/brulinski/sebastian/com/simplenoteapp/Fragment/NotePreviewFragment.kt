package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding
import com.labo.kaji.fragmentanimations.MoveAnimation

class NotePreviewFragment : Fragment() {

    var itemPosition = 0
    var itemId = ""
    lateinit var database: LocalSQLAnkoDatabase

    lateinit var binding: PreviewCardBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("frag", "Prev Create")
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)

        (activity as MainActivity).supportActionBar?.setTitle(getString(R.string.preview))
        database = LocalSQLAnkoDatabase(context)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        itemId = arguments.getString("id")
        val title = arguments.getString("title")
        val note = arguments.getString("note")
        itemPosition = arguments.getInt("position")

        binding.previewTitleField.text = title
        binding.previewNoteField.text = note

    }

    override fun onAttach(context: Context?) {
        try {
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must impelent OnPreviewAnimationListener")
        }
        super.onAttach(context)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed) {
            if (CurrentFragmentState.PREVIOUS.equals(MainActivity.EDIT_NOTE_FRAGMENT_TAG)) {
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            } else {
                if (enter) {
                    return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
                } else {
                    return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
                }
            }
        } else {
            if (enter) {
                return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            } else {
                return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
            }
        }
    }

}