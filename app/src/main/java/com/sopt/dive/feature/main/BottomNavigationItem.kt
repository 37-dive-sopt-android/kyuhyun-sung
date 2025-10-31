package com.sopt.dive.feature.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 바텀 네비게이션 각 탭의 정보를 담는 데이터 클래스
 *
 * @param icon 탭에 표시될 아이콘
 * @param label 탭 하단에 표시될 텍스트 (탭 식별에도 사용)
 */
data class BottomNavigationItem(
    val icon: ImageVector,
    val label: String
)

/**
 * 앱에서 사용할 바텀 네비게이션 탭 목록
 * 나중에 탭을 추가하거나 순서를 변경하려면 이 리스트만 수정하기
 */
val bottomNavigationItems = listOf(
    BottomNavigationItem(
        icon = Icons.Filled.Home,
        label = "Home"
    ),
    BottomNavigationItem(
        icon = Icons.Filled.Search,
        label = "Search"
    ),
    BottomNavigationItem(
        icon = Icons.Filled.Person,
        label = "My"
    )
)