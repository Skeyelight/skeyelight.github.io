package com.example.dailyweighttracker_kotlin.data.room

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Converts LocalDate to a string to be stored in the Database
class Converters {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE


    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let {
            LocalDate.parse(it, formatter)
        }
    }
}