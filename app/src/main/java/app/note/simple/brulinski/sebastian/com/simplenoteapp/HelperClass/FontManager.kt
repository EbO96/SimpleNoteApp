package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.graphics.Typeface
import android.widget.EditText
import android.widget.TextView


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