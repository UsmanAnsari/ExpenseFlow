package com.uansari.expenseflow.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.expenseflow.domain.model.CategoryWithAmount
import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.usecase.dashboard.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModel() {

    // ════════════════════════════════════════════════════════════════
    // UI State
    // ════════════════════════════════════════════════════════════════

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // ════════════════════════════════════════════════════════════════
    // Events (one-time actions)
    // ════════════════════════════════════════════════════════════════

    private val _events = Channel<DashboardEvent>()
    val events = _events.receiveAsFlow()

    // ════════════════════════════════════════════════════════════════
    // Initialization
    // ════════════════════════════════════════════════════════════════

    init {
        loadDashboardData()
    }

    // ════════════════════════════════════════════════════════════════
    // Data Loading
    // ════════════════════════════════════════════════════════════════

    private fun loadDashboardData() {
        viewModelScope.launch {
            getDashboardDataUseCase().onStart {
                _uiState.update { it.copy(isLoading = true) }
            }.catch { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = exception.message ?: "Unknown error"
                    )
                }
            }.collect { dashboardData ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        totalBalance = dashboardData.totalBalance,
                        monthlyIncome = dashboardData.monthlyIncome,
                        monthlyExpense = dashboardData.monthlyExpense,
                        recentTransactions = dashboardData.recentTransactions,
                        spendingByCategory = dashboardData.spendingByCategory
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // User Actions
    // ════════════════════════════════════════════════════════════════

    fun onAddTransactionClick() {
        viewModelScope.launch {
            _events.send(DashboardEvent.NavigateToAddTransaction)
        }
    }

    fun onSeeAllTransactionsClick() {
        viewModelScope.launch {
            _events.send(DashboardEvent.NavigateToAllTransactions)
        }
    }

    fun onTransactionClick(transactionId: Long) {
        viewModelScope.launch {
            _events.send(DashboardEvent.NavigateToTransactionDetail(transactionId))
        }
    }

    fun onRetry() {
        loadDashboardData()
    }
}


data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<TransactionDom> = emptyList(),
    val spendingByCategory: List<CategoryWithAmount> = emptyList()
) {
    // Computed properties
    val hasTransactions: Boolean
        get() = recentTransactions.isNotEmpty()

    val hasSpendingData: Boolean
        get() = spendingByCategory.isNotEmpty()

    val monthlyNet: Double
        get() = monthlyIncome - monthlyExpense
}


sealed class DashboardEvent {
    data object NavigateToAddTransaction : DashboardEvent()
    data object NavigateToAllTransactions : DashboardEvent()
    data class NavigateToTransactionDetail(val transactionId: Long) : DashboardEvent()
}