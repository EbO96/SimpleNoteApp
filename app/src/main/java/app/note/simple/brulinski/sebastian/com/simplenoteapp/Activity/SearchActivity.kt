package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.SearchResultRecycler
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivitySearchBinding
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var noteObjectsArray: ArrayList<ItemsHolder>
    private lateinit var myRecycler: SearchResultRecycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        noteObjectsArray = intent.extras.getParcelableArrayList("notesArray") //Get notes

        setSupportActionBar(binding.searchToolbar)
        supportActionBar!!.title = ""

        setupRecycler(ArrayList<ItemsHolder>())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu!!.findItem(R.id.search)

        val searchView: SearchView = (menu.findItem(R.id.search).actionView as SearchView)
        searchView.queryHint = getString(R.string.search_hint) //Set search hint

        searchView.setOnQueryTextListener(this)
        //searchView.requestFocus()
        searchItem.expandActionView()

        return true
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        makeFilter(newText!!)
        return true
    }

    //Setup recycler
    private fun setupRecycler(array: ArrayList<ItemsHolder>?) {
        val recycler = binding.searchRecycler
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = SlideInRightAnimator()
        myRecycler = SearchResultRecycler(array!!, this, binding.searchRecycler)
        binding.searchRecycler.adapter = myRecycler
    }

    private fun makeFilter(query: String?) {
        val filterArray = ArrayList<ItemsHolder>()

        if (query != "" && query != null) {
            for (x in 0 until noteObjectsArray.size) {
                if (noteObjectsArray[x].title.toLowerCase().contains(query.toLowerCase()) ||
                        noteObjectsArray[x].note.toLowerCase().contains(query.toLowerCase()))
                    filterArray.add(noteObjectsArray[x])
            }
        }
        myRecycler.setFilter(filterArray)
    }
}
