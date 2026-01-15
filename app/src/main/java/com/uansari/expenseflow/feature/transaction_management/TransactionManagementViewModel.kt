package com.uansari.expenseflow.feature.transaction_management

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.usecase.account.GetAccountsUseCase
import com.uansari.expenseflow.domain.usecase.category.GetCategoriesUseCase
import com.uansari.expenseflow.domain.usecase.transaction.AddTransactionUseCase
import com.uansari.expenseflow.domain.usecase.transaction.DeleteTransactionUseCase
import com.uansari.expenseflow.domain.usecase.transaction.GetTransactionByIdUseCase
import com.uansari.expenseflow.domain.usecase.transaction.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionManagementViewModel @Inject constructor(
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get transaction ID from navigation arguments (null for add mode)
    private val transactionId: Long? = savedStateHandle.get<Long>("transactionId")

    // ════════════════════════════════════════════════════════════════
    // UI State
    // ════════════════════════════════════════════════════════════════

    private val _uiState = MutableStateFlow(
        TransactionManagementUiState(
            isEditMode = transactionId != null, transactionId = transactionId
        )
    )
    val uiState: StateFlow<TransactionManagementUiState> = _uiState.asStateFlow()

    // ════════════════════════════════════════════════════════════════
    // Events
    // ════════════════════════════════════════════════════════════════

    private val _events = Channel<TransactionManagementEvent>()
    val events = _events.receiveAsFlow()

    // ════════════════════════════════════════════════════════════════
    // Initialization
    // ════════════════════════════════════════════════════════════════

    init {
        loadAccounts()
        loadCategories()

        // If edit mode, load existing transaction
        transactionId?.let { loadTransaction(it) }
    }

    // ════════════════════════════════════════════════════════════════
    // Data Loading
    // ════════════════════════════════════════════════════════════════

    private fun loadAccounts() {
        viewModelScope.launch {
            getAccountsUseCase().catch { /* Handle error */ }.collect { accounts ->
                _uiState.update { state ->
                    state.copy(
                        accounts = accounts,
                        // Auto-select default account if none selected
                        selectedAccount = state.selectedAccount ?: accounts.find { it.isDefault }
                        ?: accounts.firstOrNull())
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            // Get categories filtered by current type
            getCategoriesUseCase(_uiState.value.type).catch { /* Handle error */ }
                .collect { categories ->
                    _uiState.update { state ->
                        state.copy(categories = categories)
                    }
                }
        }
    }

    private fun loadTransaction(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val transaction = getTransactionByIdUseCase(id)

            if (transaction != null) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        amount = transaction.amount.toString(),
                        type = transaction.type,
                        selectedCategory = transaction.category,
                        selectedAccount = transaction.account,
                        date = transaction.date,
                        note = transaction.note ?: ""
                    )
                }
                // Reload categories for the transaction's type
                loadCategoriesForType(transaction.type)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(
                    TransactionManagementEvent.ShowSnackbar(
                        "Transaction not found"
                    )
                )
                _events.send(TransactionManagementEvent.NavigateBack)
            }
        }
    }

    private fun loadCategoriesForType(type: TransactionType) {
        viewModelScope.launch {
            getCategoriesUseCase(type).first()  // Just get once, not continuous
                .let { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // User Actions - Form Input
    // ════════════════════════════════════════════════════════════════

    fun onAmountChange(amount: String) {
        // Only allow valid decimal input
        val filtered = amount.filter { it.isDigit() || it == '.' }
        // Prevent multiple decimal points
        val singleDecimal = if (filtered.count { it == '.' } > 1) {
            _uiState.value.amount
        } else {
            filtered
        }

        _uiState.update {
            it.copy(
                amount = singleDecimal, amountError = null  // Clear error on change
            )
        }
    }

    fun onTypeChange(type: TransactionType) {
        if (type != _uiState.value.type) {
            _uiState.update {
                it.copy(
                    type = type, selectedCategory = null,  // Reset category on type change
                    categoryError = null
                )
            }
            // Reload categories for new type
            loadCategoriesForType(type)
        }
    }

    fun onCategorySelect(category: CategoryDom) {
        _uiState.update {
            it.copy(
                selectedCategory = category, categoryError = null
            )
        }
    }

    fun onAccountSelect(account: AccountDom) {
        _uiState.update {
            it.copy(
                selectedAccount = account, accountError = null
            )
        }
    }

    fun onDateSelect(date: Long) {
        _uiState.update {
            it.copy(
                date = date, showDatePicker = false
            )
        }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onShowDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun onDismissDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    // ════════════════════════════════════════════════════════════════
    // User Actions - Save/Delete
    // ════════════════════════════════════════════════════════════════

    fun onSaveClick() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val state = _uiState.value
            val amount = state.amount.toDoubleOrNull() ?: 0.0

            val result = if (state.isEditMode && state.transactionId != null) {

                // Update existing
                updateTransactionUseCase(
                    transactionId = state.transactionId,
                    amount = amount,
                    type = state.type,
                    accountId = state.selectedAccount!!.id,
                    categoryId = state.selectedCategory!!.id,
                    note = state.note.ifBlank { null },
                    date = state.date
                )
            } else {
                // Add new
                addTransactionUseCase(
                    amount = amount,
                    type = state.type,
                    accountId = state.selectedAccount!!.id,
                    categoryId = state.selectedCategory!!.id,
                    note = state.note.ifBlank { null },
                    date = state.date
                ).map { }  // Map Result<Long> to Result<Unit>
            }

            _uiState.update { it.copy(isSaving = false) }

            result.fold(onSuccess = {
                _events.send(TransactionManagementEvent.TransactionSaved)
                _events.send(TransactionManagementEvent.NavigateBack)
            }, onFailure = { error ->
                _events.send(
                    TransactionManagementEvent.ShowSnackbar(
                        error.message ?: "Failed to save transaction"
                    )
                )
            })
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    fun onConfirmDelete() {
        val id = transactionId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteConfirm = false, isSaving = true) }

            deleteTransactionUseCase(id).fold(onSuccess = {
                _events.send(TransactionManagementEvent.TransactionDeleted)
                _events.send(TransactionManagementEvent.NavigateBack)
            }, onFailure = { error ->
                _uiState.update { it.copy(isSaving = false) }
                _events.send(
                    TransactionManagementEvent.ShowSnackbar(
                        error.message ?: "Failed to delete transaction"
                    )
                )
            })
        }
    }

    fun onDismissDeleteConfirm() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    // ════════════════════════════════════════════════════════════════
    // Validation
    // ════════════════════════════════════════════════════════════════

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        // Validate amount
        val amount = state.amount.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            _uiState.update { it.copy(amountError = "Enter a valid amount") }
            isValid = false
        }

        // Validate category
        if (state.selectedCategory == null) {
            _uiState.update { it.copy(categoryError = "Select a category") }
            isValid = false
        }

        // Validate account
        if (state.selectedAccount == null) {
            _uiState.update { it.copy(accountError = "Select an account") }
            isValid = false
        }

        return isValid
    }
}

data class TransactionManagementUiState(
    // Mode
    val isEditMode: Boolean = false,
    val transactionId: Long? = null,

    // Loading states
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    // Form fields
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: CategoryDom? = null,
    val selectedAccount: AccountDom? = null,
    val date: Long = System.currentTimeMillis(),
    val note: String = "",

    // Available options
    val categories: List<CategoryDom> = emptyList(),
    val accounts: List<AccountDom> = emptyList(),

    // Validation errors
    val amountError: String? = null,
    val categoryError: String? = null,
    val accountError: String? = null,

    // Dialog states
    val showDatePicker: Boolean = false,
    val showDeleteConfirm: Boolean = false
) {
    // Computed properties
    val screenTitle: String
        get() = if (isEditMode) "Edit Transaction" else "Add Transaction"

    val canSave: Boolean
        get() = amount.isNotBlank() && selectedCategory != null && selectedAccount != null && !isSaving

    val hasErrors: Boolean
        get() = amountError != null || categoryError != null || accountError != null
}

// ════════════════════════════════════════════════════════════════════
// Events Sealed Class
// ════════════════════════════════════════════════════════════════════

sealed class TransactionManagementEvent {
    data object NavigateBack : TransactionManagementEvent()
    data object TransactionSaved : TransactionManagementEvent()
    data object TransactionDeleted : TransactionManagementEvent()
    data class ShowSnackbar(val message: String) : TransactionManagementEvent()
}