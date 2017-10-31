package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.ColorCreator
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.DialogFragment.OwnColorCreatorDialogFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ColorsLayoutBinding


class BottomSheetColorFragment : BottomSheetDialogFragment() {
    /**
     * Tags and Key's
     */
    companion object {
        val OWN_COLOR_PICKER_TAG = "own_color_picker"
    }

    /**
     * Other's
     */
    private lateinit var binding: ColorsLayoutBinding
    private lateinit var noteBackup: NoteItem
    private var currentFragmentPosition: Int = 0
    private var TAG: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = if (savedInstanceState == null) {
            arguments.getString(EditorManager.ColorManager.COLOR_OF_KEY)
        } else {
            savedInstanceState.getString(EditorManager.ColorManager.COLOR_OF_KEY)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString(EditorManager.ColorManager.COLOR_OF_KEY, TAG)
        super.onSaveInstanceState(outState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.colors_layout, container, false)

        if (TAG == EditorManager.ColorManager.COLOR_OF_NOTE)
            binding.textView.text = getString(R.string.note_color)
        else binding.textView.text = getString(R.string.font_color)

        val main = (activity as MainActivity)

        binding.colorRedImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorRedImage))
            dismiss()
        }
        binding.colorPinkImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorPinkImage))
            dismiss()
        }
        binding.colorPurpleImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorPurpleImage))
            dismiss()
        }
        binding.colorBlueImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorBlueImage))
            dismiss()
        }
        binding.colorIndigoImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorIndigoImage))
            dismiss()
        }
        binding.colorGreenImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorGreenImage))
            dismiss()
        }
        binding.colorTealImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorTealImage))
            dismiss()
        }
        binding.colorYellowImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorYellowImage))
            dismiss()
        }
        binding.colorWhiteImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorWhiteImage))
            dismiss()
        }
        binding.colorBlueGreyImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorBlueGreyImage))
            dismiss()
        }
        binding.colorBlackImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorBlackImage))
            dismiss()
        }
        binding.colorBrownImage.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.colorBrownImage))
            dismiss()
        }

        binding.colorPickerImage.setOnClickListener {
            dismiss()
            showOwnColorPicker()
        }
        binding.customColorCard.setOnClickListener {
            main.changeNoteColors(TAG, ColorCreator.getColorFromCard(activity, binding.customColorCard))
            dismiss()
        }

        val color = ColorCreator.getColorFromSharedPreferences(activity)
        binding.customColorCard.cardBackgroundColor = ColorStateList.valueOf(color)

        return binding.root
    }

    private fun showOwnColorPicker() {
        val fm = activity.supportFragmentManager
        val ownColorPicker = OwnColorCreatorDialogFragment()
        val args = Bundle()
        args.putString(EditorManager.ColorManager.COLOR_OF_KEY, TAG)
        ownColorPicker.arguments = args
        ownColorPicker.show(fm, OWN_COLOR_PICKER_TAG)
    }

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN)
                dismiss()
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.colors_layout, null)
        dialog!!.setContentView(contentView)
        val layoutParams: CoordinatorLayout.LayoutParams = ((contentView.getParent()) as View).layoutParams as (CoordinatorLayout.LayoutParams)
        val behavior = (layoutParams.behavior as CoordinatorLayout.Behavior)

        if (behavior is BottomSheetBehavior) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
    }
}