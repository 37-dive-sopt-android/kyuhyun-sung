package com.sopt.dive.core.ui.navigation

/**
 * 앱 내 모든 화면의 네비게이션 경로를 정의하는 sealed class
 * sealed class를 사용하면 컴파일러가 모든 케이스를 검증할 수 있어 안전
 */
sealed class NavigationRoute(val route: String) {
    // 로그인 화면
    object Login : NavigationRoute("login")

    // 회원가입 화면
    object SignUp : NavigationRoute("signup")

    // 메인 화면 (바텀 네비게이션을 포함하는 컨테이너)
    object Main : NavigationRoute("main")

    // 바텀 네비게이션의 각 탭
    object Home : NavigationRoute("home")
    object Search : NavigationRoute("search")
    object My : NavigationRoute("my")
}