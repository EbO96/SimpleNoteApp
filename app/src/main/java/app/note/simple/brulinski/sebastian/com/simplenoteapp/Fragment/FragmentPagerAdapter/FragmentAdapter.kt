package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.FragmentPagerAdapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter


class FragmentAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private val fragmentsArrayList = ArrayList<Fragment>()
    private val fragmentsTitlesArrayList = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragmentsArrayList.add(fragment)
        fragmentsTitlesArrayList.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentsArrayList[position]
    }

    override fun getItemPosition(`object`: Any?): Int {
        return super.getItemPosition(`object`)
    }

    override fun getCount(): Int {
        return fragmentsArrayList.size
    }
}