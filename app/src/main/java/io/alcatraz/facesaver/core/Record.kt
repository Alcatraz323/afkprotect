package io.alcatraz.facesaver.core

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

class Record(val startTime: Long) : Parcelable {
    var endTime: Long = 0
    var screenOnTimes = 0

    constructor(parcel: Parcel) : this(parcel.readLong()) {
        endTime = parcel.readLong()
        screenOnTimes = parcel.readInt()
    }

    fun addScreenOnT() {
        screenOnTimes++
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(startTime)
        parcel.writeLong(endTime)
        parcel.writeInt(screenOnTimes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }

        fun convertTime(millis: Long): String {
            @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("MM-dd HH:mm:ss")
            val date = Date(millis)
            return formatter.format(date)
        }
    }
}