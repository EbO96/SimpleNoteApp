package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnNotePropertiesClickListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ColorsLayoutBinding


/**
 * Created by sebas on 10/23/2017.
 */
class BottomSheetColorFragment : BottomSheetDialogFragment() {
    private lateinit var binding: ColorsLayoutBinding
    private lateinit var mNotePropertiesClickListener: OnNotePropertiesClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.colors_layout, container, false)

        val TAG = arguments.getString(EditorManager.ColorManager.COLOR_OF_KEY)

        if (TAG.equals(EditorManager.ColorManager.COLOR_OF_NOTE))
            binding.textView.text = getString(R.string.note_color)
        else binding.textView.text = getString(R.string.font_color)

        binding.colorRedImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.RED, TAG)
            dismiss()
        }
        binding.colorPinkImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.PINK, TAG)
            dismiss()
        }
        binding.colorPurpleImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.PURPLE, TAG)
            dismiss()
        }
        binding.colorBlueImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.BLUE, TAG)
            dismiss()
        }
        binding.colorIndigoImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.INDIGO, TAG)
            dismiss()
        }
        binding.colorGreenImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.GREEN, TAG)
            dismiss()
        }
        binding.colorTealImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.TEAL, TAG)
            dismiss()
        }
        binding.colorYellowImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.YELLOW, TAG)
            dismiss()
        }
        binding.colorWhiteImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.WHITE, TAG)
            dismiss()
        }
        binding.colorBlueGreyImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.BLUE_GRAY, TAG)
            dismiss()
        }
        binding.colorBlackImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.BLACK, TAG)
            dismiss()
        }
        binding.colorBrownImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(EditorManager.ColorManager.BROWN, TAG)
            dismiss()
        }

        binding.colorPickerImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorPickerClick()
            dismiss()
        }

        return binding.root
    }

    override fun onAttach(context: Context?) {
        try {
            mNotePropertiesClickListener = (context as OnNotePropertiesClickListener)
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement OnNotePropertiesClickListener")
        }
        super.onAttach(context)
    }

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
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