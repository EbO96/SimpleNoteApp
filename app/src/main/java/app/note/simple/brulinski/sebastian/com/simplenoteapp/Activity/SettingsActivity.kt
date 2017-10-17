package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.os.Bundle
import android.preference.PreferenceActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_general)
    }
}
