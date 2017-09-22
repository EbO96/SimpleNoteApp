package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

import android.os.Parcel
import android.os.Parcelable


class ItemsHolder(var id: String, var title: String, var note: String, var date: String, var bgColor: String, var textColor: String,
                  var fontStyle: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(note)
        parcel.writeString(date)
        parcel.writeString(bgColor)
        parcel.writeString(textColor)
        parcel.writeString(fontStyle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemsHolder> {
        override fun createFromParcel(parcel: Parcel): ItemsHolder {
            return ItemsHolder(parcel)
        }

        override fun newArray(size: Int): Array<ItemsHolder?> {
            return arrayOfNulls(size)
        }
    }


}