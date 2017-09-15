package app.note.simple.brulinski.sebastian.com.simplenoteapp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuInflater
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var itemsObject: ItemsHolder
    lateinit var itemsObjectsArray: ArrayList<ItemsHolder>
    lateinit var myRecycler: MainRecyclerAdapter
    lateinit var database: LocalDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        itemsObjectsArray = ArrayList()

        myRecycler = MainRecyclerAdapter(itemsObjectsArray)
        binding.recyclerView.adapter = myRecycler

        database = LocalDatabase(this)


    }

    private fun addNote(title: String, note: String) {
        itemsObject = ItemsHolder()

        itemsObject.title = title
        itemsObject.note = note

        itemsObjectsArray.add(itemsObject)

        myRecycler.notifyDataSetChanged()

    }

    private fun saveNoteInDatabase(title: String, note: String, date: String) {
        database.addNote(title, note, date)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }
}
