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

/**
 * 앱 하단에 고정되는 바텀 네비게이션 바
 *
 * @param navController 화면 이동을 제어하는 NavController
 * @param userInfo 탭 이동 시 전달할 사용자 정보
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    userInfo: NavigationRoute.Main
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        bottomNavigationItems.forEach { item ->
            // Type-Safe 방식으로 현재 화면 확인
            // 각 Route의 클래스 타입으로 비교
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
                        // 탭에 따라 적절한 Route 인스턴스 생성
                        val destination = when (item.label) {
                            "Home" -> NavigationRoute.Home(
                                userId = userInfo.userId,
                                userNickname = userInfo.userNickname,
                                userExtra = userInfo.userExtra,
                                userPw = userInfo.userPw
                            )
                            "Search" -> NavigationRoute.Search
                            "My" -> NavigationRoute.My(
                                userId = userInfo.userId,
                                userNickname = userInfo.userNickname,
                                userExtra = userInfo.userExtra,
                                userPw = userInfo.userPw
                            )
                            else -> return@NavigationBarItem
                        }

                        navController.navigate(destination) {
                            // Home 탭까지 백스택을 정리하고 상태 저장
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // 같은 탭을 여러 번 클릭해도 스택에 쌓이지 않도록
                            launchSingleTop = true
                            // 이전에 해당 탭에 있었던 상태를 복원
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