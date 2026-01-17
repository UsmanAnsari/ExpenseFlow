package com.uansari.expenseflow.core.util

import com.uansari.expenseflow.domain.model.AccountType

val accountIcons = listOf(
    "💵",  // Cash
    "🏦",  // Bank
    "💳",  // Credit Card
    "💰",  // Savings
    "🪙",  // Coins
    "💎",  // Investment
    "🏧",  // ATM
    "📊",  // Portfolio
    "🏪",  // Store Account
    "📱",  // Digital Wallet
    "🎮",  // Gaming
    "✨"   // Other
)

fun AccountType.icon(): String = when (this) {
    AccountType.CASH -> "💵"
    AccountType.BANK -> "🏦"
    AccountType.SAVINGS -> "💰"
    AccountType.OTHER -> "✨"

    /*AccountType.CREDIT_CARD -> "💳"
    AccountType.DEBIT_CARD -> "💳"
    AccountType.INVESTMENT -> "📈"
    AccountType.WALLET -> "👛"*/
}

val categoryIcons = listOf(
    // Food & Drink
    "🍔", "🍕", "🍜", "☕", "🍺", "🍽️",
    // Transport
    "🚗", "🚌", "✈️", "🚕", "⛽", "🚲",
    // Shopping
    "🛒", "🛍️", "👕", "💄", "📱", "💻",
    // Entertainment
    "🎬", "🎮", "🎵", "📚", "🎭", "🎪",
    // Home
    "🏠", "🔧", "💡", "🛋️", "🧹", "🌿",
    // Health
    "💊", "🏥", "🏋️", "🧘", "💆", "🦷",
    // Income
    "💰", "💵", "🎁", "💼", "📈", "🏆",
    // Other
    "✨", "📝", "🎓", "🐕", "👶", "❓"
)
