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
    val type: TransactionType,
    val category: String,
    val date: Long,
    val notes: String,
    val accountType: String
)

@Serializable
enum class TransactionType(val title: String) {
    EXPENSE("Expense"),
    INCOME("Income")
}