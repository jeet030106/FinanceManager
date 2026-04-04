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

    // --- GOAL STATES ---
    private val _isGoalActive = MutableStateFlow(false)
    val isGoalActive = _isGoalActive.asStateFlow()

    private val _goalDailyLimit = MutableStateFlow(0.0)
    val goalDailyLimit = _goalDailyLimit.asStateFlow()

    private val _goalTotalDays = MutableStateFlow(0)
    val goalTotalDays = _goalTotalDays.asStateFlow()

    // 1. Weekly Expenditure
    val weeklyCategoryExpenditure = allTransactions.map { list ->
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfThisMonday = cal.timeInMillis

        list.filter { it.date >= startOfThisMonday && it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { trans -> trans.amount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // 2. Monthly Comparison
    val monthlyStats = allTransactions.map { list ->
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        val currentTotal = list.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear && it.type == TransactionType.EXPENSE
        }.sumOf { it.amount }

        val lastMonthCal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
        val lastTotal = list.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == lastMonthCal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == lastMonthCal.get(Calendar.YEAR) && it.type == TransactionType.EXPENSE
        }.sumOf { it.amount }

        val diff = if (lastTotal > 0) ((currentTotal - lastTotal) / lastTotal) * 100 else 0.0
        Triple(currentTotal, lastTotal, diff)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0.0, 0.0, 0.0))

    // 3. Last 6 Months
    val last6MonthsExpenditure = allTransactions.map { list ->
        val result = mutableMapOf<String, Double>()
        val monthFormat = java.text.SimpleDateFormat("MMM", Locale.getDefault())
        for (i in 0..5) {
            val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }
            val total = list.filter {
                val c = Calendar.getInstance().apply { timeInMillis = it.date }
                c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && it.type == TransactionType.EXPENSE
            }.sumOf { it.amount }
            result[monthFormat.format(cal.time)] = total
        }
        result.toList().reversed().toMap()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // 4. Savings Trend
    val savingsTrend = allTransactions.map { list ->
        if (list.isEmpty()) return@map listOf(0.0, 0.0)
        val sortedList = list.sortedBy { it.date }
        val points = mutableListOf<Double>()
        var runningBalance = 0.0
        sortedList.forEach {
            runningBalance += if (it.type == TransactionType.INCOME) it.amount else -it.amount
            points.add(runningBalance)
        }
        if (points.size < 2) listOf(0.0, runningBalance) else points
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(0.0, 0.0))

    // 5. Savings Rate
    val savingsRate = allTransactions.map { list ->
        val now = Calendar.getInstance()
        val currentMonthTransactions = list.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == now.get(Calendar.MONTH) && c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        }
        val income = currentMonthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = currentMonthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        if (income > 0) ((income - expense) / income) * 100 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // 6. Today's Expenditure Logic (New)
    val todayExpenditure = allTransactions.map { list ->
        val now = Calendar.getInstance()
        list.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                    c.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    it.type == TransactionType.EXPENSE
        }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun startGoal(limit: Double, days: Int) {
        _goalDailyLimit.value = limit
        _goalTotalDays.value = days
        _isGoalActive.value = true
    }

    fun resetGoal() {
        _isGoalActive.value = false
        _goalDailyLimit.value = 0.0
        _goalTotalDays.value = 0
    }

    fun selectTab(tab: InsightTab) { _selectedTab.value = tab }
}