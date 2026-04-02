package com.example.consego.ui.features.navigation

import kotlinx.serialization.Serializable

@Serializable sealed interface NavRoutes {
    @Serializable object Onboarding : NavRoutes
    @Serializable object BalanceSetup : NavRoutes
    @Serializable object Home : NavRoutes
    @Serializable object Stats : NavRoutes
    @Serializable object Profile : NavRoutes
}