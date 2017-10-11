package app.note.simple.brulinski.sebastian.com.simplenoteapp.RecyclerView

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemsHolder

class ArchivesRecycler(val notesArrayList: ArrayList<ItemsHolder>, recycler: RecyclerView, ctx: Context) : RecyclerView.Adapter<ArchivesRecycler.MyViewHolder>() {
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
    }
}