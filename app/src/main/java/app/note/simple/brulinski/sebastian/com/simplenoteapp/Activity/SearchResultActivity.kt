package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle

/**
 * Activity to handle user search query
 */
class SearchResultActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if(intent != null) handleIntent(intent)
    }

    private fun handleIntent(intent: Intent){
        if(Intent.ACTION_SEARCH == intent.action){
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
        }
    }
}