package com.uansari.expenseflow.domain.usecase.account

import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    operator fun invoke(): Flow<List<AccountDom>> {
        return accountRepository.getAllAccounts()
    }
}