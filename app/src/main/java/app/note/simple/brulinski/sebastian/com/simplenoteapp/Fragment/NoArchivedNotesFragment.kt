package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class NoArchivedNotesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = layoutInflater.inflate(R.layout.no_archived_notes_fragment, container, false)

        return root
    }
}