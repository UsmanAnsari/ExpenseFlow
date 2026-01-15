package com.uansari.expenseflow.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.expenseflow.core.ui.components.EmptyState
import com.uansari.expenseflow.core.ui.components.LoadingScreen
import com.uansari.expenseflow.feature.dashboard.components.BalanceCard
import com.uansari.expenseflow.feature.dashboard.components.RecentTransactionsList
import com.uansari.expenseflow.feature.dashboard.components.SpendingOverview
import com.uansari.expenseflow.feature.dashboard.components.SummaryCards
import kotlinx.coroutines.flow.collectLatest

@Composable

fun DashboardScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAllTransactions: () -> Unit,
    onNavigateToTransactionDetail: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is DashboardEvent.NavigateToAddTransaction -> {
                    onNavigateToAddTransaction()
                }

                is DashboardEvent.NavigateToAllTransactions -> {
                    onNavigateToAllTransactions()
                }

                is DashboardEvent.NavigateToTransactionDetail -> {
                    onNavigateToTransactionDetail(event.transactionId)
                }
            }
        }
    }

    DashboardContent(
        uiState = uiState,
        onSeeAllTransactionsClick = viewModel::onSeeAllTransactionsClick,
        onTransactionClick = viewModel::onTransactionClick,
        onAddTransactionClick = viewModel::onAddTransactionClick,
        onRetry = viewModel::onRetry
    )
}

/**
 * Dashboard content - stateless composable for easier testing/preview.
 */
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onSeeAllTransactionsClick: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    onAddTransactionClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        // Loading state
        uiState.isLoading -> {
            LoadingScreen()
        }

        // Error state
        uiState.error != null -> {
            ErrorContent(
                error = uiState.error, onRetry = onRetry
            )
        }

        // Success state
        else -> {
            DashboardMainContent(
                uiState = uiState,
                onSeeAllTransactionsClick = onSeeAllTransactionsClick,
                onTransactionClick = onTransactionClick,
                onAddTransactionClick = onAddTransactionClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DashboardMainContent(
    uiState: DashboardUiState,
    onSeeAllTransactionsClick: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    onAddTransactionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total Balance Card
        BalanceCard(
            balance = uiState.totalBalance
        )

        // Income / Expense Summary Cards
        SummaryCards(
            income = uiState.monthlyIncome, expense = uiState.monthlyExpense
        )

        // Recent Transactions
        if (uiState.hasTransactions) {
            RecentTransactionsList(
                transactions = uiState.recentTransactions,
                onSeeAllClick = onSeeAllTransactionsClick,
                onTransactionClick = onTransactionClick
            )
        } else {
            EmptyTransactionsCard(
                onAddTransactionClick = onAddTransactionClick
            )
        }

        // Spending by Category
        if (uiState.hasSpendingData) {
            SpendingOverview(
                spendingByCategory = uiState.spendingByCategory
            )
        }

        // Bottom spacing for FAB
        Spacer(modifier = Modifier.height(72.dp))
    }
}


@Composable
private fun EmptyTransactionsCard(
    onAddTransactionClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Receipt,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No transactions yet", style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Add your first transaction to start tracking",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onAddTransactionClick) {
                Text(text = "Add Transaction")
            }
        }
    }
}


@Composable
private fun ErrorContent(
    error: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Outlined.Receipt,
        title = "Something went wrong",
        description = error,
        actionLabel = "Retry",
        onAction = onRetry,
        modifier = modifier
    )
}