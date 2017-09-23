package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.support.v7.widget.CardView
import android.widget.EditText
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class EditorManager {

    companion object {
        fun resetAllToDefault() {
            BackgroundColorManager.currentBgColor = BackgroundColorManager.WHITE
            FontStyleManager.currentFontStyle = FontStyleManager.DEFAULT_FONT
        }
    }

    @Suppress("DEPRECATION")
    class BackgroundColorManager(val ctx: Context) {
        companion object {

            var currentBgColor = "WHITE"

            var RED = "RED"
            var BLUE = "BLUE"
            var GREEN = "GREEN"
            var YELLOW = "YELLOW"
            var WHITE = "WHITE"

            fun changeColor(view: ArrayList<Any>, color: Int) {
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

        fun recogniseAndSetBackgroundColor(color: String, cardView: CardView) {
            when (color) {
                RED -> {
                    changeColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_red))
                }
                BLUE -> {
                    changeColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_blue))

                }
                GREEN -> {
                    changeColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_green))

                }
                YELLOW -> {
                    changeColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_yellow))

                }
                WHITE -> {
                    changeColor(arrayListOf(cardView), ctx.resources.getColor(R.color.material_white))

                }
            }
        }
    }

    class FontColorManager {
        companion object {
            val FONT_BLACK = "BLACK"

            var currentFontColor = FONT_BLACK
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