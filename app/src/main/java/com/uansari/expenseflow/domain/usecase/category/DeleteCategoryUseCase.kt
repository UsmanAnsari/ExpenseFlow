package com.uansari.expenseflow.domain.usecase.category

import com.uansari.expenseflow.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    sealed class DeleteResult {
        data object Success : DeleteResult()
        data object CategoryNotFound : DeleteResult()
        data object CannotDeleteDefault : DeleteResult()
        data class HasTransactions(val count: Int) : DeleteResult()
        data class Error(val exception: Throwable) : DeleteResult()
    }

    suspend operator fun invoke(
        categoryId: Long,
        forceDelete: Boolean = false
    ): DeleteResult {

        // Get category
        val category = categoryRepository.getCategoryById(categoryId)
            ?: return DeleteResult.CategoryNotFound

        // Check if default
        if (category.isDefault) {
            return DeleteResult.CannotDeleteDefault
        }

        // Check for existing transactions
        val transactionCount = categoryRepository
            .getTransactionCountForCategory(categoryId)

        if (transactionCount > 0 && !forceDelete) {
            return DeleteResult.HasTransactions(transactionCount)
        }

        // Proceed with deletion
        return try {
            categoryRepository.deleteCategory(category)
            DeleteResult.Success
        } catch (e: Exception) {
            DeleteResult.Error(e)
        }
    }
}