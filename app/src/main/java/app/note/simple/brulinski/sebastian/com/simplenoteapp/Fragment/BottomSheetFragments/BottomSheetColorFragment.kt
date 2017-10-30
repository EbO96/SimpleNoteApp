package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
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
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnNotePropertiesClickListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ColorsLayoutBinding


class BottomSheetColorFragment : BottomSheetDialogFragment() {
    private lateinit var binding: ColorsLayoutBinding
    private lateinit var mNotePropertiesClickListener: OnNotePropertiesClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.colors_layout, container, false)

        val TAG = arguments.getString(EditorManager.ColorManager.COLOR_OF_KEY)

        if (TAG.equals(EditorManager.ColorManager.COLOR_OF_NOTE))
            binding.textView.text = getString(R.string.note_color)
        else binding.textView.text = getString(R.string.font_color)

        val col = ColorCreator

        binding.colorRedImage.setOnClickListener {
            dismiss()
        }
        binding.colorPinkImage.setOnClickListener {
            dismiss()
        }
        binding.colorPurpleImage.setOnClickListener {
            dismiss()
        }
        binding.colorBlueImage.setOnClickListener {
            dismiss()
        }
        binding.colorIndigoImage.setOnClickListener {
            dismiss()
        }
        binding.colorGreenImage.setOnClickListener {
            dismiss()
        }
        binding.colorTealImage.setOnClickListener {
            dismiss()
        }
        binding.colorYellowImage.setOnClickListener {
            dismiss()
        }
        binding.colorWhiteImage.setOnClickListener {
            dismiss()
        }
        binding.colorBlueGreyImage.setOnClickListener {
            dismiss()
        }
        binding.colorBlackImage.setOnClickListener {
            dismiss()
        }
        binding.colorBrownImage.setOnClickListener {
            dismiss()
        }

        binding.colorPickerImage.setOnClickListener {
            dismiss()
        }

        binding.customColorCard.setOnClickListener {
            dismiss()
        }

        val color = ColorCreator.getColorFromSharedPreferences(activity)
        binding.customColorCard.cardBackgroundColor = ColorStateList.valueOf(color)

        return binding.root
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