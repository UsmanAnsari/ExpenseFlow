package com.uansari.expenseflow.domain.usecase.account

import com.uansari.expenseflow.domain.repository.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    sealed class DeleteResult {
        data object Success : DeleteResult()
        data object AccountNotFound : DeleteResult()
        data object CannotDeleteDefault : DeleteResult()
        data class Error(val exception: Throwable) : DeleteResult()
    }

    suspend operator fun invoke(
        accountId: Long, forceDelete: Boolean = false
    ): DeleteResult {

        // Get account
        val account =
            accountRepository.getAccountById(accountId) ?: return DeleteResult.AccountNotFound

        // Check if default
        if (account.isDefault) {
            return DeleteResult.CannotDeleteDefault
        }

        return try {
            accountRepository.deleteAccount(account)
            DeleteResult.Success
        } catch (e: Exception) {
            DeleteResult.Error(e)
        }
    }
}