package com.uansari.expenseflow.core.util

import com.uansari.expenseflow.domain.model.TransactionType
import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    private val numberFormat: NumberFormat =
        NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

    fun format(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun formatNumber(amount: Double): String {
        return numberFormat.format(amount)
    }

    fun formatWithSign(amount: Double, type: TransactionType): String {
        val formatted = format(amount)
        return when (type) {
            TransactionType.INCOME -> "+$formatted"
            TransactionType.EXPENSE -> "-$formatted"
        }
    }

    fun formatForInput(amount: Double): String {
        return if (amount == 0.0) {
            ""
        } else {
            String.format(Locale.US, "%.2f", amount)
        }
    }

    fun parse(input: String): Double {
        return input.replace(",", "").replace("$", "").replace(" ", "").toDoubleOrNull() ?: 0.0
    }
}