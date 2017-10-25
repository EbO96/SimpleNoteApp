package app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces

/**
 * Created by sebas on 10/23/2017.
 */
interface OnNotePropertiesClickListener {
    fun inEditorColorClick(color: String, colorOfWhat: String)
    fun inEditorFontClick(whichFont: String)
    fun inEditorColorPickerClick()
}