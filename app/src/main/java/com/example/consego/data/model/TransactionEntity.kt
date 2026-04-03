package com.example.consego.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.consego.data.model.TransactionType
import kotlinx.serialization.Serializable

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val type: TransactionType, // "EXPENSE" or "INCOME"
    val category: String,      // e.g., "Food", "Salary"
    val date: Long,            // Timestamp (Essential for Weekly/Daily graphs)
    val notes: String,
    val accountType: String    // "CASH" or "BANK" (So balance updates correctly)
)

@Serializable
enum class TransactionType(val title: String) {
    EXPENSE("Expense"),
    INCOME("Income")
}