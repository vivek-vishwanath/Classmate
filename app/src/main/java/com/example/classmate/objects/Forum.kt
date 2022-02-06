package com.example.classmate.objects

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class Forum(val id: String, val name: String, val description: String, val privacy: Boolean, val users: ArrayList<User>) : Serializable {

    constructor(id: String, name: String, description: String, privacy: Boolean, user: User) :
            this(id, name, description, privacy, ArrayList(listOf(user)))

    companion object {

        fun from(map: Map<String, Any>) = object {
            val id: String by map
            val name: String by map
            val description: String by map
            val privacy: Boolean by map
            val users: ArrayList<User> by map
            val data = Forum(id, name, description, privacy, users)
        }.data
    }
}