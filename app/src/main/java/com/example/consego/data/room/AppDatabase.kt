package com.example.consego.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.consego.data.model.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}