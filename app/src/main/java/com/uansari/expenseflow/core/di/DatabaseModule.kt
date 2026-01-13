package com.uansari.expenseflow.core.di

import android.content.Context
import androidx.room.Room
import com.uansari.expenseflow.data.local.dao.AccountDao
import com.uansari.expenseflow.data.local.dao.CategoryDao
import com.uansari.expenseflow.data.local.dao.TransactionDao
import com.uansari.expenseflow.data.local.database.AppDatabase
import com.uansari.expenseflow.data.local.database.DatabaseCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addCallback(callback)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideDatabaseCallback(
        database: dagger.Lazy<AppDatabase>
    ): DatabaseCallback {
        return DatabaseCallback(
            scope = CoroutineScope(SupervisorJob()),
            accountDao = { database.get().accountDao() },
            categoryDao = { database.get().categoryDao() }
        )
    }
    
    @Provides
    @Singleton
    fun provideAccountDao(database: AppDatabase): AccountDao {
        return database.accountDao()
    }
    
    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
}