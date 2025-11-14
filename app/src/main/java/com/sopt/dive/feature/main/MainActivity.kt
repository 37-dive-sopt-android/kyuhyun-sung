package com.sopt.dive.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sopt.dive.core.data.AuthPreferences  // ✅ UserPreferences → AuthPreferences
import com.sopt.dive.core.ui.navigation.NavigationRoute
import com.sopt.dive.core.ui.theme.DiveTheme
import com.sopt.dive.feature.card.FlippingCardScreen
import com.sopt.dive.feature.login.LoginScreen
import com.sopt.dive.feature.signup.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiveTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authPreferences = AuthPreferences.getInstance(context)  // 변경

    // 자동 로그인 처리
    LaunchedEffect(Unit) {
        if (authPreferences.isLoggedIn()) {
            val loginInfo = authPreferences.getLoginInfo()
            if (loginInfo != null) {
                // 저장된 userId로 바로 이동 (사용자 정보는 MyScreen에서 API로 가져옴)
                navController.navigate(
                    NavigationRoute.MainContainer(
                        userId = loginInfo.userId.toString(),  // 실제 userId
                        userNickname = "로딩중...",  // 임시값 (API에서 갱신됨)
                        userEmail = "로딩중...",
                        userAge = 0,
                        userPw = loginInfo.password
                    )
                ) {
                    popUpTo(NavigationRoute.Login) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Login,
    ) {
        composable<NavigationRoute.Login> {
            LoginScreen(
                onLoginClick = { userId, password ->
                    //API 로그인 성공 시 AuthPreferences에 저장
                    val userIdInt = userId.toIntOrNull() ?: 0
                    authPreferences.saveLoginInfo(
                        userId = userIdInt,  // 실제 userId (359)
                        username = "unknown",  // username을 모르므로 임시값
                        password = password
                    )

                    navController.navigate(
                        NavigationRoute.MainContainer(
                            userId = userId,  // 실제 userId (359)
                            userNickname = "로딩중...",
                            userEmail = "로딩중...",
                            userAge = 0,
                            userPw = password
                        )
                    ) {
                        popUpTo<NavigationRoute.Login> { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(NavigationRoute.SignUp)
                }
            )
        }

        composable<NavigationRoute.SignUp> {
            SignUpScreen(
                onSignUpClick = { signUpData ->
                    // 로컬 저장 제거
                    navController.navigate(NavigationRoute.Login) {
                        popUpTo<NavigationRoute.SignUp> { inclusive = true }
                    }
                }
            )
        }

        composable<NavigationRoute.MainContainer> { backStackEntry ->
            val mainArgs = backStackEntry.toRoute<NavigationRoute.MainContainer>()

            MainContainerScreen(
                mainRoute = mainArgs
            )
        }

        composable<NavigationRoute.Card> {
            FlippingCardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}