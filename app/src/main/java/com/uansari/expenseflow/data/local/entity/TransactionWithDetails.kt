package com.uansari.expenseflow.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithDetails(
    @Embedded val transaction: Transaction,
    @Relation(
        Account::class,
        "account_id",
        "id"
    ) val account: Account?,
    @Relation(
        Category::class,
        "category_id",
        "id"
    ) val category: Category?
)
