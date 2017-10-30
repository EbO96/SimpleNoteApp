package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.EditNoteFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.FragmentPagerAdapter.FragmentAdapter
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment.NotesListFragment
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.FragmentAndObjectStates
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.UpdateFragmentsChannel
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding
import es.dmoral.toasty.Toasty


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    /**
     * ViewPager object and Fragment Adapter object
     */
    private lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var mViewPager: ViewPager
    /**
     * Key's values
     */
    private val AFTER_EDIT = "AFTER EDIT"
    private val AFTER_CREATE = "AFTER CREATE"
    private val NOT_REFRESH = "NOT REFRESH"
    private var REFRESH_RECYCLER_AFTER = NOT_REFRESH
    private val UPDATE_CHANNEL_KEY = "update_channel_key"
    /**
     * Others
     */
    var doubleTapToExit = false
    lateinit var binding: ActivityMainBinding
    private lateinit var activityMain: MainActivity
    lateinit var updateChannel: UpdateFragmentsChannel
    /**
    Toasty Toasts colors
     */
    @ColorInt
    private val ERROR_COLOR = Color.parseColor("#D50000")
    private var infoToastShowedAtStart: Boolean = false
    /**
    There we starts...
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("interLog", "on create main")

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        Toasty.Config.getInstance().setErrorColor(ERROR_COLOR).apply()
        infoToastShowedAtStart = true
        activityMain = this
        if (savedInstanceState == null)
            updateChannel = UpdateFragmentsChannel()
        else updateChannel = savedInstanceState.getParcelable(UPDATE_CHANNEL_KEY)

        setupViewPager()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.i("interLog", "save state main")
        outState!!.putParcelable(UPDATE_CHANNEL_KEY, updateChannel)
        super.onSaveInstanceState(outState)
    }

    /**
     *  END OF onCreate(...)
     */

    fun getViewPager(): ViewPager {
        return mViewPager
    }

    fun getPagerAdapter(): FragmentAdapter {
        return fragmentAdapter
    }

    private fun setupViewPager() {
        mViewPager = binding.mainContainer
        fragmentAdapter = FragmentAdapter(supportFragmentManager, activityMain)
        mViewPager.adapter = fragmentAdapter
        mViewPager.currentItem = 1
        supportActionBar!!.title = getString(R.string.notes)

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        supportActionBar!!.title = getString(R.string.create)

//                        if (FragmentAndObjectStates.currentNote != null)
//                            EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
//                                    Color.BLACK)
                    }
                    1 -> {
                        supportActionBar!!.title = getString(R.string.notes)
                        if (updateChannel.checkUpdate(fragmentAdapter.getItem(mViewPager.currentItem) as NotesListFragment)) {
                            Log.i("interLog", "refresh")
                            fragmentAdapter.notifyDataSetChanged()
                            updateChannel.clearUpdate()
                            //updateChannel.clearUpdate()
                        }
//                        EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
//                                Color.BLACK)
                    }
                    2 -> {
                        supportActionBar!!.title = getString(R.string.preview)
//                        if (FragmentAndObjectStates.currentNote != null) {
//                            EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
//                                    FragmentAndObjectStates.currentNote!!.BGColor)
//                        } else
                    }
                    3 -> {
                        supportActionBar!!.title = getString(R.string.edit)
//                        if (updateChannel.checkUpdate(fragmentAdapter.getItem(mViewPager.currentItem) as EditNoteFragment)) {
//                            fragmentAdapter.notifyDataSetChanged()
//                            updateChannel.noUpdateEdit()
//                        }
//                        if (FragmentAndObjectStates.currentNote != null) {
//                            fragmentAdapter.notifyDataSetChanged()
//                            EditorManager.ColorManager(activityMain).changeColor(arrayListOf(EditorManager.ColorManager.ACTION_BAR_COLOR),
//                                    FragmentAndObjectStates.currentNote!!.BGColor)
//                        }
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    /**
     * MENU
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /*
    Select menu item at Toolbar and execute action
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_main -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.archives -> {
                val intent = Intent(this, ArchivesActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        doubleTapBackToExit()
    }

    private fun doubleTapBackToExit() {
        if (doubleTapToExit) {
            finish()
        }

        Toasty.error(this, getString(R.string.exit_toast), Toast.LENGTH_SHORT, true).show()
        doubleTapToExit = true

        Handler().postDelayed({
            doubleTapToExit = false
        }, 2000)
    }
}