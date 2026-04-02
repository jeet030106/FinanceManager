package com.example.consego.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.consego.ui.feature.home.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val total by viewModel.totalBalance.collectAsState()
    val cash by viewModel.cashBalance.collectAsState()
    val bank by viewModel.bankBalance.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FF))) {
        Box(
            modifier = Modifier.fillMaxWidth().height(260.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF8E6CEF), Color(0xFF744BD7))),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
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
        Text("Recent Transactions", Modifier.padding(24.dp), fontWeight = FontWeight.Bold)
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
//        Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            Text(amount, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}