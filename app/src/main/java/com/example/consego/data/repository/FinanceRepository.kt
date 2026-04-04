package com.example.consego.data.repository

import com.example.consego.data.data_store.UserPreferences
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType
import com.example.consego.data.room.TransactionDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FinanceRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val userPreferences: UserPreferences
) {
    val allTransactions = transactionDao.getAllTransactions()
    val recentTransactions = transactionDao.getRecentTransactions()

    // Used by the ViewModel to load data for editing
    suspend fun getTransactionById(id: Int): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }

    suspend fun insert(transaction: TransactionEntity) {
        // 1. Save to Room
        transactionDao.insertTransaction(transaction)

        // 2. Apply balance impact
        adjustBalance(transaction, isUndo = false)
    }

    suspend fun delete(transaction: TransactionEntity) {
        // 1. Delete from Room
        transactionDao.deleteTransaction(transaction)

        // 2. Undo balance impact (reverse the transaction)
        adjustBalance(transaction, isUndo = true)
    }

    suspend fun update(newTransaction: TransactionEntity) {
        // 1. Fetch the old version of this transaction from DB before updating
        val oldTransaction = transactionDao.getTransactionById(newTransaction.id) ?: return

        // 2. Undo the impact of the OLD transaction
        adjustBalance(oldTransaction, isUndo = true)

        // 3. Save the NEW transaction to Room
        transactionDao.updateTransaction(newTransaction)

        // 4. Apply the impact of the NEW transaction
        adjustBalance(newTransaction, isUndo = false)
    }

    private suspend fun adjustBalance(transaction: TransactionEntity, isUndo: Boolean) {
        val currentCash = userPreferences.cashBalance.first()
        val currentBank = userPreferences.bankBalance.first()

        val amount = transaction.amount
        val isIncome = transaction.type == TransactionType.INCOME

        val shouldAdd = if (isUndo) !isIncome else isIncome

        if (transaction.accountType == "CASH") {
            val newCash = if (shouldAdd) currentCash + amount else currentCash - amount
            userPreferences.saveBalances(newCash, currentBank)
        } else {
            val newBank = if (shouldAdd) currentBank + amount else currentBank - amount
            userPreferences.saveBalances(currentCash, newBank)
        }
    }
}