package com.uansari.expenseflow.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uansari.expenseflow.core.util.toComposeColor
import com.uansari.expenseflow.domain.model.CategoryDom

@Composable
fun CategoryIcon(
    category: CategoryDom, modifier: Modifier = Modifier, size: Dp = 40.dp
) {
    CategoryIcon(
        icon = category.icon,
        color = category.color.toComposeColor(),
        size = size,
        modifier = modifier
    )
}

@Composable
fun CategoryIcon(
    icon: String, color: Color, modifier: Modifier = Modifier, size: Dp = 40.dp
) {
    // Calculate font size relative to circle size
    // Emoji should be about 50% of the circle diameter
    val fontSize = (size.value * 0.5f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),  // Lighter background
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon, fontSize = fontSize
        )
    }
}