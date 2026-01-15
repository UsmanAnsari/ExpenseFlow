package com.uansari.expenseflow.domain.repository;

import com.uansari.expenseflow.domain.model.AccountDom;
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<AccountDom>>

    fun getTotalBalance(): Flow<Double>

    suspend fun getAccountById(id: Long): AccountDom?

    suspend fun getDefaultAccount(): AccountDom?

    suspend fun insertAccount(account: AccountDom): Long

    suspend fun updateAccount(account: AccountDom)

    suspend fun updateBalance(accountId: Long, newBalance: Double)

    suspend fun deleteAccount(account: AccountDom)
}