package com.example.consego.ui.features.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consego.data.data_store.UserPreferences
import com.example.consego.data.model.AddTransactionUiState
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType
import com.example.consego.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState = _uiState.asStateFlow()

    private val incomeCategories = listOf("Salary", "Investment", "Sale", "Others")
    private val expenseCategories = listOf("Rent", "Food & Shopping", "Trip", "Groceries", "Movies", "Others")

    fun onAmountChange(amount: String) = _uiState.update { it.copy(amount = amount) }
    fun onCategoryChange(category: String) = _uiState.update { it.copy(category = category) }
    fun onNotesChange(notes: String) = _uiState.update { it.copy(notes = notes) }
    fun onAccountTypeChange(account: String) = _uiState.update { it.copy(accountType = account) }

    fun onTypeChange(type: TransactionType) {
        _uiState.update {
            it.copy(
                type = type,
                category = if (type == TransactionType.INCOME) incomeCategories[0] else expenseCategories[0]
            )
        }
    }

    fun getCategories(): List<String> = if (_uiState.value.type == TransactionType.INCOME) incomeCategories else expenseCategories

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            repository.getTransactionById(id)?.let { entity ->
                _uiState.update {
                    it.copy(
                        id = entity.id,
                        amount = entity.amount.toString(),
                        type = entity.type,
                        category = entity.category,
                        notes = entity.notes,
                        date = entity.date,
                        accountType = entity.accountType
                    )
                }
            }
        }
    }

    fun saveTransaction(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val amountDouble = state.amount.toDoubleOrNull() ?: 0.0

        if (amountDouble <= 0) {
            onError("Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            val transaction = TransactionEntity(
                id = state.id ?: 0,
                amount = amountDouble,
                type = state.type,
                category = state.category,
                date = state.date,
                notes = state.notes,
                accountType = state.accountType
            )

            try {
                if (state.id == null) repository.insert(transaction)
                else repository.update(transaction)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error saving transaction")
            }
        }
    }
}