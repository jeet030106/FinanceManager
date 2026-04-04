package com.example.consego.data.model

data class AddTransactionUiState(
    val id: Int? = null,
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "Food",
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val accountType: String = "CASH" // Or "BANK"
)
