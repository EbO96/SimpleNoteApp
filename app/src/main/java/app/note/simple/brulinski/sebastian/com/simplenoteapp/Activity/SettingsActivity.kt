package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v4.app.NavUtils
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_general)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
        super.onBackPressed()
    }
}
