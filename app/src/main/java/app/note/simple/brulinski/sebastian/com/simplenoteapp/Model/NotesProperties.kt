package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

class NotesProperties {

    var id: String? = null
    var bgColor: String? = null
    var textColor: String? = null
    var fontColor: String? = null

    constructor(id: String?, bgColor: String?, textColor: String?, fontColor: String?) {
        this.id = id
        this.bgColor = bgColor
        this.textColor = textColor
        this.fontColor = fontColor
    }
}