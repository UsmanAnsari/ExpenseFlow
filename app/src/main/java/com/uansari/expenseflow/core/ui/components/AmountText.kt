package com.uansari.expenseflow.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.uansari.expenseflow.core.ui.theme.ExpenseRed
import com.uansari.expenseflow.core.ui.theme.IncomeGreen
import com.uansari.expenseflow.core.util.CurrencyFormatter
import com.uansari.expenseflow.domain.model.TransactionType

@Composable
fun AmountText(
    amount: Double,
    type: TransactionType,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    showSign: Boolean = true,
    fontWeight: FontWeight? = null
) {
    val color = when (type) {
        TransactionType.INCOME -> IncomeGreen
        TransactionType.EXPENSE -> ExpenseRed
    }

    val formattedAmount = if (showSign) {
        CurrencyFormatter.formatWithSign(amount, type)
    } else {
        CurrencyFormatter.format(amount)
    }

    Text(
        text = formattedAmount,
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = fontWeight ?: style.fontWeight
    )
}