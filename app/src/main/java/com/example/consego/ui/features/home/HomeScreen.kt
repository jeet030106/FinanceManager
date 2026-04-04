package com.example.consego.ui.features.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType
import com.example.consego.ui.feature.home.HomeViewModel


@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val total by viewModel.totalBalance.collectAsState()
    val cash by viewModel.cashBalance.collectAsState()
    val bank by viewModel.bankBalance.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()

    // Pie Chart Data from ViewModel
    val pieData by viewModel.todayTransactionsPieData.collectAsState()
    val todayTotal by viewModel.todayTotal.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFcmToken()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FF))) {
        // 1. Balance Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF8E6CEF), Color(0xFF744BD7))),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text("Total Balance", color = Color.White.copy(0.7f))
                Text("$$total", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    BalanceHeaderItem("Cash", "$$cash", Modifier.weight(1f))
                    BalanceHeaderItem("Bank", "$$bank", Modifier.weight(1f))
                }
            }
        }

        // We use LazyColumn for the whole screen content to allow scrolling
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Today's Expenditure Card
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("Today's Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        // Numeric Total Display
                        Text(
                            text = "Total: $${String.format("%.2f", todayTotal)}",
                            color = Color(0xFF744BD7),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .padding(20.dp)
                    ) {
                        EnhancedPieChart(data = pieData, total = todayTotal)
                    }
                }
            }

            item {
                Text("Recent Transactions", Modifier.padding(horizontal = 24.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
            }

            items(recentTransactions) { transaction ->
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun EnhancedPieChart(data: Map<String, Double>, total: Double) {
    val colors = listOf(
        Color(0xFF744BD7), Color(0xFFF44336), Color(0xFF4CAF50),
        Color(0xFFFFEB3B), Color(0xFF2196F3), Color(0xFF9C27B0)
    )

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
            if (total == 0.0) {
                Text("No Data", fontSize = 12.sp, color = Color.Gray)
            } else {
                Canvas(modifier = Modifier.size(110.dp)) {
                    var startAngle = -90f
                    val gap = if (data.size > 1) 3f else 0f
                    data.values.forEachIndexed { index, value ->
                        val sweep = (value.toFloat() / total.toFloat()) * 360f
                        drawArc(
                            color = colors[index % colors.size],
                            startAngle = startAngle + (gap / 2),
                            sweepAngle = sweep - gap,
                            useCenter = true
                        )
                        startAngle += sweep
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.entries.take(4).forEachIndexed { index, entry -> // Showing top 4 for space
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(colors[index % colors.size]))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(entry.key, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val percentage = if(total > 0) (entry.value / total) * 100 else 0.0
                        Text("${String.format("%.1f", percentage)}% • $${entry.value.toInt()}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ... TransactionItem and BalanceHeaderItem remain as they were
@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF1F1F1), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                transaction.category.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF744BD7)
            )
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

@Composable
fun BalanceHeaderItem(label: String, amount: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            Text(amount, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}