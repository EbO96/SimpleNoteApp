package app.note.simple.brulinski.sebastian.com.simplenoteapp

/**
 * Created by sebas on 14.09.2017.
 */
class ItemsHolder {

    var title: String = ""
    var note: String = ""


        get() = field
        set(value) {
            field = value
        }

    constructor(title: String, note: String) {
        this.title = title
        this.note = note
    }
}