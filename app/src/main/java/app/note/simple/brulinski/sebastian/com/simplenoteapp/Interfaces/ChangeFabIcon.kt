package app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces

/**
 * Created by sebas on 9/23/2017.
 */
interface ChangeFabIcon {

    companion object {
        val EDIT = "EDIT"
        val PREVIEW = "PREVIEW"
        val CREATE = "CREATE"
        val LIST = "LIST"
    }

    fun changeFabDrawableIcon(from: String) {}
}