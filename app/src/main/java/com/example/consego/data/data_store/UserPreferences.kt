package com.example.consego.data.data_store


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val IS_ONBOARDING_DONE = booleanPreferencesKey("is_onboarding_done")
        val IS_BALANCE_DONE = booleanPreferencesKey("is_balance_done")
        val CASH_BAL = doublePreferencesKey("cash_bal")
        val BANK_BAL = doublePreferencesKey("bank_bal")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[IS_ONBOARDING_DONE] ?: false }
    val isBalanceSetupCompleted: Flow<Boolean> = context.dataStore.data.map { it[IS_BALANCE_DONE] ?: false }
    val cashBalance: Flow<Double> = context.dataStore.data.map { it[CASH_BAL] ?: 0.0 }
    val bankBalance: Flow<Double> = context.dataStore.data.map { it[BANK_BAL] ?: 0.0 }

    suspend fun saveOnboardingDone() { context.dataStore.edit { it[IS_ONBOARDING_DONE] = true } }
    suspend fun saveBalanceDone() { context.dataStore.edit { it[IS_BALANCE_DONE] = true } }
    suspend fun saveBalances(cash: Double, bank: Double) {
        context.dataStore.edit { it[CASH_BAL] = cash; it[BANK_BAL] = bank }
    }
}