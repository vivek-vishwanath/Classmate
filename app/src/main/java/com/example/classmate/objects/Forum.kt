package com.example.classmate.objects

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class Forum(val uid: String, val name: String, val description: String, val privacy: Boolean, val users: ArrayList<User>) : Serializable {

    constructor(uid: String, name: String, description: String, privacy: Boolean, user: User) :
            this(uid, name, description, privacy, ArrayList(listOf(user)))

    companion object {

        fun from(map: Map<String, Any>) = object {
            val uid: String by map
            val name: String by map
            val description: String by map
            val privacy: Boolean by map
            val users: ArrayList<User> by map
            val data = Forum(uid, name, description, privacy, users)
        }.data
    }
}