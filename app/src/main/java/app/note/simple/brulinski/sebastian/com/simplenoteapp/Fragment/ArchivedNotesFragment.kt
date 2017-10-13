package app.note.simple.brulinski.sebastian.com.simplenoteapp.Fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.ArchivesRecycler
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ArchivedNotesFragmentBinding
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator

class ArchivedNotesFragment : Fragment() {

    lateinit var mChangeScreenCallback: OnChangeScreenListener

    interface OnChangeScreenListener{
        fun replaceFragment()
    }

    fun setOnChangeScreenListener(mChangeScreenCallback: OnChangeScreenListener){
        this.mChangeScreenCallback = mChangeScreenCallback
    }

    private lateinit var binding: ArchivedNotesFragmentBinding
    private lateinit var myRecycler: ArchivesRecycler

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.archived_notes_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecycler(arguments.getParcelableArrayList<ItemsHolder>("archivedNoteObject"))
        listenRecyclerSize()
    }

    private fun initRecycler(itemsHolderArrayList: ArrayList<ItemsHolder>) {
        val recycler = binding.archivesRecycler
        recycler.itemAnimator = SlideInRightAnimator()
        recycler.layoutManager = LinearLayoutManager(context)

        myRecycler = ArchivesRecycler(itemsHolderArrayList, recycler, context)

        recycler.adapter = myRecycler
    }

    private fun listenRecyclerSize() {
        myRecycler.setOnRecyclerSizeListener(object : ArchivesRecycler.OnRecyclerSizeListener {
            override fun recyclerSize(size: Int) {
                if(size == 0)
                    mChangeScreenCallback.replaceFragment()

            }
        })
    }
}