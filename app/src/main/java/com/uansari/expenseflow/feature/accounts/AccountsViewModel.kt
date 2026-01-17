package com.uansari.expenseflow.feature.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.model.AccountType
import com.uansari.expenseflow.domain.usecase.account.AddAccountUseCase
import com.uansari.expenseflow.domain.usecase.account.DeleteAccountUseCase
import com.uansari.expenseflow.domain.usecase.account.GetAccountsUseCase
import com.uansari.expenseflow.domain.usecase.account.UpdateAccountUseCase
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
class AccountsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase

) : ViewModel() {

    // ════════════════════════════════════════════════════════════════
    // UI State
    // ════════════════════════════════════════════════════════════════

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    // ════════════════════════════════════════════════════════════════
    // Events
    // ════════════════════════════════════════════════════════════════

    private val _events = Channel<AccountsEvent>()
    val events = _events.receiveAsFlow()

    // ════════════════════════════════════════════════════════════════
    // Initialization
    // ════════════════════════════════════════════════════════════════

    init {
        loadAccounts()
    }

    // ════════════════════════════════════════════════════════════════
    // Data Loading
    // ════════════════════════════════════════════════════════════════

    private fun loadAccounts() {
        viewModelScope.launch {
            getAccountsUseCase().onStart {
                _uiState.update { it.copy(isLoading = true) }
            }.catch { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = exception.message
                    )
                }
            }.collect { accounts ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        accounts = accounts,
                        totalBalance = accounts.sumOf { acc -> acc.balance })
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Dialog Actions
    // ════════════════════════════════════════════════════════════════

    fun onAddAccountClick() {
        _uiState.update {
            it.copy(dialogState = AccountDialogState.Add)
        }
    }

    fun onEditAccountClick(account: AccountDom) {
        _uiState.update {
            it.copy(dialogState = AccountDialogState.Edit(account))
        }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(dialogState = null) }
    }

    // ════════════════════════════════════════════════════════════════
    // Save Account
    // ════════════════════════════════════════════════════════════════

    fun onSaveAccount(
        name: String,
        icon: String,
        type: AccountType,
        color: Long,
        initialBalance: Double,
        isDefault: Boolean
    ) {
        val dialogState = _uiState.value.dialogState ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = when (dialogState) {
                is AccountDialogState.Add -> {
                    addAccountUseCase(
                        name = name,
                        icon = icon,
                        type = type,
                        color = color,
                        initialBalance = initialBalance,
                        isDefault = isDefault
                    ).map { }
                }

                is AccountDialogState.Edit -> {
                    updateAccountUseCase(
                        accountId = dialogState.account.id,
                        name = name,
                        icon = icon,
                        type = type,
                        color = color,
                        isDefault = isDefault
                    )
                }
            }

            _uiState.update { it.copy(isSaving = false) }

            result.fold(onSuccess = {
                _uiState.update { it.copy(dialogState = null) }
                val message = when (dialogState) {
                    is AccountDialogState.Add -> "Account added"
                    is AccountDialogState.Edit -> "Account updated"
                }
                _events.send(AccountsEvent.ShowSnackbar(message))
            }, onFailure = { error ->
                _events.send(
                    AccountsEvent.ShowSnackbar(
                        error.message ?: "Failed to save account"
                    )
                )
            })
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Delete Account
    // ════════════════════════════════════════════════════════════════

    fun onDeleteAccountClick(account: AccountDom) {
        _uiState.update {
            it.copy(
                showDeleteConfirm = true, accountToDelete = account
            )
        }
    }

    fun onConfirmDelete() {
        val account = _uiState.value.accountToDelete ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showDeleteConfirm = false, accountToDelete = null
                )
            }

            when (deleteAccountUseCase(account.id)) {
                DeleteAccountUseCase.DeleteResult.AccountNotFound -> {
                    _events.send(AccountsEvent.ShowSnackbar("Account not found"))

                }
                DeleteAccountUseCase.DeleteResult.CannotDeleteDefault -> {
                    _events.send(AccountsEvent.ShowSnackbar("Account is default account"))

                }
                is DeleteAccountUseCase.DeleteResult.Error -> {
                    _events.send(AccountsEvent.ShowSnackbar("Failed to delete"))

                }

                DeleteAccountUseCase.DeleteResult.Success -> {
                    _events.send(AccountsEvent.ShowSnackbar("Account deleted"))

                }
            }


        }
    }

    fun onDismissDeleteConfirm() {
        _uiState.update {
            it.copy(
                showDeleteConfirm = false, accountToDelete = null
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// Dialog State
// ════════════════════════════════════════════════════════════════════

sealed class AccountDialogState {
    data object Add : AccountDialogState()
    data class Edit(val account: AccountDom) : AccountDialogState()
}

// ════════════════════════════════════════════════════════════════════
// UI State
// ════════════════════════════════════════════════════════════════════

data class AccountsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val accounts: List<AccountDom> = emptyList(),
    val totalBalance: Double = 0.0,
    val dialogState: AccountDialogState? = null,
    val showDeleteConfirm: Boolean = false,
    val accountToDelete: AccountDom? = null
) {
    val isEmpty: Boolean
        get() = accounts.isEmpty() && !isLoading

    val showDialog: Boolean
        get() = dialogState != null

    val dialogTitle: String
        get() = when (dialogState) {
            is AccountDialogState.Add -> "Add Account"
            is AccountDialogState.Edit -> "Edit Account"
            null -> ""
        }

    val isEditMode: Boolean
        get() = dialogState is AccountDialogState.Edit
}

// ════════════════════════════════════════════════════════════════════
// Events
// ════════════════════════════════════════════════════════════════════

sealed class AccountsEvent {
    data class ShowSnackbar(val message: String) : AccountsEvent()
}