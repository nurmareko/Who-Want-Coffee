package com.dresta0056.whowantcoffee.util

import android.content.Context
import com.dresta0056.whowantcoffee.R

fun ratingStars(rating: Int): String {
    val safeRating = rating.coerceIn(0, 5)
    return "★".repeat(safeRating) + "☆".repeat(5 - safeRating)
}

fun relativeDate(context: Context, timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val day = 1000L * 60 * 60 * 24

    return when {
        diff < day -> context.getString(R.string.relative_today)
        diff < day * 2 -> context.getString(R.string.relative_yesterday)
        diff < day * 7 -> context.getString(R.string.relative_days_ago, (diff / day).toInt())
        diff < day * 28 -> context.getString(R.string.relative_weeks_ago, (diff / (day * 7)).toInt())
        diff < day * 365 -> context.getString(R.string.relative_months_ago, (diff / (day * 30)).toInt())
        else -> context.getString(R.string.relative_over_year)
    }
}

fun getProcessDisplayName(context: Context, process: String): String {
    return when (process) {
        "Washed" -> context.getString(R.string.process_washed)
        "Honey" -> context.getString(R.string.process_honey)
        "Natural" -> context.getString(R.string.process_natural)
        else -> process
    }
}
