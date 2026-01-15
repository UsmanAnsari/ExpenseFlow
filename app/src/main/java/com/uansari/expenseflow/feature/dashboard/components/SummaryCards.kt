package com.uansari.expenseflow.feature.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uansari.expenseflow.core.ui.components.AmountText
import com.uansari.expenseflow.core.ui.theme.ExpenseRedLight
import com.uansari.expenseflow.core.ui.theme.IncomeGreenLight
import com.uansari.expenseflow.domain.model.TransactionType

@Composable
fun SummaryCards(
    income: Double, expense: Double, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Income",
            subtitle = "This Month",
            amount = income,
            type = TransactionType.INCOME,
            icon = "📈",
            backgroundColor = IncomeGreenLight,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Expenses",
            subtitle = "This Month",
            amount = expense,
            type = TransactionType.EXPENSE,
            icon = "📉",
            backgroundColor = ExpenseRedLight,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Individual summary card for income or expense.
 */
@Composable
fun SummaryCard(
    title: String,
    subtitle: String,
    amount: Double,
    type: TransactionType,
    icon: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "$icon $title",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            AmountText(
                amount = amount,
                type = type,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}