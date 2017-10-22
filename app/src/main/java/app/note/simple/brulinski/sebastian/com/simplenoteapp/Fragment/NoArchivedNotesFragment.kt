package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.CurrentFragmentState
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import com.labo.kaji.fragmentanimations.MoveAnimation
import com.labo.kaji.fragmentanimations.PushPullAnimation

class NoArchivedNotesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = layoutInflater.inflate(R.layout.no_notes_fragment, container, false)

        Log.i("fromNoArchives", "Hello NoArchives")
        return root
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        if (enter) return PushPullAnimation.create(MoveAnimation.DOWN, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)
        else return PushPullAnimation.create(MoveAnimation.UP, enter, CurrentFragmentState.FRAGMENT_ANIM_DURATION)

    }
}