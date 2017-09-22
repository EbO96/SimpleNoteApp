package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.support.v7.widget.CardView
import android.widget.EditText
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class EditorManager {

    @Suppress("DEPRECATION")
    class BackgroundColorManager(val ctx: Context) {
        companion object {

            var currentColor = "WHITE"

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

    class FontManager {

        companion object {
            var currentFont = "DEFAULT"
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
                if (font.equals(FontManager.DEFAULT_FONT)) {
                    FontManager.setUpFontStyle(Typeface.DEFAULT, title, note)
                } else if (font.equals(FontManager.ITALIC_FONT)) {
                    FontManager.setUpFontStyle(Typeface.ITALIC, title, note)
                } else if (font.equals(FontManager.BOLD_ITALIC_FONT)) {
                    FontManager.setUpFontStyle(Typeface.BOLD_ITALIC, title, note)
                } else if (font.equals(FontManager.SERIF_FONT)) {
                    FontManager.setUpFontStyle(Typeface.SERIF, title, note)
                } else if (font.equals(FontManager.SANS_SERIF_FONT)) {
                    FontManager.setUpFontStyle(Typeface.SANS_SERIF, title, note)
                } else if (font.equals(FontManager.MONOSPACE_FONT)) {
                    FontManager.setUpFontStyle(Typeface.MONOSPACE, title, note)
                }
            }
        }
    }
}