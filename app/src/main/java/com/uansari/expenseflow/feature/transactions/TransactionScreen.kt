package com.uansari.expenseflow.feature.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.expenseflow.core.ui.components.DeleteTransactionDialog
import com.uansari.expenseflow.core.ui.components.EmptyState
import com.uansari.expenseflow.core.ui.components.LoadingScreen
import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.feature.transactions.components.FilterChips
import com.uansari.expenseflow.feature.transactions.components.TransactionsList
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TransactionsScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Long) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionsEvent.NavigateToAddTransaction -> {
                    onNavigateToAddTransaction()
                }

                is TransactionsEvent.NavigateToEditTransaction -> {
                    onNavigateToEditTransaction(event.transactionId)
                }

                is TransactionsEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message, actionLabel = event.actionLabel
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        TransactionsContent(
            uiState = uiState,
            onFilterChange = viewModel::onFilterChange,
            onTransactionClick = viewModel::onTransactionClick,
            onDeleteTransaction = viewModel::onDeleteTransaction,
            onAddTransactionClick = viewModel::onAddTransactionClick,
            modifier = Modifier.padding(paddingValues)
        )
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirm) {
        DeleteTransactionDialog(
            onConfirm = viewModel::onConfirmDelete, onDismiss = viewModel::onDismissDeleteConfirm
        )
    }
}

@Composable
private fun TransactionsContent(
    uiState: TransactionsUiState,
    onFilterChange: (TransactionFilter) -> Unit,
    onTransactionClick: (Long) -> Unit,
    onDeleteTransaction: (TransactionDom) -> Unit,

    onAddTransactionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Filter chips
        FilterChips(
            selectedFilter = uiState.selectedFilter,
            onFilterChange = onFilterChange,
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp
            )
        )

        // Content based on state
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }

            uiState.error != null -> {
                ErrorContent(error = uiState.error)
            }

            uiState.isEmpty -> {
                EmptyTransactionsContent(
                    filterLabel = uiState.filterLabel, onAddTransactionClick = onAddTransactionClick
                )
            }

            else -> {
                TransactionsList(
                    groupedTransactions = uiState.groupedTransactions,
                    onTransactionClick = onTransactionClick,
                    onDeleteTransaction = onDeleteTransaction
                )
            }
        }
    }
}

@Composable
private fun EmptyTransactionsContent(
    filterLabel: String, onAddTransactionClick: () -> Unit, modifier: Modifier = Modifier
) {
    val (title, description) = if (filterLabel == "all") {
        "No transactions yet" to "Add your first transaction to start tracking your expenses."
    } else {
        "No $filterLabel transactions" to "You don't have any $filterLabel transactions yet."
    }

    EmptyState(
        icon = Icons.Outlined.Receipt,
        title = title,
        description = description,
        actionLabel = if (filterLabel == "all") "Add Transaction" else null,
        onAction = if (filterLabel == "all") onAddTransactionClick else null,
        modifier = modifier
    )
}

@Composable
private fun ErrorContent(
    error: String, modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Outlined.Receipt,
        title = "Something went wrong",
        description = error,
        modifier = modifier
    )
}