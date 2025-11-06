package com.sopt.dive.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sopt.dive.core.ui.navigation.NavigationRoute
import com.sopt.dive.feature.My.MyScreen
import com.sopt.dive.feature.home.HomeScreen
import com.sopt.dive.feature.main.components.BottomNavigationBar
import com.sopt.dive.feature.search.SearchScreen

/**
 * 메인 화면 컨테이너 - 바텀 네비게이션을 포함하는 Scaffold
 *
 * 이 화면은 로그인 성공 후 표시되며, Home, Search, My 세 개의 탭을 제공.
 * 각 탭은 독립적인 화면이지만 사용자 정보를 공유합니다.
 *
 * @param mainRoute 로그인 시 전달받은 사용자 정보를 담고 있는 Route 객체
 */
@Composable
fun MainContainerScreen(
    mainRoute: NavigationRoute.MainContainer
) {
    // 바텀 네비게이션 탭 간 이동을 위한 별도의 NavController를 생성
    // 이것은 앱 전체 네비게이션과는 독립적으로 동작하는 내부 네비게이션입
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = bottomNavController,
                userInfo = mainRoute
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = NavigationRoute.Home,  // 파라미터 제거
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<NavigationRoute.Home> {
                HomeScreen(
                    userId = mainRoute.userId,  // mainRoute에서 직접 전달
                    userNickname = mainRoute.userNickname
                )
            }

            composable<NavigationRoute.Search> {
                SearchScreen()
            }

            composable<NavigationRoute.My> {
                MyScreen(
                    userId = mainRoute.userId,  // mainRoute에서 직접 전달
                    userNickname = mainRoute.userNickname,
                    userExtra = mainRoute.userExtra,
                    userPw = mainRoute.userPw
                )
            }
        }
    }
}


// Preview 함수는 UI 확인용으로만 사용되므로 private으로 선언
@Preview(showBackground = true, name = "Main Container Screen")
@Composable
private fun MainContainerScreenPreview() {
    // Preview를 위한 더미 사용자 데이터 생성
    val dummyMainRoute = NavigationRoute.MainContainer(
        userId = "previewUser",
        userNickname = "미리보기",
        userExtra = "가위",
        userPw = "preview123"
    )

    MainContainerScreen(mainRoute = dummyMainRoute)
}