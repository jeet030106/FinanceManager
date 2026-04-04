package com.example.consego.ui.features.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.consego.ui.features.graph.BarGraphWithAxes
import com.example.consego.ui.features.graph.EnhancedPieChart
import com.example.consego.ui.features.graph.LineGraphWithAxes

@Composable
fun InsightsScreen(onGoalClick: () -> Unit, viewModel: InsightsViewModel = hiltViewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val weeklyData by viewModel.weeklyCategoryExpenditure.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val barData by viewModel.last6MonthsExpenditure.collectAsState()
    val savingsTrend by viewModel.savingsTrend.collectAsState()
    val savingsRate by viewModel.savingsRate.collectAsState()

    val isGoalActive by viewModel.isGoalActive.collectAsState()
    val goalLimit by viewModel.goalDailyLimit.collectAsState()
    val todaySpent by viewModel.todayExpenditure.collectAsState()
    val streakInfo by viewModel.streakProgress.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Insights", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            InsightTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                        .background(if (isSelected) Color(0xFF744BD7) else Color.Transparent)
                        .clickable { viewModel.selectTab(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (selectedTab == InsightTab.EXPENDITURE) {
            ExpenditureInsights(weeklyData, monthlyStats, barData)
        } else {
            SavingsInsights(
                savingsPoints = savingsTrend,
                savingsRate = savingsRate,
                isGoalActive = isGoalActive,
                goalLimit = goalLimit,
                todaySpent = todaySpent,
                streakInfo = streakInfo,
                onStartGoal = { limit, days -> viewModel.startGoal(limit, days) },
                onResetGoal = { viewModel.resetGoal() }
            )
        }
    }
}

@Composable
fun SavingsInsights(
    savingsPoints: List<Double>,
    savingsRate: Double,
    isGoalActive: Boolean,
    goalLimit: Double,
    todaySpent: Double,
    streakInfo: Pair<Int, Int>,
    onStartGoal: (Double, Int) -> Unit,
    onResetGoal: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            SavingsGoalHero(
                isActive = isGoalActive,
                dailyLimit = goalLimit,
                todaySpent = todaySpent,
                streakDay = streakInfo.first,
                totalDays = streakInfo.second,
                onStartGoal = onStartGoal,
                onReset = onResetGoal
            )
        }

        item { Text("Savings Performance", fontWeight = FontWeight.Bold, fontSize = 18.sp) }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Savings", fontSize = 12.sp, color = Color(0xFF388E3C))
                        Text("$${savingsPoints.lastOrNull()?.toInt() ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Savings Rate", fontSize = 12.sp, color = Color(0xFF1976D2))
                        Text("${String.format("%.1f", savingsRate)}%", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F1F1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Savings Trend", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (savingsPoints.size < 2) {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Not enough data", color = Color.Gray)
                        }
                    } else {
                        LineGraphWithAxes(savingsPoints)
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsGoalHero(
    isActive: Boolean,
    dailyLimit: Double,
    todaySpent: Double,
    streakDay: Int,
    totalDays: Int,
    onStartGoal: (Double, Int) -> Unit,
    onReset: () -> Unit
) {
    var showSetupDialog by remember { mutableStateOf(false) }

    if (isActive) {
        val isExceeded = todaySpent > dailyLimit
        val remaining = (dailyLimit - todaySpent).coerceAtLeast(0.0)
        val daysLeft = (totalDays - streakDay).coerceAtLeast(0)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(if (isExceeded) Color(0xFFEF5350) else Color(0xFF744BD7))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExceeded) "Streak Broken!" else "Day $streakDay of $totalDays",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (!isExceeded) {
                        Text(
                            text = "$daysLeft days left",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }

                if (!isExceeded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { streakDay.toFloat() / totalDays.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Spent Today: $${String.format("%.2f", todaySpent)}",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (isExceeded) "Limit was $${dailyLimit.toInt()}" else "Limit: $${dailyLimit.toInt()} | Safe to spend: $${String.format("%.2f", remaining)}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            Surface(
                onClick = onReset,
                modifier = Modifier.align(Alignment.BottomEnd),
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Restart Goal",
                    color = if (isExceeded) Color.Red else Color(0xFF744BD7),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF744BD7), Color(0xFF5A36B1))))
                .clickable { showSetupDialog = true }
                .padding(24.dp)
        ) {
            Column {
                Icon(Icons.Default.Star, null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Start a Savings Goal", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Set a daily target and build your streak today!", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
            Surface(
                onClick = { showSetupDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd),
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Get Started", color = Color(0xFF744BD7), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
            }
        }
    }

    if (showSetupDialog) {
        var limitInput by remember { mutableStateOf("") }
        var daysInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSetupDialog = false },
            title = { Text("Setup Savings Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it },
                        label = { Text("Daily Expense Limit ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = daysInput,
                        onValueChange = { daysInput = it },
                        label = { Text("Number of Days for Streak") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val l = limitInput.toDoubleOrNull() ?: 0.0
                    val d = daysInput.toIntOrNull() ?: 0
                    if (l > 0 && d > 0) {
                        onStartGoal(l, d)
                        showSetupDialog = false
                    }
                }) { Text("Start") }
            },
            dismissButton = {
                TextButton(onClick = { showSetupDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ExpenditureInsights(weeklyData: Map<String, Double>, monthlyStats: Triple<Double, Double, Double>, barData: Map<String, Double>) {
    val weeklyTotal = weeklyData.values.sum()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Column {
                Text("This Week (Mon - Sun)", color = Color.Gray, fontSize = 14.sp)
                Text(text = "$${String.format("%.2f", weeklyTotal)}", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color(0xFF744BD7))
            }
        }
        item { EnhancedPieChart(weeklyData, weeklyTotal) }
        item {
            Text("6-Month Expenditure Trend", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            BarGraphWithAxes(barData)
        }
    }
}