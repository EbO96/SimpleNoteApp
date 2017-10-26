package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.BottomSheetFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.ColorCreator
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.OnNotePropertiesClickListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ColorsLayoutBinding
import org.jetbrains.anko.backgroundColor


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

        val col = ColorCreator

        binding.customColorCard.cardBackgroundColor= ColorStateList.valueOf(col.getColorFromSharedPreferences(activity))

        binding.colorRedImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorRedImage), TAG)
            dismiss()
        }
        binding.colorPinkImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorPinkImage), TAG)
            dismiss()
        }
        binding.colorPurpleImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorPurpleImage), TAG)
            dismiss()
        }
        binding.colorBlueImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorBlueImage), TAG)
            dismiss()
        }
        binding.colorIndigoImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorIndigoImage), TAG)
            dismiss()
        }
        binding.colorGreenImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorGreenImage), TAG)
            dismiss()
        }
        binding.colorTealImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorTealImage), TAG)
            dismiss()
        }
        binding.colorYellowImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorYellowImage), TAG)
            dismiss()
        }
        binding.colorWhiteImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorWhiteImage), TAG)
            dismiss()
        }
        binding.colorBlueGreyImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorBlueGreyImage), TAG)
            dismiss()
        }
        binding.colorBlackImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorBlackImage), TAG)
            dismiss()
        }
        binding.colorBrownImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.colorBrownImage), TAG)
            dismiss()
        }

        binding.colorPickerImage.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorPickerClick()
            dismiss()
        }

        binding.customColorCard.setOnClickListener {
            mNotePropertiesClickListener.inEditorColorClick(col.getColorFromCard(activity, binding.customColorCard), TAG)
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