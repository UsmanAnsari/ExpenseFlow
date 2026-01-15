package com.uansari.expenseflow.data.mapper

import com.uansari.expenseflow.data.local.entity.Category
import com.uansari.expenseflow.domain.model.CategoryDom

fun Category.toDomain(): CategoryDom {
    return CategoryDom(
        id = id,
        name = name,
        icon = icon,
        color = color,
        type = type,
        isDefault = isDefault,
        createdAt = createdAt
    )
}

// Domain →
fun CategoryDom.toEntity(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        color = color,
        type = type,
        isDefault = isDefault,
        createdAt = createdAt
    )
}

// List convenience
fun List<Category>.toDomain(): List<CategoryDom> {
    return map { it.toDomain() }
}