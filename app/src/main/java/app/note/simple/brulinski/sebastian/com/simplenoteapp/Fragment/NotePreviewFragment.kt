package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

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
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.PreviewCardBinding
import com.labo.kaji.fragmentanimations.MoveAnimation

class NotePreviewFragment : Fragment() {


    var itemPosition = 0
    var itemId = ""
    var noteObject = ArrayList<ItemsHolder>()
    lateinit var database: LocalSQLAnkoDatabase

    lateinit var binding: PreviewCardBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("fragAnim", "Prev Create")
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_card, container, false)

        database = LocalSQLAnkoDatabase(context)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).refreshActivity(MainActivity.NOTE_PREVIEW_FRAGMENT_TAG)

        itemId = arguments.getString("id")
        val title = arguments.getString("title")
        val note = arguments.getString("note")
        itemPosition = arguments.getInt("position")

        noteObject = arguments.getParcelableArrayList<ItemsHolder>("note_object")

        EditorManager.FontStyleManager.recogniseAndSetFont(noteObject[0].fontStyle, binding.previewTitleField, binding.previewNoteField)
        val bg = EditorManager.ColorManager(context)

        bg.recogniseAndSetColor(noteObject[0].bgColor, arrayListOf(binding.previewCardParentCard), "BG") //Change note color
        bg.recogniseAndSetColor(noteObject[0].textColor, arrayListOf(binding.previewTitleField, binding.previewNoteField), "FONT") //Change text color
        binding.previewTitleField.text = title
        binding.previewNoteField.text = note

    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        if (CurrentFragmentState.backPressed) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)

        } else {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        }
    }

}