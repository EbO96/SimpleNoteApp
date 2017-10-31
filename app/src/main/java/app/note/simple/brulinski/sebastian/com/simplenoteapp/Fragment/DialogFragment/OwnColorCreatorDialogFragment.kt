package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.DialogFragment

import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.ColorCreator
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ColorCreatorLayoutBinding


class OwnColorCreatorDialogFragment : DialogFragment() {

    /**
     * Other's
     */
    private lateinit var binding: ColorCreatorLayoutBinding
    private var COLOR_OF_TAG = EditorManager.ColorManager.COLOR_OF_NOTE
    /**
    RGB value range 0-255
     */
    private val RGB_MAX_VALUE = 255
    /*
    SeekBar's control values
     */
    private var isEditTextUnlocked = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.color_creator_layout, container, false)

        seekBarsListeners()
        rgbEditsListeners()

        setLayout()

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.applyButton.setOnClickListener {
            val main = (activity as MainActivity)
            dismiss()
            when (COLOR_OF_TAG) {
                EditorManager.ColorManager.COLOR_OF_TEXT -> {
                    main.setColorBottomSheet()
                }
                EditorManager.ColorManager.COLOR_OF_NOTE -> {
                    main.setColorBottomSheet(true)
                }
            }
        }
        COLOR_OF_TAG = arguments.getString(EditorManager.ColorManager.COLOR_OF_KEY)
        return binding.root
    }

    private fun setLayout() { //This method gets the data from shared preferences and sets the RGB values at SeekBar's
        val arrayOfRGB = ColorCreator.getRGBFromSharedPreferences(activity)

        val R = arrayOfRGB[0][ColorCreator.RED_KEY]!!.toInt()
        val G = arrayOfRGB[1][ColorCreator.GREEN_KEY]!!.toInt()
        val B = arrayOfRGB[2][ColorCreator.BLUE_KEY]!!.toInt()

        binding.seekBarRedValue.progress = R
        binding.seekBarGreenValue.progress = G
        binding.seekBarBlueValue.progress = B
    }

    private fun setHexPreview() {
        val R = binding.seekBarRedValue.progress
        val G = binding.seekBarGreenValue.progress
        val B = binding.seekBarBlueValue.progress

        val color = ColorCreator(R, G, B, activity)
        color.saveToSharedPref()

        binding.hexValuePreview.text = color.hexColorValue
        binding.colorPreview.cardBackgroundColor = ColorStateList.valueOf(color.getColor())
    }

    private fun seekBarsListeners() {

        binding.seekBarRedValue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.redValueEdit.setText("$p1")
                setHexPreview()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isEditTextUnlocked = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isEditTextUnlocked = true
            }
        })

        binding.seekBarGreenValue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.greenValueEdit.setText("$p1")
                setHexPreview()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isEditTextUnlocked = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isEditTextUnlocked = true
            }
        })

        binding.seekBarBlueValue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.blueValueEdit.setText("$p1")
                setHexPreview()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isEditTextUnlocked = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isEditTextUnlocked = true
            }
        })
    }

    private fun rgbEditsListeners() {

        binding.redValueEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var value: Int

                if (p0!!.isNotEmpty()) {
                    value = p0.toString().toInt()

                    if (value > RGB_MAX_VALUE)
                        value = p0.subSequence(1, 3).toString().toInt()

                } else value = 0

                if (p0.isNotEmpty()) {
                    val length = value.toString().length
                    binding.redValueEdit.setSelection(length)
                }
                binding.seekBarRedValue.progress = value

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.greenValueEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var value: Int

                if (p0!!.isNotEmpty()) {
                    value = p0.toString().toInt()

                    if (value > RGB_MAX_VALUE)
                        value = p0.subSequence(1, 3).toString().toInt()

                } else value = 0

                if (p0.isNotEmpty()) {
                    val length = value.toString().length
                    binding.greenValueEdit.setSelection(length)
                }
                binding.seekBarGreenValue.progress = value
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.blueValueEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var value: Int

                if (p0!!.isNotEmpty()) {
                    value = p0.toString().toInt()

                    if (value > RGB_MAX_VALUE)
                        value = p0.subSequence(1, 3).toString().toInt()

                } else value = 0

                if (p0.isNotEmpty()) {
                    val length = value.toString().length
                    binding.blueValueEdit.setSelection(length)
                }
                binding.seekBarBlueValue.progress = value

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val menuInflater: MenuInflater = activity.menuInflater
//        menuInflater.inflate(R.menu.own_color_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item!!.itemId) {
//            R.id.home -> {
//                navigateToParent()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    override fun onBackPressed() {
//        navigateToParent()
//        super.onBackPressed()
//    }

//    private fun navigateToParent() {
//        val intent = Intent(this, MainActivity::class.java)
//        NavUtils.navigateUpTo(this, intent)
//    }

    override fun onResume() {
        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams

        super.onResume()
    }

//    override fun onDismiss(dialog: DialogInterface?) {
//        super.onDismiss(dialog)
//        val main = (activity as MainActivity)
//
//        when (COLOR_OF_TAG) {
//            EditorManager.ColorManager.COLOR_OF_TEXT -> {
//                main.setColorBottomSheet()
//            }
//            EditorManager.ColorManager.COLOR_OF_NOTE -> {
//                main.setColorBottomSheet(true)
//            }
//        }
//    }

}
