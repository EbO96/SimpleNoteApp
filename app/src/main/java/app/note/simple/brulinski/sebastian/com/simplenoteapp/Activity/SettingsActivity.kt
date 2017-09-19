package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val view = findViewById<CoordinatorLayout>(R.id.coordinator_layout)

        Snackbar.make(view, "Test", Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
