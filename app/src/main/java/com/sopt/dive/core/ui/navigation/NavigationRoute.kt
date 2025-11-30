package com.sopt.dive.core.ui.navigation

import kotlinx.serialization.Serializable

/**
 * 앱 내 모든 화면의 네비게이션 경로를 정의하는 sealed interface
 * @Serializable을 붙여 Type-Safe Navigation 지원
 */
sealed interface NavigationRoute {

    // 파라미터가 없는 화면들은 data object 사용
    @Serializable
    data object Login : NavigationRoute

    @Serializable
    data object SignUp : NavigationRoute

    // 메인 컨테이너 화면 (바텀 네비게이션을 포함하는 상위 화면)
    @Serializable
    data class MainContainer(
        val userId: String,
        val userNickname: String,
        val userEmail: String,    // userExtra → userEmail
        val userAge: Int,         // age 추가
        val userPw: String
    ) : NavigationRoute

    @Serializable
    data object Card : NavigationRoute

    // 바텀 네비게이션 탭들
    @Serializable
    data object Home : NavigationRoute

    @Serializable
    data object Search : NavigationRoute

    @Serializable
    data object My : NavigationRoute
}