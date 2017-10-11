package app.note.simple.brulinski.sebastian.com.simplenoteapp.Activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.LocalSQLAnkoDatabase
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Database.database
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNoteProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass.MyRowParserNotes
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.NotesProperties
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R
import app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView.ArchivesRecycler
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.ActivityArchivesBinding
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import org.jetbrains.anko.db.select

class ArchivesActivity : AppCompatActivity() {

    private lateinit var myRecycler: ArchivesRecycler
    private lateinit var binding: ActivityArchivesBinding
    private lateinit var archivedNotesArrayList: ArrayList<ItemsHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_archives)

        getArchivedNotes()
        initRecycler()
    }

    private fun initRecycler() {
        val recycler = binding.archivesRecycler
        recycler.itemAnimator = SlideInRightAnimator()
        recycler.layoutManager = LinearLayoutManager(this)

        myRecycler = ArchivesRecycler(archivedNotesArrayList, recycler, this)

        recycler.adapter = myRecycler
    }

    private fun getArchivedNotes() {
        archivedNotesArrayList = ArrayList()
        val propertiesArray = ArrayList<NotesProperties>()

        database.use {
            val properties = select(LocalSQLAnkoDatabase.TABLE_NOTES_PROPERTIES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", "true").
                    parseList(MyRowParserNoteProperties())

            val size = properties.size

            for (x in 0 until size) {
                val item = properties[x].get(x)
                propertiesArray.add(NotesProperties(item.id, item.bgColor, item.textColor, item.fontStyle))
            }
        }
        database.use {
            val notes = select(LocalSQLAnkoDatabase.TABLE_NOTES).whereSimple("${LocalSQLAnkoDatabase.IS_DELETED}=?", "true").
                    parseList(MyRowParserNotes())
            val size = notes.size

            for (x in 0 until size) {
                val item = notes[x].get(x)
                val noteObject = ItemsHolder(item.id!!, item.title!!, item.note!!, item.date!!, propertiesArray[x].bgColor.toString(),
                        propertiesArray[x].textColor.toString(), propertiesArray[x].fontStyle.toString())
                archivedNotesArrayList.add(noteObject)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }
}
