package com.uansari.expenseflow.data.mapper

import com.uansari.expenseflow.data.local.entity.Account
import com.uansari.expenseflow.domain.model.AccountDom

fun Account.toDomain(): AccountDom {
    return AccountDom(
        id = id,
        name = name,
        type = type,
        balance = balance,
        color = color,
        icon = icon,
        isDefault = isDefault,
        createdAt = createdAt
    )
}

fun AccountDom.toEntity(): Account {
    return Account(
        id = id,
        name = name,
        type = type,
        balance = balance,
        color = color,
        icon = icon,
        isDefault = isDefault,
        createdAt = createdAt
    )
}

fun List<Account>.toDomain(): List<AccountDom> {
    return map { it.toDomain() }
}