package com.example.consego.ui.features.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consego.data.model.TransactionType
import com.example.consego.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

enum class InsightTab { EXPENDITURE, SAVINGS }

@HiltViewModel
class InsightsViewModel @Inject constructor(
    repository: FinanceRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(InsightTab.EXPENDITURE)
    val selectedTab = _selectedTab.asStateFlow()

    private val allTransactions = repository.allTransactions

    // 1. Current Week Category-wise Expenditure (Mon-Sun)
    val weeklyCategoryExpenditure = allTransactions.map { list ->
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val startOfThisMonday = cal.timeInMillis

        list.filter { it.date >= startOfThisMonday && it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { trans -> trans.amount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // 2. Monthly Comparison Logic
    val monthlyStats = allTransactions.map { list ->
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        val currentTotal = list.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear && it.type == TransactionType.EXPENSE
        }.sumOf { it.amount }

        val lastMonthCal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
        val lastMonth = lastMonthCal.get(Calendar.MONTH)
        val lastYear = lastMonthCal.get(Calendar.YEAR)

        val lastTotal = list.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == lastMonth && c.get(Calendar.YEAR) == lastYear && it.type == TransactionType.EXPENSE
        }.sumOf { it.amount }

        val diff = if (lastTotal > 0) ((currentTotal - lastTotal) / lastTotal) * 100 else 0.0
        Triple(currentTotal, lastTotal, diff)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0.0, 0.0, 0.0))

    // 3. Last 6 Months Expenditure (for Bar Graph)
    val last6MonthsExpenditure = allTransactions.map { list ->
        val result = mutableMapOf<String, Double>()
        val monthFormat = java.text.SimpleDateFormat("MMM", Locale.getDefault())

        for (i in 0..5) {
            val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            val total = list.filter {
                val c = Calendar.getInstance().apply { timeInMillis = it.date }
                c.get(Calendar.MONTH) == m && c.get(Calendar.YEAR) == y && it.type == TransactionType.EXPENSE
            }.sumOf { it.amount }

            result[monthFormat.format(cal.time)] = total
        }
        result.toList().reversed().toMap()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // --- NEW SAVINGS LOGIC ---

    // 4. Savings Trend (Calculates cumulative savings over time)
    val savingsTrend = allTransactions.map { list ->
        if (list.isEmpty()) return@map listOf(0.0, 0.0)

        // Sort by date to calculate progress correctly
        val sortedList = list.sortedBy { it.date }
        val points = mutableListOf<Double>()
        var runningBalance = 0.0

        sortedList.forEach { transaction ->
            if (transaction.type == TransactionType.INCOME) {
                runningBalance += transaction.amount
            } else {
                runningBalance -= transaction.amount
            }
            points.add(runningBalance)
        }

        if (points.size < 2) listOf(0.0, runningBalance) else points
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(0.0, 0.0))

    // 5. Savings Rate (Percentage of income saved this month)
    val savingsRate = allTransactions.map { list ->
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        val monthlyTransactions = list.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear
        }

        val income = monthlyTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = monthlyTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val savings = income - expense
        if (income > 0) (savings / income) * 100 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun selectTab(tab: InsightTab) { _selectedTab.value = tab }
}