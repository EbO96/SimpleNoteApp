package app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor

import android.graphics.Color.rgb

/**
This class is used to create colors by the user.
User sets three RGB values 0-255 and creates a HEX color value on them.
 */
class ColorCreator(private val R: Int, private val G: Int, private val B: Int) {

    private var hexColorValue: String

    init {
        hexColorValue = makeHexColorValue()
    }

    private fun makeHexColorValue(): String {
        return String.format("#%02x%02x%02x", R, G, B)
    }

    fun getColor(): Int {
        return rgb(R, G, B)
    }

    private fun saveToDatabase() {
        //TODO save hex value to database
    }
}