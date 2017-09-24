package app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.widget.EditText
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.textColor

class EditorManager {

    class ColorManager(private val ctx: Context) {
        companion object {

            val RED = "RED"
            val PINK = "PINK"
            val PURPLE = "PURPLE"
            val BLUE = "BLUE"
            val INDIGO = "INDIGO"
            val GREEN = "GREEN"
            val TEAL = "TEAL"
            val YELLOW = "YELLOW"
            val WHITE = "WHITE"
            val BLUE_GRAY = "BLUE_GRAY"
            val BLACK = "BLACK"
            val BROWN = "BROWN"

            fun changeFontColor(views: ArrayList<Any>, color: Int) {
                var myView: TextView?

                for (x in 0 until views.size) {
                    if (views[x] is EditText)
                        myView = (views[x] as EditText)
                    else {
                        myView = (views[x] as TextView)
                    }
                    myView.textColor = color
                    myView.hintTextColor = color
                }
            }

            fun changeBgColor(view: ArrayList<Any>, color: Int) {
                try {
                    var myView: CardView? = null

                    for (x in 0 until view.size) {
                        if (view[x] is CardView)
                            myView = (view[x] as CardView)
                        myView!!.cardBackgroundColor = ColorStateList.valueOf(color)
                    }
                } catch (e: Exception) {
                    throw Exception("Wrong color. As \"color\" parameter you must pass color from resource for example - R.color.red")
                }

            }
        }

        fun recogniseAndSetColor(color: String, viewsArray: ArrayList<Any>, colorOf: String) {
            var resColor = 0

            when (color) {
                RED -> resColor = ContextCompat.getColor(ctx, R.color.material_red)
                PINK -> resColor = ContextCompat.getColor(ctx,R.color.material_pink)
                PURPLE -> resColor = ContextCompat.getColor(ctx,R.color.material_purple)
                BLUE -> resColor = ContextCompat.getColor(ctx,R.color.material_blue)
                INDIGO -> resColor = ContextCompat.getColor(ctx,R.color.material_indigo)
                GREEN -> resColor = ContextCompat.getColor(ctx,R.color.material_green)
                TEAL -> resColor = ContextCompat.getColor(ctx,R.color.material_teal)
                YELLOW -> resColor = ContextCompat.getColor(ctx,R.color.material_yellow)
                WHITE -> resColor = ContextCompat.getColor(ctx,R.color.material_white)
                BLUE_GRAY -> resColor = ContextCompat.getColor(ctx,R.color.material_blue_grey)
                BLACK -> resColor = ContextCompat.getColor(ctx,R.color.material_black)
                BROWN -> resColor = ContextCompat.getColor(ctx,R.color.material_brown)
            }

            if (colorOf.equals("BG")) {//Color of background
                changeBgColor(viewsArray, resColor)
            } else if (colorOf.equals("FONT")) { //Color of font
                changeFontColor(viewsArray, resColor)
            }
        }
    }

    class FontStyleManager {

        companion object {
            val DEFAULT_FONT = "DEFAULT"
            val ITALIC_FONT = "ITALIC"
            val BOLD_ITALIC_FONT = "BOLD_ITALIC"
            val SERIF_FONT = "SERIF"
            val SANS_SERIF_FONT = "SANS_SERIF"
            val MONOSPACE_FONT = "MONOSPACE"

            fun setUpFontStyle(font: Any?, viewTitle: Any?, viewNote: Any?) {
                val recognisedFont: Typeface?

                val title: TextView?
                val note: TextView?

                if (font is Int) {
                    recognisedFont = Typeface.defaultFromStyle(font)
                } else recognisedFont = (font as Typeface)

                if (viewTitle is TextView) {
                    title = viewTitle
                    note = (viewNote as TextView?)
                } else {
                    title = (viewTitle as EditText)
                    note = (viewNote as EditText)
                }

                title.typeface = recognisedFont
                note!!.typeface = recognisedFont
            }

            fun recogniseAndSetFont(font: String, title: TextView, note: TextView) {
                if (font.equals(DEFAULT_FONT)) {
                    setUpFontStyle(Typeface.DEFAULT, title, note)
                } else if (font.equals(ITALIC_FONT)) {
                    setUpFontStyle(Typeface.ITALIC, title, note)
                } else if (font.equals(BOLD_ITALIC_FONT)) {
                    setUpFontStyle(Typeface.BOLD_ITALIC, title, note)
                } else if (font.equals(SERIF_FONT)) {
                    setUpFontStyle(Typeface.SERIF, title, note)
                } else if (font.equals(SANS_SERIF_FONT)) {
                    setUpFontStyle(Typeface.SANS_SERIF, title, note)
                } else if (font.equals(MONOSPACE_FONT)) {
                    setUpFontStyle(Typeface.MONOSPACE, title, note)
                }
            }
        }
    }
}