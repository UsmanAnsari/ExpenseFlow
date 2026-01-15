package com.uansari.expenseflow.core.di

import com.uansari.expenseflow.data.repository.AccountRepositoryImpl
import com.uansari.expenseflow.data.repository.CategoryRepositoryImpl
import com.uansari.expenseflow.data.repository.TransactionRepositoryImpl
import com.uansari.expenseflow.domain.repository.AccountRepository
import com.uansari.expenseflow.domain.repository.CategoryRepository
import com.uansari.expenseflow.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

}