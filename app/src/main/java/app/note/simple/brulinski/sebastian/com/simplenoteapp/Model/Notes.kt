package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

import android.graphics.Color
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager


class Notes {

    class Note {

        companion object {
            val default = NoteItem(null, "", "", "", Color.WHITE, Color.BLACK, EditorManager.FontStyleManager.DEFAULT_FONT, false, false)
        }

        var id: Int? = null

        var title: String? = null

        var note: String? = null

        var date: String? = null

        var BGColor: Int? = null

        var TXTColor: Int? = null

        var fontStyle: String? = null

        var isDeleted: Boolean? = null

        var isSelected: Boolean? = null


        constructor(id: Int?, title: String?, note: String?, date: String?, BGColor: Int?, TXTColor: Int?,
                    fontStyle: String?, isDeleted: Boolean?, isSelected: Boolean?) {
            this.id = id
            this.title = title
            this.note = note
            this.date = date
            this.BGColor = BGColor
            this.TXTColor = TXTColor
            this.fontStyle = fontStyle
            this.isDeleted = isDeleted
            this.isSelected = isSelected
        }
    }
}

