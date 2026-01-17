package com.uansari.expenseflow.feature.accounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.uansari.expenseflow.core.util.accountColors
import com.uansari.expenseflow.core.util.accountIcons
import com.uansari.expenseflow.core.util.icon
import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFormDialog(
    account: AccountDom?,
    title: String,
    isEditMode: Boolean,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        name: String, icon: String, type: AccountType, color: Long, initialBalance: Double, isDefault: Boolean
    ) -> Unit
) {
    // ════════════════════════════════════════════════════════════════
    // Form State
    // ════════════════════════════════════════════════════════════════

    var name by remember { mutableStateOf(account?.name ?: "") }
    var selectedIcon by remember { mutableStateOf(account?.icon ?: accountIcons.first()) }
    var selectedType by remember { mutableStateOf(account?.type ?: AccountType.CASH) }
    var selectedColor by remember { mutableLongStateOf(account?.color ?: accountColors.first()) }
    var initialBalance by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(account?.isDefault ?: false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    // ════════════════════════════════════════════════════════════════
    // Dialog
    // ════════════════════════════════════════════════════════════════

    Dialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // ──────────────────────────────────────────────────────
                // Title
                // ──────────────────────────────────────────────────────
                Text(
                    text = title, style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ──────────────────────────────────────────────────────
                // Account Name
                // ──────────────────────────────────────────────────────
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Account Name") },
                    placeholder = { Text("e.g., Cash, Main Bank") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ──────────────────────────────────────────────────────
                // Account Type Dropdown
                // ──────────────────────────────────────────────────────
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { if (!isSaving) typeDropdownExpanded = it }) {
                    OutlinedTextField(
                        value = selectedType.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Account Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }) {
                        AccountType.entries.forEach { type ->
                            DropdownMenuItem(text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(type.icon())
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(type.displayName())
                                }
                            }, onClick = {
                                selectedType = type
                                typeDropdownExpanded = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ──────────────────────────────────────────────────────
                // Initial Balance (only for new accounts)
                // ──────────────────────────────────────────────────────
                if (!isEditMode) {
                    OutlinedTextField(
                        value = initialBalance,
                        onValueChange = { initialBalance = it },
                        label = { Text("Initial Balance") },
                        placeholder = { Text("0.00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ──────────────────────────────────────────────────────
                // Icon Selection
                // ──────────────────────────────────────────────────────
                Text(
                    text = "Select Icon",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(accountIcons) { icon ->
                        IconOption(
                            icon = icon,
                            isSelected = icon == selectedIcon,
                            color = Color(selectedColor),
                            enabled = !isSaving,
                            onClick = { selectedIcon = icon })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ──────────────────────────────────────────────────────
                // Color Selection
                // ──────────────────────────────────────────────────────
                Text(
                    text = "Select Color",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(accountColors) { color ->
                        ColorOption(
                            color = Color(color),
                            isSelected = color == selectedColor,
                            enabled = !isSaving,
                            onClick = { selectedColor = color })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ──────────────────────────────────────────────────────
                // Default Account Toggle
                // ──────────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(enabled = !isSaving) { isDefault = !isDefault }
                        .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { if (!isSaving) isDefault = it },
                        enabled = !isSaving
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Set as Default", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "New transactions will use this account",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ──────────────────────────────────────────────────────
                // Action Buttons
                // ──────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss, enabled = !isSaving
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Validate
                            if (name.isBlank()) {
                                nameError = "Name is required"
                                return@Button
                            }

                            val balance = initialBalance.toDoubleOrNull() ?: 0.0

                            onSave(
                                name.trim(),
                                selectedIcon,
                                selectedType,
                                selectedColor,
                                balance,
                                isDefault
                            )
                        }, enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (isEditMode) "Save" else "Create")
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Helper Components
// ════════════════════════════════════════════════════════════════════════

@Composable
private fun IconOption(
    icon: String, isSelected: Boolean, color: Color, enabled: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(MaterialTheme.shapes.small)
            .background(
                if (isSelected) color.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) color else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Text(text = icon, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ColorOption(
    color: Color, isSelected: Boolean, enabled: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.small)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// AccountType Extension Functions
// ════════════════════════════════════════════════════════════════════════

private fun AccountType.displayName(): String = when (this) {
    AccountType.CASH -> "Cash"
    AccountType.BANK -> "Bank Account"
    AccountType.SAVINGS -> "Savings"
    AccountType.OTHER -> "Other"
    /*AccountType.CREDIT_CARD -> "Credit Card"
    AccountType.DEBIT_CARD -> "Debit Card"
    AccountType.INVESTMENT -> "Investment"
    AccountType.WALLET -> "Digital Wallet"*/
}