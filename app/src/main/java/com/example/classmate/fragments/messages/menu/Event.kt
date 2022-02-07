package com.example.classmate.fragments.messages.menu;

import com.example.classmate.fragments.profile.Course
import com.google.firebase.Timestamp
import java.io.*
import java.util.*

data class Event(val name: String, val forumID: String, val description: String, val location: String, val from: Date, val to: Date) : Serializable, Comparable<Event> {

    companion object {

        fun from(map: Map<String, Any>) = object {
            val forumID: String by map
            val name: String by map
            val description: String by map
            val location: String by map
            val from: Timestamp by map
            val to: Timestamp by map
            val data = Event(name, forumID, description, location, Date(from.seconds * 1000), Date(to.seconds * 1000))
        }.data

        fun deserialize(string: String): Event {
            val bytes: ByteArray = Base64.getDecoder().decode(string.toByteArray())
            val inStream = ByteArrayInputStream(bytes)
            val objStream = ObjectInputStream(inStream)
            return objStream.readObject() as Event
        }
    }

    fun serialize(): String {
        val stream = ByteArrayOutputStream()
        val objStream = ObjectOutputStream(stream)
        objStream.writeObject(this)
        objStream.flush()
        val bytes: ByteArray = Base64.getEncoder().encode(stream.toByteArray())
        return String(bytes)
    }

    override fun compareTo(other: Event): Int {
        return from.compareTo(other.from)
    }
}
