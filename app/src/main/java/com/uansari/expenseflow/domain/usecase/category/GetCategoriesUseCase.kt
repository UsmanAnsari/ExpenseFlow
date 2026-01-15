package com.uansari.expenseflow.domain.usecase.category

import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(
        type: TransactionType? = null
    ): Flow<List<CategoryDom>> {
        return if (type != null) {
            categoryRepository.getCategoriesByType(type)
        } else {
            categoryRepository.getAllCategories()
        }
    }
}