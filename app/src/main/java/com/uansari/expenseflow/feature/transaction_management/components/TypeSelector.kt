package com.uansari.expenseflow.feature.transaction_management.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uansari.expenseflow.core.ui.theme.ExpenseRed
import com.uansari.expenseflow.core.ui.theme.ExpenseRedLight
import com.uansari.expenseflow.core.ui.theme.IncomeGreen
import com.uansari.expenseflow.core.ui.theme.IncomeGreenLight
import com.uansari.expenseflow.domain.model.TransactionType

@Composable
fun TypeSelector(
    selectedType: TransactionType,
    onTypeSelect: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TypeButton(
            type = TransactionType.EXPENSE,
            isSelected = selectedType == TransactionType.EXPENSE,
            onClick = { onTypeSelect(TransactionType.EXPENSE) },
            modifier = Modifier.weight(1f)
        )

        TypeButton(
            type = TransactionType.INCOME,
            isSelected = selectedType == TransactionType.INCOME,
            onClick = { onTypeSelect(TransactionType.INCOME) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TypeButton(
    type: TransactionType, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val (icon, label, selectedColor, selectedBgColor) = when (type) {
        TransactionType.EXPENSE -> listOf("💸", "Expense", ExpenseRed, ExpenseRedLight)

        TransactionType.INCOME -> listOf("💰", "Income", IncomeGreen, IncomeGreenLight)

    }

    val containerColor = if (isSelected) {
        selectedBgColor as androidx.compose.ui.graphics.Color
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        selectedColor as androidx.compose.ui.graphics.Color
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = if (isSelected) {
        selectedColor as androidx.compose.ui.graphics.Color
    } else {
        MaterialTheme.colorScheme.outline
    }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp, color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$icon $label",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
        }
    }
}