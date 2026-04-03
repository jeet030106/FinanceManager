package com.example.consego.ui.features.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType
import com.example.consego.data.repository.FinanceRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionHistoryScreen(viewModel: TransactionHistoryViewModel = hiltViewModel()) {
    val allTransactions by viewModel.allTransactions.collectAsState(initial = emptyList())

    val grouped = allTransactions.groupBy {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it.date))
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        grouped.forEach { (date, transactions) ->
            item {
                Text(date, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}


@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFFF1F1F1), CircleShape),
            contentAlignment = Alignment.Center
        ) {

        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(transaction.category, fontWeight = FontWeight.Bold)
            Text(transaction.notes, fontSize = 12.sp, color = Color.Gray)
        }
        Text(
            text = (if (transaction.type == TransactionType.EXPENSE) "-" else "+") + "$${transaction.amount}",
            color = if (transaction.type == TransactionType.EXPENSE) Color.Red else Color(0xFF4CAF50),
            fontWeight = FontWeight.Bold
        )
    }
}