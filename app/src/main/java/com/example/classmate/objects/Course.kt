package com.example.classmate.objects

import javax.annotation.Nullable

data class Course(@Nullable var name: String, @Nullable var field: Field, @Nullable var gradeAvg: Int, @Nullable var difficulty: Difficulty) {


    enum class Field {

        MATH, SCIENCE, ENGLISH, SOCIAL_STUDIES, WORLD_LANGUAGE, BUSINESS, ARTS, NULL;

        companion object {

            fun getField(name: String): Field {
                when(name) {
                    "Math" -> return MATH
                    "Science" -> return SCIENCE
                    "English" -> return ENGLISH
                    "Social Studies" -> return SOCIAL_STUDIES
                    "World Language" -> return WORLD_LANGUAGE
                    "Business" -> return BUSINESS
                    "Arts" -> return ARTS
                }
                return NULL
            }

        }
    }

    enum class Difficulty {

        SUPPORT, ON_LEVEL, HONORS, ACCELERATED, AP, IB
    }

    companion object {

        final var OTHER: Course = Course("Other", Field.NULL, 100, Difficulty.ON_LEVEL)
    }
}
