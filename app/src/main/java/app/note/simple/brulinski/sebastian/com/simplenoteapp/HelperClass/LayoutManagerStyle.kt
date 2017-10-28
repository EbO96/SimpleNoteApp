package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

/**
 * Get RecyclerView LayoutManager style from preferences
 */
class LayoutManagerStyle(val activity: Activity) {

        val sharedPref: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        var flag: Boolean = sharedPref.getBoolean(activity.getString(R.string.layout_manager_key), true)

}