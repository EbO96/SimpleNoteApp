package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

/**
 * Created by sebas on 9/19/2017.
 */


class Notes {

    class Note {


        var title: String? = null

        var note: String? = null

        var date: String? = null

        var font: String? = null

        constructor(title: String?, note: String?, date: String?, font: String?) {
            this.title = title
            this.note = note
            this.date = date
            this.font = font
        }
    }
}

