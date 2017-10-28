package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity.MainActivity
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor.EditorManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.MainRecyclerSizeListener
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import com.labo.kaji.fragmentanimations.MoveAnimation
import com.labo.kaji.fragmentanimations.PushPullAnimation

class NoNotesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
        Change color of status bar
         */
        val colorMng = EditorManager.ColorManager(activity)
        colorMng.changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR), Color.BLACK)

        return inflater!!.inflate(R.layout.no_notes_fragment, container, false)
    }

    override fun onStart() {
        val mSizeListener: MainRecyclerSizeListener = (context as MainActivity)
        if (MainActivity.adapterSize != 0)
            mSizeListener.getRecyclerAdapterSize(1)
        super.onStart()
    }
    //TODO
//    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
//        if (enter) return PushPullAnimation.create(MoveAnimation.DOWN, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
//        else return PushPullAnimation.create(MoveAnimation.UP, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
//
//    }
}