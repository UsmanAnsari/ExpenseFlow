package com.uansari.expenseflow.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.expenseflow.core.util.groupByDate
import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.usecase.transaction.DeleteTransactionUseCase
import com.uansari.expenseflow.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    // ════════════════════════════════════════════════════════════════
    // UI State
    // ════════════════════════════════════════════════════════════════

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    // ════════════════════════════════════════════════════════════════
    // Events
    // ════════════════════════════════════════════════════════════════

    private val _events = Channel<TransactionsEvent>()
    val events = _events.receiveAsFlow()

    // ════════════════════════════════════════════════════════════════
    // Internal state for filtering
    // ════════════════════════════════════════════════════════════════

    private val selectedFilter = MutableStateFlow(TransactionFilter.ALL)

    // ════════════════════════════════════════════════════════════════
    // Initialization
    // ════════════════════════════════════════════════════════════════

    init {
        observeTransactions()
    }

    // ════════════════════════════════════════════════════════════════
    // Data Loading
    // ════════════════════════════════════════════════════════════════

    private fun observeTransactions() {
        viewModelScope.launch {
            // Combine filter changes with transaction data
            selectedFilter.flatMapLatest { filter ->
                // Get transactions based on filter
                val type = when (filter) {
                    TransactionFilter.ALL -> null
                    TransactionFilter.INCOME -> TransactionType.INCOME
                    TransactionFilter.EXPENSE -> TransactionType.EXPENSE


                }
                getTransactionsUseCase(type = type)
            }.onStart {
                _uiState.update { it.copy(isLoading = true) }
            }.catch { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = exception.message ?: "Unknown error"
                    )
                }
            }.collect { transactions ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        transactions = transactions,
                        groupedTransactions = transactions.groupByDate(),
                        selectedFilter = selectedFilter.value
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // User Actions
    // ════════════════════════════════════════════════════════════════

    fun onFilterChange(filter: TransactionFilter) {
        selectedFilter.value = filter
    }

    fun onTransactionClick(transactionId: Long) {
        viewModelScope.launch {
            _events.send(TransactionsEvent.NavigateToEditTransaction(transactionId))
        }
    }

    fun onAddTransactionClick() {
        viewModelScope.launch {
            _events.send(TransactionsEvent.NavigateToAddTransaction)
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Delete Flow
    // ════════════════════════════════════════════════════════════════

    fun onDeleteTransaction(transaction: TransactionDom) {
        _uiState.update {
            it.copy(
                showDeleteConfirm = true, transactionToDelete = transaction
            )
        }
    }

    fun onConfirmDelete() {
        val transaction = _uiState.value.transactionToDelete ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showDeleteConfirm = false, transactionToDelete = null
                )
            }

            deleteTransactionUseCase(transaction.id).fold(onSuccess = {
                _events.send(
                    TransactionsEvent.ShowSnackbar(
                        message = "Transaction deleted",
                        actionLabel = null  // Could add "Undo" here
                    )
                )
            }, onFailure = { error ->
                _events.send(
                    TransactionsEvent.ShowSnackbar(
                        message = error.message ?: "Failed to delete", actionLabel = null
                    )
                )
            })
        }
    }

    fun onDismissDeleteConfirm() {
        _uiState.update {
            it.copy(
                showDeleteConfirm = false, transactionToDelete = null
            )
        }
    }
}


enum class TransactionFilter(val label: String) {
    ALL("All"), INCOME("Income"), EXPENSE("Expense")
}

// ════════════════════════════════════════════════════════════════════
// UI State Data Class
// ════════════════════════════════════════════════════════════════════

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val transactions: List<TransactionDom> = emptyList(),
    val groupedTransactions: Map<String, List<TransactionDom>> = emptyMap(),
    val selectedFilter: TransactionFilter = TransactionFilter.ALL,
    val showDeleteConfirm: Boolean = false,
    val transactionToDelete: TransactionDom? = null
) {
    // Computed properties
    val isEmpty: Boolean
        get() = transactions.isEmpty() && !isLoading

    val transactionCount: Int
        get() = transactions.size

    val filterLabel: String
        get() = when (selectedFilter) {
            TransactionFilter.ALL -> "all"
            TransactionFilter.INCOME -> "income"
            TransactionFilter.EXPENSE -> "expense"
        }
}

// ════════════════════════════════════════════════════════════════════
// Events Sealed Class
// ════════════════════════════════════════════════════════════════════

sealed class TransactionsEvent {
    data class NavigateToEditTransaction(
        val transactionId: Long
    ) : TransactionsEvent()

    data object NavigateToAddTransaction : TransactionsEvent()

    data class ShowSnackbar(
        val message: String, val actionLabel: String? = null
    ) : TransactionsEvent()
}