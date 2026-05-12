package com.dresta0056.whowantcoffee.util

fun ratingStars(rating: Int): String {
    val safeRating = rating.coerceIn(0, 5)
    return "★".repeat(safeRating) + "☆".repeat(5 - safeRating)
}

fun relativeDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val day = 1000L * 60 * 60 * 24

    return when {
        diff < day -> "Today"
        diff < day * 2 -> "Yesterday"
        diff < day * 7 -> "${diff / day} days ago"
        diff < day * 28 -> "${diff / (day * 7)} weeks ago"
        diff < day * 365 -> "${diff / (day * 30)} months ago"
        else -> "Over a year ago"
    }
}