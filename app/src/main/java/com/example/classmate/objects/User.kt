package com.example.classmate.objects

import kotlin.collections.ArrayList

data class User(
    val firstName: String, val lastName: String, val email: String, val school: String,
    val grade: Int, val contacts: ArrayList<String>, val courses: ArrayList<Course>,
    val forums: ArrayList<Forum>
) {

    constructor(firstName: String, lastName: String, email: String, school: String, grade: Int) :
            this(firstName, lastName, email, school, grade, ArrayList(), ArrayList(), ArrayList())

    companion object {

        fun from(map: Map<String, Any>) = object {
            val firstName: String by map
            val lastName: String by map
            val email: String by map
            val school: String by map
            val grade: Int by map
            val contacts: ArrayList<String> by map
            val courses: ArrayList<Course> by map
            val forums: ArrayList<Forum> by map
            val data = User(firstName, lastName, email, school, grade, contacts, courses, forums)
        }.data
    }

    fun add(contact: String) {
        contacts.add(contact)
    }

    fun getName(): String {
        return "$firstName $lastName"
    }

    override fun toString(): String {
        return "${getName()}: $email"
    }
}
