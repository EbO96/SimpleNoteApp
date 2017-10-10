package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model



class Notes {

    class Note {

        var id: String? = null

        var title: String? = null

        var note: String? = null

        var date: String? = null


        constructor(id: String?, title: String?, note: String?, date: String?) {
            this.id = id
            this.title = title
            this.note = note
            this.date = date
        }
    }
}

