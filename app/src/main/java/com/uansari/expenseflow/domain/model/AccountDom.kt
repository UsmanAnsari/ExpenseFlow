package com.uansari.expenseflow.domain.model

data class AccountDom(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val color: Long,
    val icon: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
