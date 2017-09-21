package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.content.Context
import android.graphics.Typeface
import android.widget.EditText
import android.widget.TextView
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import kotlinx.android.synthetic.main.activity_settings.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource

class EditorManager {

    class BackgroundColorManager {
        companion object {

            fun changeColor(view: ArrayList<Any>, color: Int) {
                try {
                    var myView: TextView? = null
                    for (x in 0 until view.size) {
                        if (view[x] is TextView) {
                            myView = (view[x] as TextView)
                        } else if (view is EditText) {
                            myView = (view[x] as EditText)
                        }
                        myView!!.backgroundColor = color
                    }
                } catch (e: Exception) {
                    throw Exception("Wrong color. As \"color\" parameter you must pass color from resource for example - R.color.red")
                }

            }
        }
    }

    class FontManager {

        companion object {
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