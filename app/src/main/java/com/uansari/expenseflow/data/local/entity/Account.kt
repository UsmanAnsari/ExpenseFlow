package com.uansari.expenseflow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uansari.expenseflow.domain.model.AccountType

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: AccountType,
    var balance: Double = 0.0,
    val color: Long,
    val icon: String,
    @ColumnInfo("is_default") val isDefault: Boolean = false,
    @ColumnInfo("created_at") val createdAt: Long = System.currentTimeMillis()
)

