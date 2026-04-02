package com.example.consego.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consego.data.data_store.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(userPreferences: UserPreferences) : ViewModel() {
    val cashBalance = userPreferences.cashBalance.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val bankBalance = userPreferences.bankBalance.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val totalBalance = combine(cashBalance, bankBalance) { c, b -> c + b }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}