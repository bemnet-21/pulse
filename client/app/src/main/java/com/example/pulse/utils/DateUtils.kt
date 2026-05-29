package com.example.pulse.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    /**
     * Formats an ISO-8601 date string (e.g., "2023-10-27T12:00:00Z") 
     * into a more readable format like "Oct 27, 2023".
     */
    fun formatPublishedDate(isoDate: String?): String {
        if (isoDate.isNullOrBlank()) return ""
        return try {
            val odt = OffsetDateTime.parse(isoDate)
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
            odt.format(formatter)
        } catch (e: Exception) {
            isoDate
        }
    }
}
