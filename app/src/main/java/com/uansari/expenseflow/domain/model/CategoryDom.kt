package com.uansari.expenseflow.domain.model

data class CategoryDom(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val type: TransactionType,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
