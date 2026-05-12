package com.dresta0056.whowantcoffee.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dresta0056.whowantcoffee.R

fun ratingStars(rating: Int): String {
    val safeRating = rating.coerceIn(0, 5)
    return "★".repeat(safeRating) + "☆".repeat(5 - safeRating)
}

sealed class RelativeDateRes {
    data class Simple(val resId: Int) : RelativeDateRes()
    data class Quantity(val resId: Int, val value: Int) : RelativeDateRes()
}

fun getRelativeDateRes(timestamp: Long): RelativeDateRes {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val day = 1000L * 60 * 60 * 24

    return when {
        diff < day -> RelativeDateRes.Simple(R.string.relative_today)
        diff < day * 2 -> RelativeDateRes.Simple(R.string.relative_yesterday)
        diff < day * 7 -> RelativeDateRes.Quantity(R.string.relative_days_ago, (diff / day).toInt())
        diff < day * 28 -> RelativeDateRes.Quantity(R.string.relative_weeks_ago, (diff / (day * 7)).toInt())
        diff < day * 365 -> RelativeDateRes.Quantity(R.string.relative_months_ago, (diff / (day * 30)).toInt())
        else -> RelativeDateRes.Simple(R.string.relative_over_year)
    }
}

@Composable
fun relativeDateResource(timestamp: Long): String {
    return when (val res = getRelativeDateRes(timestamp)) {
        is RelativeDateRes.Simple -> stringResource(res.resId)
        is RelativeDateRes.Quantity -> stringResource(res.resId, res.value)
    }
}

@Deprecated("Use relativeDateResource instead", ReplaceWith("relativeDateResource(timestamp)"))
fun relativeDate(context: Context, timestamp: Long): String {
    return when (val res = getRelativeDateRes(timestamp)) {
        is RelativeDateRes.Simple -> context.getString(res.resId)
        is RelativeDateRes.Quantity -> context.getString(res.resId, res.value)
    }
}

@Deprecated("Use getProcessDisplayNameRes instead", ReplaceWith("getProcessDisplayNameRes(process)"))
fun getProcessDisplayName(context: Context, process: String): String {
    return getProcessDisplayNameRes(process)?.let { context.getString(it) } ?: process
}

fun getProcessDisplayNameRes(process: String): Int? {
    return when (process) {
        "Washed" -> R.string.process_washed
        "Honey" -> R.string.process_honey
        "Natural" -> R.string.process_natural
        else -> null
    }
}
