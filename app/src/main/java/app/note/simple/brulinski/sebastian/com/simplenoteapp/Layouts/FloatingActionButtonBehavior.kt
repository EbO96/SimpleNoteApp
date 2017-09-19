package app.note.simple.brulinski.sebastian.com.simplenoteapp.Layouts

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

/**
 * Created by sebas on 9/19/2017.
 */
class FloatingActionButtonBehavior(ctx: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<FloatingActionButton>() {
    override fun layoutDependsOn(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        val translationY = Math.min(0.0, (dependency!!.translationY - dependency.height).toDouble())
        child!!.translationY = translationY.toFloat()
        return true
    }

}