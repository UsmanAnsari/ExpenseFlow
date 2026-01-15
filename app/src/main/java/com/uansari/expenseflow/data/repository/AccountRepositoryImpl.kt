package com.uansari.expenseflow.data.repository

import com.uansari.expenseflow.data.local.dao.AccountDao
import com.uansari.expenseflow.data.mapper.toDomain
import com.uansari.expenseflow.data.mapper.toEntity
import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {


    override fun getAllAccounts(): Flow<List<AccountDom>> {
        return accountDao.getAllAccounts()
            .map { entities -> entities.toDomain() }
    }

    override fun getTotalBalance(): Flow<Double> {
        return accountDao.getTotalBalance()
            .map { it ?: 0.0 }
    }

    override suspend fun getAccountById(id: Long): AccountDom? {
        return accountDao.getAccountById(id)?.toDomain()
    }

    override suspend fun getDefaultAccount(): AccountDom? {
        return accountDao.getDefaultAccount()?.toDomain()
    }

    override suspend fun insertAccount(account: AccountDom): Long {
        return accountDao.insert(account.toEntity())
    }

    override suspend fun updateAccount(account: AccountDom) {
        accountDao.update(account.toEntity())
    }

    override suspend fun updateBalance(
        accountId: Long,
        newBalance: Double
    ) {
        accountDao.updateBalance(accountId, newBalance)
    }

    override suspend fun deleteAccount(account: AccountDom) {
        accountDao.delete(account.toEntity())
    }
}