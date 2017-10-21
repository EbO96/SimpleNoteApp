package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.MainRecyclerSizeListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class NoNotesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.no_notes_fragment, container, false)
    }

    override fun onStart() {
        val mSizeListener: MainRecyclerSizeListener = (context as MainActivity)
        if (MainActivity.adapterSize != 0)
            mSizeListener.getRecyclerAdapterSize(1)
        super.onStart()
    }
}