package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.content.Context
import android.support.v4.content.ContextCompat
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NoteItem
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

/**
 * Class to store current fragment and actually state of note object selected by user
 */
class FragmentAndObjectStates {

    companion object {
        var currentNote: NoteItem? = null
        var currentFragment = 0
        var itemPositionInRecycler: Int? = null
        var refreshPreview = false
        fun getDefaultNote(context: Context): NoteItem {
            return NoteItem(null, "", "", "", ContextCompat.getColor(context, R.color.material_white), ContextCompat.getColor(context, R.color.material_black), EditorManager.FontStyleManager.DEFAULT_FONT, false, false)
        }
    }
}