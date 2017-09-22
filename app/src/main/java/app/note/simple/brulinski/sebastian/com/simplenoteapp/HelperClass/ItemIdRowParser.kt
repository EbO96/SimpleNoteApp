package app.note.simple.brulinski.sebastian.com.simplenoteapp.HelperClass

import app.note.simple.brulinski.sebastian.com.simplenoteapp.Model.ItemID
import org.jetbrains.anko.db.MapRowParser

/**
 * Created by sebas on 9/22/2017.
 */
class ItemIdRowParser : MapRowParser<List<ItemID>> {
    var idArrayList = ArrayList<ItemID>()

    override fun parseRow(columns: Map<String, Any?>): List<ItemID> {
        val id = columns.getValue("_id")

        idArrayList.add(ItemID(id!!))

        return idArrayList
    }
}