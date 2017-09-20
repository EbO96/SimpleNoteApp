package app.note.simple.brulinski.sebastian.com.simplenoteapp.Model

import android.os.Parcel
import android.os.Parcelable

class ItemsHolder() : Parcelable {

    var title: String = ""
    var note: String = ""
    var date: String = ""
        get() = field
        set(value) {
            field = value
        }

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        note = parcel.readString()
    }

    constructor(title: String, note: String, date: String) : this() {
        this.title = title
        this.note = note
        this.date = date
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(note)
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