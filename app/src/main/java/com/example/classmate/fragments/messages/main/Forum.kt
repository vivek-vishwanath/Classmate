package com.example.classmate.fragments.messages.main;

import com.example.classmate.R
import com.example.classmate.fragments.messages.menu.Event
import com.example.classmate.fragments.profile.User
import com.example.classmate.statics.Graphics
import java.io.Serializable
import kotlin.collections.ArrayList

data class Forum(val id: String, val name: String, val description: String, val privacy: Boolean, val users: ArrayList<User>, val events: ArrayList<Event>) : Serializable {

    val key = id.substring(0, 6)

    constructor(id: String, name: String, description: String, privacy: Boolean, user: User) :
            this(id, name, description, privacy, ArrayList(listOf(user)), ArrayList())

    companion object {

        fun from(map: Map<String, Any>) = object {
            val id: String by map
            val name: String by map
            val description: String by map
            val privacy: Boolean by map
            val users: ArrayList<User> by map
            val events: ArrayList<Event> by map
            val data = Forum(id, name, description, privacy, users, events)
        }.data
    }

    fun getDrawable(): Int {
        if(name[0] > 64.toChar() && name[0] < 91.toChar())
            return Graphics.PROFILE_PIC_LETTERS[name[0] - 65.toChar()]
        if(name[0] > 96.toChar() && name[0] < 123.toChar())
            return Graphics.PROFILE_PIC_LETTERS[name[0] - 97.toChar()]
        return R.drawable.account_circle_grey
    }
}