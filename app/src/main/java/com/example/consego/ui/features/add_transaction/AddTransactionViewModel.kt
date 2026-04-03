package com.example.consego.ui.features.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consego.data.model.AddTransactionUiState
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType
import com.example.consego.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState = _uiState.asStateFlow()

    val categories = listOf("Food", "Salary", "Transport", "Rent", "Groceries", "Shopping", "Entertainment")

    fun onAmountChange(amount: String) = _uiState.update { it.copy(amount = amount) }
    fun onTypeChange(type: TransactionType) = _uiState.update { it.copy(type = type) }
    fun onCategoryChange(category: String) = _uiState.update { it.copy(category = category) }
    fun onNotesChange(notes: String) = _uiState.update { it.copy(notes = notes) }
    fun onAccountTypeChange(account: String) = _uiState.update { it.copy(accountType = account) }

    fun saveTransaction(onSuccess: () -> Unit) {
        val state = _uiState.value
        val amountDouble = state.amount.toDoubleOrNull() ?: 0.0

        if (amountDouble > 0) {
            viewModelScope.launch {
                val transaction = TransactionEntity(
                    amount = amountDouble,
                    type = state.type,
                    category = state.category,
                    date = state.date,
                    notes = state.notes,
                    accountType = state.accountType
                )
                repository.insert(transaction)
                onSuccess()
            }
        }
    }
}