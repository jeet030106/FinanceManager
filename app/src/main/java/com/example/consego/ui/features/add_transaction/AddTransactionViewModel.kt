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
    private val repository: FinanceRepository,
    private val userPreferences: UserPreferences // Inject UserPreferences to access saveBalances
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState = _uiState.asStateFlow()

    private val incomeCategories = listOf("Salary", "Investment", "Sale", "Others")
    private val expenseCategories = listOf("Rent", "Food & Shopping", "Trip", "Groceries", "Movies", "Others")

    fun getCategories(): List<String> {
        return if (_uiState.value.type == TransactionType.INCOME) incomeCategories else expenseCategories
    }

    fun onAmountChange(amount: String) = _uiState.update { it.copy(amount = amount) }

    fun onTypeChange(type: TransactionType) {
        _uiState.update {
            it.copy(
                type = type,
                category = if (type == TransactionType.INCOME) incomeCategories[0] else expenseCategories[0]
            )
        }
    }

    fun onCategoryChange(category: String) = _uiState.update { it.copy(category = category) }
    fun onNotesChange(notes: String) = _uiState.update { it.copy(notes = notes) }
    fun onAccountTypeChange(account: String) = _uiState.update { it.copy(accountType = account) }

    // saveTransaction now handles balance validation and updates
    fun saveTransaction(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val amountDouble = state.amount.toDoubleOrNull() ?: 0.0

        if (amountDouble > 0) {
            viewModelScope.launch {
                // 1. Get current balances from UserPreferences
                val currentCash = userPreferences.cashBalance.first()
                val currentBank = userPreferences.bankBalance.first()

                // 2. Determine which balance to check/update
                val isCash = state.accountType == "CASH"
                val relevantBalance = if (isCash) currentCash else currentBank

                // 3. Decline if it's an expense and exceeds the available balance
                if (state.type == TransactionType.EXPENSE && amountDouble > relevantBalance) {
                    onError("Insufficient funds in ${state.accountType} account")
                    return@launch
                }

                // 4. Save the transaction to the database
                val transaction = TransactionEntity(
                    amount = amountDouble,
                    type = state.type,
                    category = state.category,
                    date = state.date,
                    notes = state.notes,
                    accountType = state.accountType
                )
                repository.insert(transaction)

                // 5. Calculate new balances and update DataStore
                if (isCash) {
                    val newCash = if (state.type == TransactionType.EXPENSE) currentCash - amountDouble else currentCash + amountDouble
                    userPreferences.saveBalances(newCash, currentBank)
                } else {
                    val newBank = if (state.type == TransactionType.EXPENSE) currentBank - amountDouble else currentBank + amountDouble
                    userPreferences.saveBalances(currentCash, newBank)
                }

                onSuccess()
            }
        } else {
            onError("Please enter a valid amount")
        }
    }
}