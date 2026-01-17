package com.uansari.expenseflow.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    // ════════════════════════════════════════════════════════════════
    // Events
    // ════════════════════════════════════════════════════════════════

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    // ════════════════════════════════════════════════════════════════
    // User Actions
    // ════════════════════════════════════════════════════════════════

    fun onCategoriesClick() {
        viewModelScope.launch {
            _events.send(SettingsEvent.NavigateToCategories)
        }
    }

    fun onAccountsClick() {
        viewModelScope.launch {
            _events.send(SettingsEvent.NavigateToAccounts)
        }
    }

    fun onExportDataClick() {
        viewModelScope.launch {
            _events.send(SettingsEvent.ShowSnackbar("Export coming soon!"))
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// Events Sealed Class
// ════════════════════════════════════════════════════════════════════

sealed class SettingsEvent {
    data object NavigateToCategories : SettingsEvent()
    data object NavigateToAccounts : SettingsEvent()
    data class ShowSnackbar(val message: String) : SettingsEvent()
}