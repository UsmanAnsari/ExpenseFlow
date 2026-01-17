package com.uansari.expenseflow.feature.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.uansari.expenseflow.core.util.categoryColors


@Composable
fun ColorPicker(
    selectedColor: Long, onColorSelect: (Long) -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Color",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(categoryColors) { color ->
                ColorItem(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = { onColorSelect(color) })
            }
        }
    }
}

@Composable
private fun ColorItem(
    color: Long, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val composeColor = Color(color)
    val checkColor = if (composeColor.luminance() > 0.5f) {
        Color.Black
    } else {
        Color.White
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(composeColor)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp, color = MaterialTheme.colorScheme.outline, shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = checkColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}