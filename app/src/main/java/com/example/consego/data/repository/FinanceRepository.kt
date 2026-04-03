package com.example.consego.data.repository

import com.example.consego.data.data_store.UserPreferences
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType
import com.example.consego.data.room.TransactionDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FinanceRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val userPreferences: UserPreferences // Injecting UserPreferences
) {
    val allTransactions = transactionDao.getAllTransactions()
    val recentTransactions = transactionDao.getRecentTransactions()

    suspend fun insert(transaction: TransactionEntity) {
        // 1. Save the transaction to Room database
        transactionDao.insertTransaction(transaction)

        // 2. Fetch current balances from DataStore (Snapshot)
        val currentCash = userPreferences.cashBalance.first()
        val currentBank = userPreferences.bankBalance.first()

        val amount = transaction.amount
        val isExpense = transaction.type == TransactionType.EXPENSE

        // 3. Calculate and update new balance based on Account Type (CASH or BANK)
        if (transaction.accountType == "CASH") {
            val newCash = if (isExpense) currentCash - amount else currentCash + amount
            userPreferences.saveBalances(newCash, currentBank)
        } else {
            val newBank = if (isExpense) currentBank - amount else currentBank + amount
            userPreferences.saveBalances(currentCash, newBank)
        }
    }
}