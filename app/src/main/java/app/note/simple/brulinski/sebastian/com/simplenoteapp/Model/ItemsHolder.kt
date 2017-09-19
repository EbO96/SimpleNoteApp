package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

/**
 * Created by sebas on 14.09.2017.
 */
class ItemsHolder {

    var title: String = ""
    var note: String = ""
    var date: String = ""
        get() = field
        set(value) {
            field = value
        }

    constructor(title: String, note: String, date: String) {
        this.title = title
        this.note = note
        this.date = date
    }
}