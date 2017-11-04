package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.SearchDatabaseTable

/**
 * Activity to handle user search query
 */
class SearchResultActivity : Activity() {

    /**
    Search database
     */
    private lateinit var db: SearchDatabaseTable.DatabaseOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("searchActivity", "onCreate")
        db = SearchDatabaseTable.DatabaseOpenHelper(this)
        handleIntent(intent)//Handle search intent among others
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        db = SearchDatabaseTable.DatabaseOpenHelper(this)
        if (intent != null) handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val c = db.getWordMatches(query, null)
        }
    }
}