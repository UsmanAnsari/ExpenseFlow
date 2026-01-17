package com.uansari.expenseflow.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.usecase.category.AddCategoryUseCase
import com.uansari.expenseflow.domain.usecase.category.DeleteCategoryUseCase
import com.uansari.expenseflow.domain.usecase.category.GetCategoriesUseCase
import com.uansari.expenseflow.domain.usecase.category.UpdateCategoryUseCase
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
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    // ════════════════════════════════════════════════════════════════
    // UI State
    // ════════════════════════════════════════════════════════════════

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    // ════════════════════════════════════════════════════════════════
    // Events
    // ════════════════════════════════════════════════════════════════

    private val _events = Channel<CategoriesEvent>()
    val events = _events.receiveAsFlow()

    // ════════════════════════════════════════════════════════════════
    // Initialization
    // ════════════════════════════════════════════════════════════════

    init {
        loadCategories()
    }

    // ════════════════════════════════════════════════════════════════
    // Data Loading
    // ════════════════════════════════════════════════════════════════

    private fun loadCategories() {
        viewModelScope.launch {
            // Get all categories (null type = all)
            getCategoriesUseCase(type = null).onStart {
                _uiState.update { it.copy(isLoading = true) }
            }.catch { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = exception.message
                    )
                }
            }.collect { categories ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        categories = categories,
                        expenseCategories = categories.filter { cat ->
                            cat.type == TransactionType.EXPENSE
                        },
                        incomeCategories = categories.filter { cat ->
                            cat.type == TransactionType.INCOME
                        })
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Dialog Actions
    // ════════════════════════════════════════════════════════════════

    fun onAddCategoryClick() {
        _uiState.update {
            it.copy(
                dialogState = CategoryDialogState.Add
            )
        }
    }

    fun onEditCategoryClick(category: CategoryDom) {
        _uiState.update {
            it.copy(
                dialogState = CategoryDialogState.Edit(category)
            )
        }
    }

    fun onDismissDialog() {
        _uiState.update {
            it.copy(dialogState = null)
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Save Category
    // ════════════════════════════════════════════════════════════════

    fun onSaveCategory(
        name: String, type: TransactionType, icon: String, color: Long
    ) {
        val dialogState = _uiState.value.dialogState ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = when (dialogState) {
                is CategoryDialogState.Add -> {
                    addCategoryUseCase(
                        name = name, type = type, icon = icon, color = color
                    ).map { }
                }

                is CategoryDialogState.Edit -> {
                    updateCategoryUseCase(
                        categoryId = dialogState.category.id, name = name,
//                        type = type,
                        icon = icon, color = color
                    )
                }
            }

            _uiState.update { it.copy(isSaving = false) }

            result.fold(onSuccess = {
                _uiState.update { it.copy(dialogState = null) }
                val message = when (dialogState) {
                    is CategoryDialogState.Add -> "Category added"
                    is CategoryDialogState.Edit -> "Category updated"
                }
                _events.send(CategoriesEvent.ShowSnackbar(message))
            }, onFailure = { error ->
                _events.send(
                    CategoriesEvent.ShowSnackbar(
                        error.message ?: "Failed to save category"
                    )
                )
            })
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Delete Category
    // ════════════════════════════════════════════════════════════════

    fun onDeleteCategoryClick(category: CategoryDom) {
        _uiState.update {
            it.copy(
                showDeleteConfirm = true, categoryToDelete = category
            )
        }
    }

    fun onConfirmDelete() {
        val category = _uiState.value.categoryToDelete ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showDeleteConfirm = false, categoryToDelete = null
                )
            }

            when (deleteCategoryUseCase(category.id)) {
                DeleteCategoryUseCase.DeleteResult.CannotDeleteDefault -> {
                    _events.send(
                        CategoriesEvent.ShowSnackbar(
                            "Cannot delete default Category"
                        )
                    )
                }

                DeleteCategoryUseCase.DeleteResult.CategoryNotFound -> {
                    _events.send(
                        CategoriesEvent.ShowSnackbar(
                            "Failed to delete"
                        )
                    )
                }

                is DeleteCategoryUseCase.DeleteResult.Error -> {
                    _events.send(
                        CategoriesEvent.ShowSnackbar(
                            "Failed to delete"
                        )
                    )
                }

                is DeleteCategoryUseCase.DeleteResult.HasTransactions -> {
                    _events.send(
                        CategoriesEvent.ShowSnackbar(
                            "Failed to delete"
                        )
                    )
                }

                DeleteCategoryUseCase.DeleteResult.Success -> {
                    _events.send(
                        CategoriesEvent.ShowSnackbar(
                            "Category deleted"
                        )
                    )
                }
            }

        }
    }

    fun onDismissDeleteConfirm() {
        _uiState.update {
            it.copy(
                showDeleteConfirm = false, categoryToDelete = null
            )
        }
    }
}


sealed class CategoryDialogState {
    data object Add : CategoryDialogState()
    data class Edit(val category: CategoryDom) : CategoryDialogState()
}

// ════════════════════════════════════════════════════════════════════
// UI State
// ════════════════════════════════════════════════════════════════════

data class CategoriesUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val categories: List<CategoryDom> = emptyList(),
    val expenseCategories: List<CategoryDom> = emptyList(),
    val incomeCategories: List<CategoryDom> = emptyList(),
    val dialogState: CategoryDialogState? = null,
    val showDeleteConfirm: Boolean = false,
    val categoryToDelete: CategoryDom? = null
) {
    val isEmpty: Boolean
        get() = categories.isEmpty() && !isLoading

    val showDialog: Boolean
        get() = dialogState != null

    val dialogTitle: String
        get() = when (dialogState) {
            is CategoryDialogState.Add -> "Add Category"
            is CategoryDialogState.Edit -> "Edit Category"
            null -> ""
        }
}

// ════════════════════════════════════════════════════════════════════
// Events
// ════════════════════════════════════════════════════════════════════

sealed class CategoriesEvent {
    data class ShowSnackbar(val message: String) : CategoriesEvent()
}