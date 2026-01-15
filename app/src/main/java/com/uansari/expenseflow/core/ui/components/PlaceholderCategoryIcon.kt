package com.uansari.expenseflow.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderCategoryIcon(
    modifier: Modifier = Modifier, size: Dp = 40.dp
) {
    CategoryIcon(
        icon = "❓", color = MaterialTheme.colorScheme.outline, size = size, modifier = modifier
    )
}