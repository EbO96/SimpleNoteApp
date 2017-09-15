package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by sebas on 15.09.2017.
 */
class NotesListFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root : View = inflater!!.inflate(R.layout.notes_list_fragment, container, false)
        return root
    }
}