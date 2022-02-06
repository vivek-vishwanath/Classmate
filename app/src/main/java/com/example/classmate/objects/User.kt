package com.example.classmate.objects

import java.io.Serializable
import kotlin.collections.ArrayList

data class User(
    val firstName: String, val lastName: String, val email: String,
    val grade: Int, val courses: ArrayList<Course>, val forums: ArrayList<String>): Serializable {

    constructor(firstName: String, lastName: String, email: String, grade: Int) :
            this(firstName, lastName, email, grade, ArrayList(), ArrayList())

    companion object {

        fun from(map: Map<String, Any>) = object {
            val firstName: String by map
            val lastName: String by map
            val email: String by map
            val grade: Int by map
            val courses: ArrayList<Course> by map
            val forums: ArrayList<String> by map
            val data = User(firstName, lastName, email, grade, courses, forums)
        }.data
    }

    fun addTo(forum: String) {
        forums.add(forum);
    }

    fun load(courses: ArrayList<Course>) {
        this.courses.addAll(courses)
    }

    fun getName(): String {
        return "$firstName $lastName"
    }

    override fun toString(): String {
        return "${getName()}: $email"
    }
}
