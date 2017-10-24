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
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.FontLayoutBinding

/*
Fragment displayed in BottomSheet
 */
class BottomSheetFontFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FontLayoutBinding
    private lateinit var mNotePropertiesClickListener: OnNotePropertiesClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.font_layout, container, false)

        val ed = EditorManager.FontStyleManager

        binding.fontDefault.setOnClickListener {
            mNotePropertiesClickListener.inEditorFontClick(ed.DEFAULT_FONT)
            dismiss()
        }

        binding.fontItalic.setOnClickListener {
            mNotePropertiesClickListener.inEditorFontClick(ed.ITALIC_FONT)
            dismiss()
        }

        binding.fontBoldItalic.setOnClickListener {
            mNotePropertiesClickListener.inEditorFontClick(ed.BOLD_ITALIC_FONT)
            dismiss()
        }

        binding.fontSerif.setOnClickListener {
            mNotePropertiesClickListener.inEditorFontClick(ed.SERIF_FONT)
            dismiss()
        }

        binding.fontSansSerif.setOnClickListener {
            mNotePropertiesClickListener.inEditorFontClick(ed.SANS_SERIF_FONT)
            dismiss()
        }

        binding.fontMonospace.setOnClickListener {
            mNotePropertiesClickListener.inEditorFontClick(ed.MONOSPACE_FONT)
            dismiss()
        }


        return binding.root
    }

    override fun onAttach(context: Context?) {
        try {
            mNotePropertiesClickListener = (context as OnNotePropertiesClickListener)
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnNotePropertiesClickListener")
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
        val contentView = View.inflate(context, R.layout.font_layout, null)
        dialog!!.setContentView(contentView)
        val layoutParams: CoordinatorLayout.LayoutParams = ((contentView.getParent()) as View).layoutParams as (CoordinatorLayout.LayoutParams)
        val behavior = (layoutParams.behavior as CoordinatorLayout.Behavior)

        if (behavior is BottomSheetBehavior) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

    }
}