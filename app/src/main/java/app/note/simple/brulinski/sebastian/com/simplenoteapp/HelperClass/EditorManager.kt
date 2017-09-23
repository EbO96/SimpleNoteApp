package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.support.v7.widget.CardView
import android.widget.EditText
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.textColor

class EditorManager {

    companion object {
        fun resetAllToDefault() {
            ColorManager.currentBgColor = ColorManager.WHITE
            FontStyleManager.currentFontStyle = FontStyleManager.DEFAULT_FONT
        }
    }

    @Suppress("DEPRECATION")
    class ColorManager(val ctx: Context) {
        companion object {

            var currentBgColor = "WHITE"
            var currentFontColor = "BLACK"

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

        fun recogniseAndSetColor(color: String, cardView: CardView) {

            when (color) {
                RED -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_red))
                }
                PINK -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_pink))
                }
                PURPLE -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_purple))
                }
                BLUE -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_blue))

                }
                INDIGO -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_indigo))
                }
                GREEN -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_green))

                }
                TEAL -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_teal))
                }
                YELLOW -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_yellow))

                }
                WHITE -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_white))

                }
                BLUE_GRAY -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_blue_grey))
                }
                BLACK -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_black))
                }
                BROWN -> {
                    changeBgColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_brown))
                }
            }
        }
    }

    class FontColorManager {
        companion object {


        }
    }

    class FontStyleManager {

        companion object {
            var currentFontStyle = "DEFAULT"
            val DEFAULT_FONT = "DEFAULT"
            val ITALIC_FONT = "ITALIC"
            val BOLD_ITALIC_FONT = "BOLD_ITALIC"
            val SERIF_FONT = "SERIF"
            val SANS_SERIF_FONT = "SANS_SERIF"
            val MONOSPACE_FONT = "MONOSPACE"

            fun setUpFontStyle(font: Any?, viewTitle: Any?, viewNote: Any?) {
                val recognisedFont: Typeface?

                var title: TextView?
                var note: TextView?

                if (font is Int) {
                    recognisedFont = Typeface.defaultFromStyle(font)
                } else recognisedFont = (font as Typeface)

                if (viewTitle is TextView) {
                    title = (viewTitle as TextView)
                    note = (viewNote as TextView?)
                } else {
                    title = (viewTitle as EditText)
                    note = (viewNote as EditText)
                }

                title.typeface = recognisedFont
                note!!.typeface = recognisedFont
            }

            fun recogniseAndSetFont(font: String, title: TextView, note: TextView) {
                if (font.equals(FontStyleManager.DEFAULT_FONT)) {
                    FontStyleManager.setUpFontStyle(Typeface.DEFAULT, title, note)
                } else if (font.equals(FontStyleManager.ITALIC_FONT)) {
                    FontStyleManager.setUpFontStyle(Typeface.ITALIC, title, note)
                } else if (font.equals(FontStyleManager.BOLD_ITALIC_FONT)) {
                    FontStyleManager.setUpFontStyle(Typeface.BOLD_ITALIC, title, note)
                } else if (font.equals(FontStyleManager.SERIF_FONT)) {
                    FontStyleManager.setUpFontStyle(Typeface.SERIF, title, note)
                } else if (font.equals(FontStyleManager.SANS_SERIF_FONT)) {
                    FontStyleManager.setUpFontStyle(Typeface.SANS_SERIF, title, note)
                } else if (font.equals(FontStyleManager.MONOSPACE_FONT)) {
                    FontStyleManager.setUpFontStyle(Typeface.MONOSPACE, title, note)
                }
            }
        }
    }
}