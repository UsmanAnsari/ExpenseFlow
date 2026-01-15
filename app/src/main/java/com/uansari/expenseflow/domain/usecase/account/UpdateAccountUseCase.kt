package com.uansari.expenseflow.domain.usecase.account

import com.uansari.expenseflow.domain.model.AccountType
import com.uansari.expenseflow.domain.repository.AccountRepository
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(
        accountId: Long,
        name: String,
        type: AccountType,
        icon: String,
        color: Long,
        isDefault: Boolean
    ): Result<Unit> {

        // Validate name
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Account name cannot be empty")
            )
        }

        // Get existing account
        val existingAccount = accountRepository.getAccountById(accountId)
            ?: return Result.failure(
                IllegalArgumentException("Account not found")
            )

        return runCatching {
            // Handle default flag change
            if (isDefault && !existingAccount.isDefault) {
                // Setting this as new default, unset current
                accountRepository.getDefaultAccount()?.let { current ->
                    if (current.id != accountId) {
                        accountRepository.updateAccount(
                            current.copy(isDefault = false)
                        )
                    }
                }
            }

            // Update account (preserve balance!)
            val updatedAccount = existingAccount.copy(
                name = trimmedName,
                type = type,
                icon = icon,
                color = color,
                isDefault = isDefault
                // balance stays the same
            )
            accountRepository.updateAccount(updatedAccount)
        }
    }
}