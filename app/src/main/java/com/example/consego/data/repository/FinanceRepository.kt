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

    suspend fun getTransactionById(id: Int): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }

    suspend fun insert(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
        adjustBalance(transaction, isUndo = false)
    }

    suspend fun delete(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
        adjustBalance(transaction, isUndo = true)
    }

    suspend fun update(newTransaction: TransactionEntity) {
        val oldTransaction = transactionDao.getTransactionById(newTransaction.id) ?: return
        adjustBalance(oldTransaction, isUndo = true)
        transactionDao.updateTransaction(newTransaction)
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