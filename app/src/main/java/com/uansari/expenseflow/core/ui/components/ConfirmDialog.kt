package com.uansari.expenseflow.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(text = title)
    }, text = {
        Text(text = message)
    }, confirmButton = {
        TextButton(
            onClick = {
                onConfirm()
                onDismiss()
            }, colors = if (isDestructive) {
                ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            } else {
                ButtonDefaults.textButtonColors()
            }
        ) {
            Text(text = confirmText)
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = dismissText)
        }
    })
}

// ════════════════════════════════════════════════════════════════════
// Pre-built dialogs for common scenarios
// ════════════════════════════════════════════════════════════════════

@Composable
fun DeleteTransactionDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "Delete Transaction",
        message = "Are you sure you want to delete this transaction? This action cannot be undone.",
        confirmText = "Delete",
        dismissText = "Cancel",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isDestructive = true
    )
}

@Composable
fun DeleteCategoryDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "Delete Category",
        message = "Transactions use this category and will become uncategorized.",
        confirmText = "Delete",
        dismissText = "Cancel",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isDestructive = true
    )
}

@Composable
fun DiscardChangesDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "Discard Changes",
        message = "You have unsaved changes. Are you sure you want to discard them?",
        confirmText = "Discard",
        dismissText = "Keep Editing",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isDestructive = true
    )
}