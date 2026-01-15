package com.uansari.expenseflow.feature.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uansari.expenseflow.domain.model.TransactionDom

@Composable
fun TransactionsList(
    groupedTransactions: Map<String, List<TransactionDom>>,
    onTransactionClick: (Long) -> Unit,
    onDeleteTransaction: (TransactionDom) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedTransactions.forEach { (date, transactions) ->
            // Date header
            item(key = "header_$date") {
                TransactionDateHeader(
                    date = date,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Transactions for this date
            items(
                items = transactions,
                key = { it.id }
            ) { transaction ->
                SwipeableTransactionCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) },
                    onDelete = { onDeleteTransaction(transaction) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}