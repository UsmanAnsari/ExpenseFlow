package com.uansari.expenseflow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.uansari.expenseflow.domain.model.TransactionType

@Entity(
    tableName = "categories",
    indices = [
        Index("type"),
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val color: Long,
    val icon: String,
    @ColumnInfo("is_default") val isDefault: Boolean = false,
    @ColumnInfo("created_at") val createdAt: Long = System.currentTimeMillis()
)


