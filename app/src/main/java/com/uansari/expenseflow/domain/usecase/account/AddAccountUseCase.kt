package com.uansari.expenseflow.domain.usecase.account

import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.model.AccountType
import com.uansari.expenseflow.domain.repository.AccountRepository
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(
        name: String,
        type: AccountType,
        icon: String,
        color: Long,
        initialBalance: Double = 0.0,
        isDefault: Boolean = false
    ): Result<Long> {

        // Validate name
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Account name cannot be empty")
            )
        }

        return runCatching {
            // If setting as default, unset current default first
            if (isDefault) {
                accountRepository.getDefaultAccount()?.let { current ->
                    accountRepository.updateAccount(
                        current.copy(isDefault = false)
                    )
                }
            }

            val account = AccountDom(
                name = trimmedName,
                type = type,
                balance = initialBalance,
                icon = icon,
                color = color,
                isDefault = isDefault,
                createdAt = System.currentTimeMillis()
            )

            accountRepository.insertAccount(account)
        }
    }
}