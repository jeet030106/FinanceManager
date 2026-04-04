package com.example.consego.ui.features.transaction_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType
import com.example.consego.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TransactionFilter { ALL, INCOME, EXPENSE }

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TransactionFilter.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()


    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        repository.allTransactions,
        _selectedFilter
    ) { transactions, filter ->
        when (filter) {
            TransactionFilter.ALL -> transactions
            TransactionFilter.INCOME -> transactions.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> transactions.filter { it.type == TransactionType.EXPENSE }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateFilter(filter: TransactionFilter) {
        _selectedFilter.value = filter
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}