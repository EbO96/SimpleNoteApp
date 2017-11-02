package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS



/**
 * Created by sebas on 11/2/2017.
 */
class InputMethodsManager {

    companion object {
        fun hideKeyboard(activity: Activity) {
            try {
                val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(activity.currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } catch (e: Exception) {
                // Ignore exceptions if any
                Log.e("KeyBoardUtil", e.toString(), e)
            }

        }
    }
}