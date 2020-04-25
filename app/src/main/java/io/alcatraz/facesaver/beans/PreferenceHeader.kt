package io.alcatraz.facesaver.beans

import android.os.Parcel
import android.os.Parcelable

data class PreferenceHeader(val title: String?, val summary: String?,val icon_res: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(summary)
        parcel.writeInt(icon_res)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PreferenceHeader> {
        override fun createFromParcel(parcel: Parcel): PreferenceHeader {
            return PreferenceHeader(parcel)
        }

        override fun newArray(size: Int): Array<PreferenceHeader?> {
            return arrayOfNulls(size)
        }
    }
}
