package com.example.consego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.example.consego.data.data_store.UserPreferences
import com.example.consego.ui.features.balance_setup.BalanceSetupScreen
import com.example.consego.ui.features.home.HomeScreen
import com.example.consego.ui.features.navigation.NavRoutes
import com.example.consego.ui.features.onboarding.OnBoardingScreen
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
                                        Triple(NavRoutes.Stats, Icons.Default.Person, "Stats"),
                                        Triple(NavRoutes.Profile, Icons.Default.Person, "Profile")
                                    )

                                    items.forEach { (route, icon, label) ->
                                        val selected = currentDest?.hierarchy?.any { it.hasRoute(route::class) } == true
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = { navController.navigate(route) },
                                            icon = { Icon(icon, contentDescription = label, tint = if(selected) Color(0xFF744BD7) else Color.Gray) }
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
                                // Simplified Onboarding Call
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
                                        navController.navigate(NavRoutes.Home)
                                    }
                                })
                            }
                            composable<NavRoutes.Home> {
                                showBottomBar.value = true
                                HomeScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}