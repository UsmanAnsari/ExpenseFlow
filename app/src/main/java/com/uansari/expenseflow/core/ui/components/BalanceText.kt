package com.uansari.expenseflow.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.uansari.expenseflow.core.util.CurrencyFormatter

@Composable
fun BalanceText(
    amount: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = CurrencyFormatter.format(amount),
        modifier = modifier,
        style = style,
        fontWeight = fontWeight
    )
}