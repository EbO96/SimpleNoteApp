package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SeekBar
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.ColorCreator
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityOwnColorCreatorBinding

class OwnColorCreatorActivity : AppCompatActivity() {

    lateinit var binding: ActivityOwnColorCreatorBinding

    /**
    RGB value range 0-255
     */
    private val RGB_MAX_VALUE = 255

    /*
    SeekBar's control values
     */
    private var isEditTextUnlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_own_color_creator)
        setSupportActionBar(binding.ownColorCreatorToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        seekBarsListeners()
        rgbEditsListeners()

        setLayout()
    }

    private fun setLayout(){ //This method gets the data from shared preferences and sets the RGB values at SeekBar's
        val arrayOfRGB = ColorCreator.getRGBFromSharedPreferences(this)

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

        val color = ColorCreator(R, G, B, this)
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
                //TODO
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
                //TODO
            }
        })

        binding.greenValueEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO
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
                //TODO
            }
        })

        binding.blueValueEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO
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
                //TODO
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.own_color_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
        super.onBackPressed()
    }
}
