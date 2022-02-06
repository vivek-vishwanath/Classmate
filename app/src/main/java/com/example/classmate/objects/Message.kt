package com.example.classmate.objects

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "messages")
data class Message(@ColumnInfo(name = "text") val text: String,
                   @ColumnInfo(name = "sender") val senderID: String,
                   @ColumnInfo(name = "sender") val senderName: String,
                   @ColumnInfo(name = "recipient") val forumID: String
                   ) : Parcelable {

    @ColumnInfo(name = "timestamp")
    val date: Date = Date()

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    fun getTime(): String {
        return "${if(date.hours % 12 == 0) 12 else date.hours}:" +
                "${date.minutes} ${if(date.hours < 12) "AM" else "PM"}"
    }

    override fun toString(): String {
        return text
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(senderID)
        parcel.writeString(senderName)
        parcel.writeString(forumID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {

        fun from(map: Map<String, Any>) = object {
            val text: String by map
            val senderID: String by map
            val senderName: String by map
            val forumID: String by map
            val data = Message(text, senderID, senderName, forumID)
        }.data

        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }

}
