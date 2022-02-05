package com.example.classmate.objects

import java.util.*

data class Post(val uid: String, val message: String, val group: Forum, val date: Date) {

    fun getTime(): String {
        return date.toString().substring(11, 16)
    }
}