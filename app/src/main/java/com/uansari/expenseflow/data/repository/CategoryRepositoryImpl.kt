package com.uansari.expenseflow.data.repository

import com.uansari.expenseflow.data.local.dao.CategoryDao
import com.uansari.expenseflow.data.mapper.toDomain
import com.uansari.expenseflow.data.mapper.toEntity
import com.uansari.expenseflow.domain.model.CategoryDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<CategoryDom>> {
        return categoryDao.getAllCategories().map { entities -> entities.toDomain() }
    }

    override fun getCategoriesByType(
        type: TransactionType
    ): Flow<List<CategoryDom>> {
        return categoryDao.getCategoriesByType(type.name).map { entities -> entities.toDomain() }
    }

    override suspend fun getCategoryById(id: Long): CategoryDom? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun insertCategory(category: CategoryDom): Long {
        return categoryDao.insert(category.toEntity())
    }

    override suspend fun updateCategory(category: CategoryDom) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun deleteCategory(category: CategoryDom) {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun getTransactionCountForCategory(
        categoryId: Long
    ): Int {
        return categoryDao.getTransactionCountForCategory(categoryId)
    }
}