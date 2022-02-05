package com.example.classmate.objects

data class Forum(val uid: String, val name: String, val description: String, val users: ArrayList<User>) {

    companion object {

        fun from(map: Map<String, Any>) = object {
            val uid: String by map
            val name: String by map
            val description: String by map
            val users: ArrayList<User> by map
            val data = Forum(uid, name, description, users)
        }.data
    }
}