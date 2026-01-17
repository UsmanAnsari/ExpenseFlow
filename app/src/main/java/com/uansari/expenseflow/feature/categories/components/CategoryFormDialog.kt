package com.uansari.expenseflow.feature.categories.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uansari.expenseflow.core.util.categoryColors
import com.uansari.expenseflow.core.util.categoryIcons
import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.feature.transaction_management.components.TypeSelector

@Composable
fun CategoryFormDialog(
    title: String,
    category: CategoryDom?,
    isSaving: Boolean,
    onSave: (name: String, type: TransactionType, icon: String, color: Long) -> Unit,
    onDismiss: () -> Unit
) {
    // Form state
    var name by remember { mutableStateOf(category?.name ?: "") }
    var type by remember {
        mutableStateOf(category?.type ?: TransactionType.EXPENSE)
    }
    var selectedIcon by remember {
        mutableStateOf(category?.icon ?: categoryIcons.first())
    }
    var selectedColor by remember {
        mutableStateOf(category?.color ?: categoryColors.first())
    }
    var nameError by remember { mutableStateOf<String?>(null) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name input
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Name") },
                placeholder = { Text("Category name") },
                isError = nameError != null,
                supportingText = if (nameError != null) {
                    { Text(nameError!!) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth())

            // Type selector
            Column {
                Text(
                    text = "Type",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TypeSelector(
                    selectedType = type, onTypeSelect = { type = it })
            }

            // Icon picker
            IconPicker(
                selectedIcon = selectedIcon, onIconSelect = { selectedIcon = it })

            // Color picker
            ColorPicker(
                selectedColor = selectedColor, onColorSelect = { selectedColor = it })
        }
    }, confirmButton = {
        Button(
            onClick = {
                // Validate
                if (name.isBlank()) {
                    nameError = "Name is required"
                    return@Button
                }
                onSave(name.trim(), type, selectedIcon, selectedColor)
            }, enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp), strokeWidth = 2.dp
                )
            } else {
                Text("Save")
            }
        }
    }, dismissButton = {
        TextButton(
            onClick = onDismiss, enabled = !isSaving
        ) {
            Text("Cancel")
        }
    })
}