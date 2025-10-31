package com.sopt.dive.feature.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.sopt.dive.core.ui.navigation.NavigationRoute

/**
 * 바텀 네비게이션 각 탭의 정보를 담는 데이터 클래스
 *
 * @param route 해당 탭으로 이동할 때 사용할 네비게이션 경로
 * @param icon 탭에 표시될 아이콘
 * @param label 탭 하단에 표시될 텍스트
 */
data class BottomNavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

/**
 * 앱에서 사용할 바텀 네비게이션 탭 목록
 * 나중에 탭을 추가하거나 순서를 변경하려면 이 리스트만 수정.
 */
val bottomNavigationItems = listOf(
    BottomNavigationItem(
        route = NavigationRoute.Home.route,
        icon = Icons.Filled.Home,
        label = "Home"
    ),
    BottomNavigationItem(
        route = NavigationRoute.Search.route,
        icon = Icons.Filled.Search,
        label = "Search"
    ),
    BottomNavigationItem(
        route = NavigationRoute.My.route,
        icon = Icons.Filled.Person,
        label = "My"
    )
)