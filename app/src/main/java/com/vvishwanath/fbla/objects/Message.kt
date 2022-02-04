package com.vvishwanath.fbla.objects

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "messages")
data class Message(@ColumnInfo(name = "text") val text: String,
                   @ColumnInfo(name = "sender") val senderID: String,
                   @ColumnInfo(name = "recipient") val recipientID: String
                   ) : Parcelable {

    @ColumnInfo(name = "timestamp")
    val date: Date = Date()

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun toString(): String {
        return text
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(senderID)
        parcel.writeString(recipientID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {

        fun from(map: Map<String, Any>) = object {
            val text: String by map
            val senderID: String by map
            val recipientID: String by map
            val data = Message(text, senderID, recipientID)
        }.data

        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }

}
