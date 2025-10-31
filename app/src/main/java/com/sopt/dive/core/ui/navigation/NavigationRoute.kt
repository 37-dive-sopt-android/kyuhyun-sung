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

    // 파라미터를 전달해야 하는 화면은 data class로 사용
    @Serializable
    data class Main(
        val userId: String,
        val userNickname: String,
        val userExtra: String,
        val userPw: String
    ) : NavigationRoute

    @Serializable
    data class Home(
        val userId: String = "",
        val userNickname: String = "",
        val userExtra: String = "",
        val userPw: String = ""
    ) : NavigationRoute

    @Serializable
    data object Search : NavigationRoute

    @Serializable
    data class My(
        val userId: String = "",
        val userNickname: String = "",
        val userExtra: String = "",
        val userPw: String = ""
    ) : NavigationRoute
}