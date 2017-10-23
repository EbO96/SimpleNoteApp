package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

import android.content.Context
import app.note.simple.brulinski.sebastian.com.simplenoteapp.R

class ColorsModel(private val ctx: Context) {

    val listViewColorImages = arrayOf(R.mipmap.red_circle, R.mipmap.pink_circle, R.mipmap.purple_circle,
            R.mipmap.blue_circle, R.mipmap.indigo_circle, R.mipmap.green_circle, R.mipmap.teal_circle, R.mipmap.yellow_circle,
            R.mipmap.white_circle, R.mipmap.blue_grey_circle, R.mipmap.black_circle, R.mipmap.brown_circle)

    val listViewColorNames = arrayOf(ctx.getString(R.string.red), ctx.getString(R.string.pink), ctx.getString(R.string.purple),
            ctx.getString(R.string.blue), ctx.getString(R.string.indigo), ctx.getString(R.string.green), ctx.getString(R.string.teal),
            ctx.getString(R.string.yellow), ctx.getString(R.string.white), ctx.getString(R.string.blue_grey), ctx.getString(R.string.black),
            ctx.getString(R.string.brown))


    fun getArrays(): ArrayList<HashMap<String, String>> {
        val aList = ArrayList<HashMap<String, String>>()

        for (x in 0 until listViewColorImages.size) {
            val hashMap = HashMap<String, String>()
            hashMap.put("color_image", listViewColorImages[x].toString())
            hashMap.put("color_name", listViewColorNames[x])
            aList.add(hashMap)
        }

        return aList
    }
}