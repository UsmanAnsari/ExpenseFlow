package com.uansari.expenseflow.data.local.database

import androidx.room.TypeConverter
import com.uansari.expenseflow.domain.model.AccountType
import com.uansari.expenseflow.domain.model.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(name: String): TransactionType {
        return TransactionType.valueOf(name)
    }

    @TypeConverter
    fun fromAccountType(type: AccountType): String {
        return type.name
    }

    @TypeConverter
    fun toAccountType(name: String): AccountType {
        return AccountType.valueOf(name)
    }

}
