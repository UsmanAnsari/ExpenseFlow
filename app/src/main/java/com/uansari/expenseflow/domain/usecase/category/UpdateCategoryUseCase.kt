package com.uansari.expenseflow.domain.usecase.category

import com.uansari.expenseflow.domain.repository.CategoryRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(
        categoryId: Long, name: String, icon: String, color: Long
    ): Result<Unit> {

        // Validate name
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Category name cannot be empty")
            )
        }

        val existingCategory =
            categoryRepository.getCategoryById(categoryId) ?: return Result.failure(
                IllegalArgumentException("Category not found")
            )

        return runCatching {
            val updatedCategory = existingCategory.copy(
                name = trimmedName, icon = icon, color = color
            )
            categoryRepository.updateCategory(updatedCategory)
        }
    }
}