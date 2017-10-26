package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model


class Notes {

    class Note {

        var id: String? = null

        var title: String? = null

        var note: String? = null

        var date: String? = null

        var rBGColor: Int? = null

        var gBGColor: Int? = null

        var bBGColor: Int? = null

        var rTXTColor: Int? = null

        var gTXTColor: Int? = null

        var bTXTColor: Int? = null

        var fontStyle: String? = null

        var isDeleted: Boolean? = null


        constructor(id: String?, title: String?, note: String?, date: String?, rBGColor: Int?, gBGColor: Int?, bBGColor: Int?, rTXTColor: Int?, gTXTColor: Int?, bTXTColor: Int?,
                    fontStyle: String?, isDeleted: Boolean?) {
            this.id = id
            this.title = title
            this.note = note
            this.date = date
            this.rBGColor = rBGColor
            this.bBGColor = bBGColor
            this.gBGColor = gBGColor
            this.rTXTColor = rTXTColor
            this.bTXTColor = bTXTColor
            this.gTXTColor = gTXTColor
            this.fontStyle = fontStyle
            this.isDeleted = isDeleted
        }
    }
}

