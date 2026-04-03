package com.example.consego.ui.features.transaction_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(private val repository: FinanceRepository): ViewModel() {


    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}