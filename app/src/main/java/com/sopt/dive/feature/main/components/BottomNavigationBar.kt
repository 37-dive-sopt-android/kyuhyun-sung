package com.sopt.dive.feature.main.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sopt.dive.core.ui.navigation.NavigationRoute
import com.sopt.dive.feature.main.bottomNavigationItems

/**
 * 앱 하단에 고정되는 바텀 네비게이션 바
 *
 * @param navController 화면 이동을 제어하는 NavController
 *
 * 현재 선택된 탭을 자동으로 하이라이트하고,
 * 탭 클릭 시 해당 화면으로 이동합니다
 */
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    // 현재 네비게이션 스택의 최상단 엔트리를 관찰합니다
    // 화면이 변경될 때마다 이 값이 자동으로 업데이트되어 리컴포지션이 발생합니다
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        bottomNavigationItems.forEach { item ->
            // 현재 보고 있는 화면의 route와 이 아이템의 route가 같은지 확인
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // 같은 탭을 다시 클릭하면 무시합니다
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // 백스택 관리: Home으로 돌아갈 때까지의 모든 화면을 제거
                            popUpTo(NavigationRoute.Home.route) {
                                saveState = true
                            }
                            // 같은 화면을 여러 번 추가하지 않도록 방지
                            launchSingleTop = true
                            // 탭을 다시 클릭했을 때 이전 상태를 복원
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