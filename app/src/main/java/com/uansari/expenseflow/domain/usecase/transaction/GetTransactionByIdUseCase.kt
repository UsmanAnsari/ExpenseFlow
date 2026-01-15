package com.uansari.expenseflow.domain.usecase.transaction

import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(id: Long): TransactionDom? {
        return transactionRepository.getTransactionById(id)
    }
}