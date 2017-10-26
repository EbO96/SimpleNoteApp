package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

import android.os.Parcel
import android.os.Parcelable


open class NoteItem(var id: String, var title: String, var note: String, var date: String?, var rBGColor: Int,
                    var gBGColor: Int, var bBGColor: Int, var rTXTColor: Int, var gTXTColor: Int, var bTXTColor: Int,
                    var fontStyle: String, var isDeleted: Boolean) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(note)
        parcel.writeString(date)
        parcel.writeInt(rBGColor)
        parcel.writeInt(gBGColor)
        parcel.writeInt(bBGColor)
        parcel.writeInt(rTXTColor)
        parcel.writeInt(gTXTColor)
        parcel.writeInt(bTXTColor)
        parcel.writeString(fontStyle)
        parcel.writeByte(if (isDeleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteItem> {
        override fun createFromParcel(parcel: Parcel): NoteItem {
            return NoteItem(parcel)
        }

        override fun newArray(size: Int): Array<NoteItem?> {
            return arrayOfNulls(size)
        }
    }

}