package com.uansari.expenseflow.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.uansari.expenseflow.domain.model.TransactionDom

@Composable
fun TransactionCard(
    transaction: TransactionDom, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            if (transaction.category != null) {
                CategoryIcon(
                    category = transaction.category, size = 44.dp
                )
            } else {
                PlaceholderCategoryIcon(size = 44.dp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Category name and note
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.category?.name ?: "Uncategorized",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!transaction.note.isNullOrBlank()) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Amount and account
            Column(
                horizontalAlignment = Alignment.End
            ) {
                AmountText(
                    amount = transaction.amount,
                    type = transaction.type,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (transaction.account != null) {
                    Text(
                        text = "${transaction.account.icon} ${transaction.account.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Compact version for dashboard recent transactions.
 * Shows less detail, optimized for smaller space.
 */
@Composable
fun TransactionCardCompact(
    transaction: TransactionDom, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon (smaller)
        if (transaction.category != null) {
            CategoryIcon(
                category = transaction.category, size = 36.dp
            )
        } else {
            PlaceholderCategoryIcon(size = 36.dp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Category name
        Text(
            text = transaction.category?.name ?: "Uncategorized",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Amount
        AmountText(
            amount = transaction.amount,
            type = transaction.type,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}