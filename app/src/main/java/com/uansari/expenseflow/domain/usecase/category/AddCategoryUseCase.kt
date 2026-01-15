package com.uansari.expenseflow.domain.usecase.category

import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(
        name: String, icon: String, color: Long, type: TransactionType
    ): Result<Long> {

        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Category name cannot be empty")
            )
        }

        if (icon.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Icon is required")
            )
        }

        return runCatching {
            val category = CategoryDom(
                name = trimmedName,
                icon = icon,
                color = color,
                type = type,
                isDefault = false,
                createdAt = System.currentTimeMillis()
            )
            categoryRepository.insertCategory(category)
        }
    }
}