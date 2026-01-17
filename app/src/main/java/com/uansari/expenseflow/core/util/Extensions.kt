package com.uansari.expenseflow.core.util

import androidx.compose.ui.graphics.Color
import com.uansari.expenseflow.domain.model.TransactionDom

fun List<TransactionDom>.groupByDate(): Map<String, List<TransactionDom>> {
    return this.sortedByDescending { it.date }  // Most recent first
        .groupBy { transaction ->
            DateFormatter.formatForGrouping(transaction.date)
        }
}


fun Long.toComposeColor(): Color {
    return Color(this)
}


fun Color.toLong(): Long {
    return (alpha * 255).toLong() shl 24 or (red * 255).toLong() shl 16 or (green * 255).toLong() shl 8 or (blue * 255).toLong()
}


fun <T> List<T>.getOrNull(index: Int): T? {
    return if (index in indices) this[index] else null
}