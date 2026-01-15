package com.uansari.expenseflow.domain.repository;

import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<CategoryDom>>


    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryDom>>


    suspend fun getCategoryById(id: Long): CategoryDom?


    suspend fun insertCategory(category: CategoryDom): Long


    suspend fun updateCategory(category: CategoryDom)


    suspend fun deleteCategory(category: CategoryDom)


    suspend fun getTransactionCountForCategory(categoryId: Long): Int
}