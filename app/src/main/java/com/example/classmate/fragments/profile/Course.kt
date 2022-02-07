package com.example.classmate.fragments.profile

import android.graphics.Color
import java.io.*
import java.util.*
import javax.annotation.Nullable
import kotlin.collections.ArrayList

data class Course(@Nullable var name: String, @Nullable var field: String, @Nullable var teacher: String?, @Nullable var period: Int): Serializable {

    companion object {

        val OTHER: Course = Course("Other", "Other", null, -1)

        fun from(map: Map<String, Any>) = object {
            val name: String by map
            val field: String by map
            val teacher: String by map
            val period: Int by map
            val data = Course(name, field, teacher, period)
        }.data

        fun serialize(courses: ArrayList<Course>): String {
            val stream = ByteArrayOutputStream()
            val objStream = ObjectOutputStream(stream)
            objStream.writeObject(courses)
            objStream.flush()
            val bytes: ByteArray = Base64.getEncoder().encode(stream.toByteArray())
            return String(bytes)
        }

        fun deserialize(string: String?): ArrayList<Course> {
            if(string == null) return ArrayList()
            val bytes: ByteArray = Base64.getDecoder().decode(string.toByteArray())
            val inStream = ByteArrayInputStream(bytes)
            val objStream = ObjectInputStream(inStream)
            val courses = objStream.readObject() as ArrayList<Course>?
            return courses ?: ArrayList()
        }
    }

    fun getColor(): Int {
        when(field) {
            "Math" -> return Color.BLUE
            "Science" -> return Color.GREEN
            "English" -> return Color.RED
            "Social Studies" -> return Color.YELLOW
            "World Language" -> return Color.rgb(0xAF, 0x00, 0xFF)
            "Business" -> return Color.rgb(0x3F, 0xFF, 0xDF)
            "Health" -> return Color.rgb(0xFF, 0x3F, 0xFF)
            "Music, Drama & Arts" -> return Color.rgb(0xFF, 0x7F, 0x00)
        }
        return Color.DKGRAY
    }
}
