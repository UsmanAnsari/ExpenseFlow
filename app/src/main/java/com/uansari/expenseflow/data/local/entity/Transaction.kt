package com.uansari.expenseflow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.uansari.expenseflow.domain.model.TransactionType

@Entity(
    tableName = "transactions", foreignKeys = [ForeignKey(
        Account::class,
        ["id"],
        ["account_id"],
        onDelete = ForeignKey.SET_NULL,
    ), ForeignKey(
        Category::class,
        ["id"],
        ["category_id"],
        onDelete = ForeignKey.SET_NULL,
    )], indices = [Index("account_id"), Index("category_id"), Index("type"), Index("date")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("account_id") val accountId: Long?,
    @ColumnInfo("category_id") val categoryId: Long?,
    val amount: Double,
    val type: TransactionType,
    val note: String?,
    val date: Long = System.currentTimeMillis(),
    @ColumnInfo("created_at") val createdAt: Long = System.currentTimeMillis(),
)
