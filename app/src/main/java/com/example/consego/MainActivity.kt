package com.example.consego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.consego.data.data_store.UserPreferences
import com.example.consego.ui.features.add_transaction.AddTransactionScreen
import com.example.consego.ui.features.balance_setup.BalanceSetupScreen
import com.example.consego.ui.features.home.HomeScreen
import com.example.consego.ui.features.insights.InsightsScreen
import com.example.consego.ui.features.navigation.NavRoutes
import com.example.consego.ui.features.onboarding.OnBoardingScreen
import com.example.consego.ui.features.transaction_history.TransactionHistoryScreen
import com.example.consego.ui.theme.ConsegoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ConsegoTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val showBottomBar = remember { mutableStateOf(false) }

                val onboardingDone by userPreferences.isOnboardingCompleted.collectAsState(initial = null)
                val balanceDone by userPreferences.isBalanceSetupCompleted.collectAsState(initial = null)

                if (onboardingDone != null && balanceDone != null) {
                    Scaffold(
                        bottomBar = {
                            AnimatedVisibility(visible = showBottomBar.value) {
                                NavigationBar(containerColor = Color.White) {
                                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                                    val currentDest = navBackStackEntry?.destination

                                    val items = listOf(
                                        Triple(NavRoutes.Home, Icons.Default.Home, "Home"),
                                        Triple(NavRoutes.History, Icons.Default.AccountBox, "History"),
                                        Triple(NavRoutes.AddTransaction, Icons.Default.Add, "Add"),
                                        Triple(NavRoutes.Insights, Icons.Default.DateRange, "Insights")
                                    )

                                    items.forEach { (route, icon, label) ->
                                        val isAddButton = route is NavRoutes.AddTransaction

                                        val selected = currentDest?.hierarchy?.any {
                                            it.hasRoute(route::class)
                                        } == true && !isAddButton

                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = {
                                                if (isAddButton) {

                                                    navController.navigate(route)
                                                } else {

                                                    navController.navigate(route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                            icon = {
                                                if (isAddButton) {
                                                    // Special styling for the Add (+) icon
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color(0xFF744BD7),
                                                        modifier = Modifier.size(42.dp)
                                                    ) {
                                                        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.padding(8.dp))
                                                    }
                                                } else {
                                                    Icon(icon, contentDescription = label, tint = if(selected) Color(0xFF744BD7) else Color.Gray)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = if (onboardingDone == true && balanceDone == true) NavRoutes.Home else NavRoutes.Onboarding,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<NavRoutes.Onboarding> {
                                showBottomBar.value = false
                                OnBoardingScreen(onFinish = {
                                    scope.launch {
                                        userPreferences.saveOnboardingDone()
                                        navController.navigate(NavRoutes.BalanceSetup)
                                    }
                                })
                            }
                            composable<NavRoutes.BalanceSetup> {
                                showBottomBar.value = false
                                BalanceSetupScreen(onComplete = { cash, bank ->
                                    scope.launch {
                                        userPreferences.saveBalances(cash, bank)
                                        userPreferences.saveBalanceDone()
                                        navController.navigate(NavRoutes.Home) {
                                            popUpTo(NavRoutes.BalanceSetup) { inclusive = true }
                                        }
                                    }
                                })
                            }
                            composable<NavRoutes.Home> {
                                showBottomBar.value = true
                                HomeScreen() // No arguments passed here
                            }
                            composable<NavRoutes.History> {
                                showBottomBar.value = true
                                TransactionHistoryScreen()
                            }
                            composable<NavRoutes.AddTransaction> {
                                AddTransactionScreen(onBack = { navController.popBackStack() })
                            }
                            composable<NavRoutes.Insights> {
                                showBottomBar.value = true
                                InsightsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}