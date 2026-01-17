package com.uansari.expenseflow.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.expenseflow.core.ui.components.DeleteCategoryDialog
import com.uansari.expenseflow.core.ui.components.EmptyState
import com.uansari.expenseflow.core.ui.components.LoadingScreen
import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.feature.categories.components.CategoryFormDialog
import com.uansari.expenseflow.feature.categories.components.CategoryListItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onNavigateBack: () -> Unit, viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CategoriesEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Categories") }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                )
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = viewModel::onAddCategoryClick
        ) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = "Add Category"
            )
        }
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        CategoriesContent(
            uiState = uiState,
            onCategoryClick = viewModel::onEditCategoryClick,
            onDeleteCategory = viewModel::onDeleteCategoryClick,
            modifier = Modifier.padding(paddingValues)
        )
    }

    // Add/Edit dialog
    if (uiState.showDialog) {
        val editCategory = (uiState.dialogState as? CategoryDialogState.Edit)?.category
        CategoryFormDialog(
            title = uiState.dialogTitle,
            category = editCategory,
            isSaving = uiState.isSaving,
            onSave = viewModel::onSaveCategory,
            onDismiss = viewModel::onDismissDialog
        )
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirm && uiState.categoryToDelete != null) {
        DeleteCategoryDialog(
            onConfirm = viewModel::onConfirmDelete, onDismiss = viewModel::onDismissDeleteConfirm
        )
    }
}

@Composable
private fun CategoriesContent(
    uiState: CategoriesUiState,
    onCategoryClick: (CategoryDom) -> Unit,
    onDeleteCategory: (CategoryDom) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            LoadingScreen(modifier = modifier)
        }

        uiState.isEmpty -> {
            EmptyState(
                icon = Icons.Outlined.Category,
                title = "No categories",
                description = "Add your first category to get started",
                modifier = modifier
            )
        }

        else -> {
            CategoriesList(
                expenseCategories = uiState.expenseCategories,
                incomeCategories = uiState.incomeCategories,
                onCategoryClick = onCategoryClick,
                onDeleteCategory = onDeleteCategory,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CategoriesList(
    expenseCategories: List<CategoryDom>,
    incomeCategories: List<CategoryDom>,
    onCategoryClick: (CategoryDom) -> Unit,
    onDeleteCategory: (CategoryDom) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Expense categories section
        if (expenseCategories.isNotEmpty()) {
            item {
                SectionHeader(title = "EXPENSE CATEGORIES")
            }

            items(
                items = expenseCategories, key = { it.id }) { category ->
                CategoryListItem(
                    category = category,
                    onClick = { onCategoryClick(category) },
                    onDelete = { onDeleteCategory(category) })
            }
        }

        // Spacer between sections
        if (expenseCategories.isNotEmpty() && incomeCategories.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Income categories section
        if (incomeCategories.isNotEmpty()) {
            item {
                SectionHeader(title = "INCOME CATEGORIES")
            }

            items(
                items = incomeCategories, key = { it.id }) { category ->
                CategoryListItem(
                    category = category,
                    onClick = { onCategoryClick(category) },
                    onDelete = { onDeleteCategory(category) })
            }
        }

        // Bottom spacing for FAB
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun SectionHeader(
    title: String, modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}