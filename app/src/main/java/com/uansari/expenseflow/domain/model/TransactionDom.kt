package com.uansari.expenseflow.domain.model

data class TransactionDom(
    val id: Long = 0,

    val amount: Double,

    val type: TransactionType,

    val note: String? = null,

    val date: Long,

    val account: AccountDom? = null,

    val category: CategoryDom? = null,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
