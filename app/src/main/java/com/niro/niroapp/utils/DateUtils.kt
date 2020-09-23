package com.niro.niroapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getDateString(date: Date?, postDateFormat: String): String? {
        val simpleDateFormat = SimpleDateFormat(postDateFormat, Locale.ENGLISH)
        date?.let { return simpleDateFormat.format(it) }
        return null
    }

    fun convertDate(dateString: String?, oldFormat: String, newFormat: String): String? {
        return try {
            val oldDateFormat = SimpleDateFormat(oldFormat, Locale.ENGLISH)
            val newDateFormat = SimpleDateFormat(newFormat, Locale.ENGLISH)
            val date = oldDateFormat.parse(dateString)
            date?.let { newDateFormat.format(date) }
        } catch (exception: Exception) {
            ""
        }
    }

    fun getDate(dateString : String?, format : String) : Date? {

        if(dateString.isNullOrEmpty()) return Date()
        return SimpleDateFormat(format, Locale.ENGLISH).parse(dateString)
    }
}