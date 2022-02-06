package com.example.classmate.fragments.messages.menu;

import java.util.*
import kotlin.collections.HashMap

data class Event(val name: String, val forumID: String, val description: String, val location: String, val from: Date, val to: Date) {

    fun getMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["name"] = name
        map["forumID"] = forumID
        map["description"] = description
        map["location"] = location
        map["from"] = from
        map["to"] = to
        return map
    }
}
