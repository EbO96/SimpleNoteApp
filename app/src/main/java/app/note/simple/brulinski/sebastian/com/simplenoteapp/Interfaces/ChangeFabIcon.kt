package app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces

/**
 Interface using to changing FloatingActionButton from Fragments level.
 FloatingActionButton with root in MainActivity
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