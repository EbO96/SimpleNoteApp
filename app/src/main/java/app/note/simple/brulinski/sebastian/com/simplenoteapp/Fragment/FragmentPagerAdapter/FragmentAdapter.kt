package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.FragmentPagerAdapter

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.CreateNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.EditNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotePreviewFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment


class FragmentAdapter(fm: FragmentManager?, var activity: Activity) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                CreateNoteFragment()
            }
            1 -> {
                NotesListFragment()
            }
            2 -> {
                NotePreviewFragment()
            }
            3 -> {
                EditNoteFragment()
            }
            else -> CreateNoteFragment()
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }
}