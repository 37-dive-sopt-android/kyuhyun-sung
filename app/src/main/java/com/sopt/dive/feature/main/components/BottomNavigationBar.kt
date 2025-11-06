package com.sopt.dive.feature.main.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sopt.dive.core.ui.navigation.NavigationRoute
import com.sopt.dive.feature.main.bottomNavigationItems

@Composable
fun BottomNavigationBar(
    navController: NavController,
    userInfo: NavigationRoute.MainContainer  // Main → MainContainer로 변경
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        bottomNavigationItems.forEach { item ->
            val isSelected = when (item.label) {
                "Home" -> currentDestination?.hasRoute<NavigationRoute.Home>() == true
                "Search" -> currentDestination?.hasRoute<NavigationRoute.Search>() == true
                "My" -> currentDestination?.hasRoute<NavigationRoute.My>() == true
                else -> false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        val destination = when (item.label) {
                            "Home" -> NavigationRoute.Home  // 파라미터 제거
                            "Search" -> NavigationRoute.Search
                            "My" -> NavigationRoute.My  // 파라미터 제거
                            else -> return@NavigationBarItem
                        }

                        navController.navigate(destination) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.LightGray
                )
            )
        }
    }
}