package com.uansari.expenseflow.feature.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uansari.expenseflow.core.util.CurrencyFormatter
import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.feature.accounts.components.AccountFormDialog
import com.uansari.expenseflow.feature.accounts.components.AccountListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ════════════════════════════════════════════════════════════════
    // Handle Events
    // ════════════════════════════════════════════════════════════════

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AccountsEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Main Scaffold
    // ════════════════════════════════════════════════════════════════

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddAccountClick() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ════════════════════════════════════════════════════════
            // Content States
            // ════════════════════════════════════════════════════════

            when {
                uiState.isLoading -> {
                    LoadingState(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.isEmpty -> {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    AccountsList(
                        accounts = uiState.accounts,
                        totalBalance = uiState.totalBalance,
                        onAccountClick = { viewModel.onEditAccountClick(it) },
                        onDeleteClick = { viewModel.onDeleteAccountClick(it) }
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Account Form Dialog
    // ════════════════════════════════════════════════════════════════

    if (uiState.showDialog) {
        val editAccount = (uiState.dialogState as? AccountDialogState.Edit)?.account

        AccountFormDialog(
            account = editAccount,
            title = uiState.dialogTitle,
            isEditMode = uiState.isEditMode,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.onDismissDialog() },
            onSave = { name, icon, type, color, initialBalance, isDefault ->
                viewModel.onSaveAccount(
                    name = name,
                    icon = icon,
                    type = type,
                    color = color,
                    initialBalance = initialBalance,
                    isDefault = isDefault
                )
            }
        )
    }

    // ════════════════════════════════════════════════════════════════
    // Delete Confirmation Dialog
    // ════════════════════════════════════════════════════════════════

    if (uiState.showDeleteConfirm) {
        DeleteAccountDialog(
            accountName = uiState.accountToDelete?.name ?: "",
            onDismiss = { viewModel.onDismissDeleteConfirm() },
            onConfirm = { viewModel.onConfirmDelete() }
        )
    }
}

// ════════════════════════════════════════════════════════════════════════
// Accounts List with Header
// ════════════════════════════════════════════════════════════════════════

@Composable
private fun AccountsList(
    accounts: List<AccountDom>,
    totalBalance: Double,
    onAccountClick: (AccountDom) -> Unit,
    onDeleteClick: (AccountDom) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ──────────────────────────────────────────────────────────
        // Total Balance Header
        // ──────────────────────────────────────────────────────────
        item {
            TotalBalanceCard(totalBalance = totalBalance)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ──────────────────────────────────────────────────────────
        // Accounts Count
        // ──────────────────────────────────────────────────────────
        item {
            Text(
                text = "${accounts.size} Account${if (accounts.size != 1) "s" else ""}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ──────────────────────────────────────────────────────────
        // Account Items
        // ──────────────────────────────────────────────────────────
        items(
            items = accounts,
            key = { it.id }
        ) { account ->
            AccountListItem(
                account = account,
                onClick = { onAccountClick(account) },
                onDelete = { onDeleteClick(account) }
            )
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Total Balance Card
// ════════════════════════════════════════════════════════════════════════

@Composable
private fun TotalBalanceCard(totalBalance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CurrencyFormatter.format(totalBalance),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (totalBalance >= 0) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// State Components
// ════════════════════════════════════════════════════════════════════════

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading accounts...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏦",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Accounts Yet",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to add your first account\nand start tracking your finances",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ════════════════════════════════════════════════════════════════════════
// Delete Confirmation Dialog
// ════════════════════════════════════════════════════════════════════════

@Composable
private fun DeleteAccountDialog(
    accountName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🗑️", style = MaterialTheme.typography.headlineMedium)
        },
        title = {
            Text("Delete Account?")
        },
        text = {
            Text(
                text = "Are you sure you want to delete \"$accountName\"?\n\nThis action cannot be undone."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}