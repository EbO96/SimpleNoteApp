package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity

/**
 * Created by sebas on 16.09.2017.
 */
class CurrentFragmentState {

    companion object {
        var CURRENT = "NONE"
        var backPressed = false
        var PREVIOUS = MainActivity.NOTE_LIST_FRAGMENT_TAG
        val FRAGMENT_ANIM_DURATION = 200L
    }
}