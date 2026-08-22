package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val idLocale = Locale("id", "ID")

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentMonthString(): String {
        val sdf = SimpleDateFormat("MM", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentYearString(): String {
        val sdf = SimpleDateFormat("yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun formatIndonesianDate(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateStr) ?: return dateStr
            val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", idLocale)
            formatter.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getMonthName(monthCode: String): String {
        return when (monthCode) {
            "01" -> "Januari"
            "02" -> "Februari"
            "03" -> "Maret"
            "04" -> "April"
            "05" -> "Mei"
            "06" -> "Juni"
            "07" -> "Juli"
            "08" -> "Agustus"
            "09" -> "September"
            "10" -> "Oktober"
            "11" -> "November"
            "12" -> "Desember"
            else -> "Semua Bulan"
        }
    }

    val monthsList = listOf(
        "ALL" to "Semua Bulan",
        "01" to "Januari (01)",
        "02" to "Februari (02)",
        "03" to "Maret (03)",
        "04" to "April (04)",
        "05" to "Mei (05)",
        "06" to "Juni (06)",
        "07" to "Juli (07)",
        "08" to "Agustus (08)",
        "09" to "September (09)",
        "10" to "Oktober (10)",
        "11" to "November (11)",
        "12" to "Desember (12)"
    )
}
