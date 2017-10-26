package app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color.rgb
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView

/**
This class is used to create colors by the user.
User sets three RGB values 0-255 and creates a HEX color value on them.
 */
class ColorCreator(private val R: Int, private val G: Int, private val B: Int, private val activity: Activity) {

    var hexColorValue: String

    init {
        hexColorValue = makeHexColorValue()
        saveToSharedPref()
    }

    private fun makeHexColorValue(): String {
        return String.format("#%02x%02x%02x", R, G, B)
    }

    fun getColor(): Int {
        return rgb(R, G, B)
    }

    private fun saveToSharedPref() {
        val preferences: SharedPreferences = activity.getPreferences(0)
        val editor = preferences.edit()
        editor.putInt(RED_KEY, R)
        editor.putInt(GREEN_KEY, G)
        editor.putInt(BLUE_KEY, B)
        editor.apply()
    }

    companion object {
        /*
       * Shared preferences KEY's
       */
        val RED_KEY = "red_key"
        val GREEN_KEY = "green_key"
        val BLUE_KEY = "blue_key"

        fun getRGBFromSharedPreferences(activity: Activity): Array<HashMap<String, Int>> {
            val preferences: SharedPreferences = activity.getPreferences(0)

            val rShared = preferences.getInt(RED_KEY, 0)
            val gShared = preferences.getInt(GREEN_KEY, 0)
            val bShared = preferences.getInt(BLUE_KEY, 0)

            val r = HashMap<String, Int>()
            val g = HashMap<String, Int>()
            val b = HashMap<String, Int>()

            r.put(RED_KEY, rShared)
            g.put(GREEN_KEY, gShared)
            b.put(BLUE_KEY, bShared)

            return arrayOf(r, g, b)
        }

        fun getColorFromSharedPreferences(activity: Activity): Int {
            val preferences: SharedPreferences = activity.getPreferences(0)

            val rShared = preferences.getInt(RED_KEY, 0)
            val gShared = preferences.getInt(GREEN_KEY, 0)
            val bShared = preferences.getInt(BLUE_KEY, 0)

            return rgb(rShared, gShared, bShared)
        }

        fun getColorFromCard(activity: Activity, cardView: CardView): Int {
            val states = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_enabled, android.R.attr.state_checked,
                    android.R.attr.state_pressed)
            val defaultColor = ContextCompat.getColor(activity, app.note.simple.brulinski.sebastian.com.simplenoteapp.R.color.material_white)

            return cardView.cardBackgroundColor.getColorForState(states, defaultColor)
        }
    }
}