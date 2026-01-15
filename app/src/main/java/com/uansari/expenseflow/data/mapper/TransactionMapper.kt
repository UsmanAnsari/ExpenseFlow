package com.uansari.expenseflow.data.mapper

import com.uansari.expenseflow.data.local.entity.Account
import com.uansari.expenseflow.data.local.entity.Transaction
import com.uansari.expenseflow.data.local.entity.TransactionWithDetails
import com.uansari.expenseflow.domain.model.AccountDom
import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.model.TransactionType

fun TransactionWithDetails.toDomain(): TransactionDom {
           return TransactionDom(
               id = transaction.id,
               amount = transaction.amount,
               type = transaction.type,
               note = transaction.note,
               date = transaction.date,
               account = account?.toDomain(),   // Nested mapping
               category = category?.toDomain(), // Nested mapping
               createdAt = transaction.createdAt,
           )
       }

   // Domain Transaction → TransactionEntity
   // Note: Only creates entity, not the relations
   // Used for INSERT/UPDATE operations

   fun TransactionDom.toEntity(): Transaction {
           return Transaction(
               id = id,
               accountId = account?.id,   // Extract ID from nested object
               categoryId = category?.id, // Extract ID from nested object
               amount = amount,
               type = type,
               note = note,
               date = date,
               createdAt = createdAt,
           )
       }

   // List convenience
   fun List<TransactionWithDetails>.toDomain(): List<TransactionDom> {
           return map { it.toDomain() }
       }