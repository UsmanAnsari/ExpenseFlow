package com.uansari.expenseflow.feature.transaction_management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.expenseflow.core.ui.components.DeleteTransactionDialog
import com.uansari.expenseflow.core.ui.components.LoadingScreen
import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.feature.transaction_management.components.AccountPicker
import com.uansari.expenseflow.feature.transaction_management.components.AmountInput
import com.uansari.expenseflow.feature.transaction_management.components.CategoryPicker
import com.uansari.expenseflow.feature.transaction_management.components.DatePickerButton
import com.uansari.expenseflow.feature.transaction_management.components.NoteInput
import com.uansari.expenseflow.feature.transaction_management.components.TransactionDatePickerDialog
import com.uansari.expenseflow.feature.transaction_management.components.TypeSelector
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun TransactionManagementScreen(
    onNavigateBack: () -> Unit = {}, viewModel: TransactionManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionManagementEvent.NavigateBack -> {
                    onNavigateBack()
                }

                is TransactionManagementEvent.TransactionSaved -> {
                    snackbarHostState.showSnackbar("Transaction saved")
                }

                is TransactionManagementEvent.TransactionDeleted -> {
                    snackbarHostState.showSnackbar("Transaction deleted")
                }

                is TransactionManagementEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(uiState.screenTitle) }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                )
            }
        }, actions = {
            // Delete button (edit mode only)
            if (uiState.isEditMode) {
                IconButton(
                    onClick = viewModel::onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Save button
            IconButton(
                onClick = viewModel::onSaveClick, enabled = uiState.canSave
            ) {
                Icon(
                    imageVector = Icons.Default.Check, contentDescription = "Save"
                )
            }
        })
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else {
                TransactionManagementContent(
                    uiState = uiState,
                    onAmountChange = viewModel::onAmountChange,
                    onTypeChange = viewModel::onTypeChange,
                    onCategorySelect = viewModel::onCategorySelect,
                    onAccountSelect = viewModel::onAccountSelect,
                    onDateClick = viewModel::onShowDatePicker,
                    onNoteChange = viewModel::onNoteChange,
                    onSaveClick = viewModel::onSaveClick
                )
            }

            // Saving overlay
            if (uiState.isSaving) {
                LoadingScreen()
            }
        }
    }

    // Date picker dialog
    if (uiState.showDatePicker) {
        TransactionDatePickerDialog(
            initialDate = uiState.date,
            onDateSelected = viewModel::onDateSelect,
            onDismiss = viewModel::onDismissDatePicker
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
private fun TransactionManagementContent(
    uiState: TransactionManagementUiState,
    onAmountChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,

    onCategorySelect: (CategoryDom) -> Unit,

    onAccountSelect: (AccountDom) -> Unit,
    onDateClick: () -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Amount input (large, prominent)
        AmountInput(
            amount = uiState.amount, onAmountChange = onAmountChange, error = uiState.amountError
        )

        // Type selector (Income/Expense)
        TypeSelector(
            selectedType = uiState.type, onTypeSelect = onTypeChange
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Category picker
        CategoryPicker(
            categories = uiState.categories,
            selectedCategory = uiState.selectedCategory,
            onCategorySelect = onCategorySelect,
            error = uiState.categoryError
        )

        // Account picker
        AccountPicker(
            accounts = uiState.accounts,
            selectedAccount = uiState.selectedAccount,
            onAccountSelect = onAccountSelect,
            error = uiState.accountError
        )

        // Date picker
        DatePickerButton(
            date = uiState.date, onClick = onDateClick
        )

        // Note input
        NoteInput(
            note = uiState.note, onNoteChange = onNoteChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.canSave
        ) {
            Text(
                text = if (uiState.isEditMode) "Update Transaction" else "Save Transaction",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(32.dp))
    }
}