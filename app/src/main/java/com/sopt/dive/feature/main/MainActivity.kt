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
import com.sopt.dive.core.data.UserPreferences
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
    val userPreferences = UserPreferences.getInstance(context)

    // 로그인 상태를 확인하여 자동 로그인을 처리
    LaunchedEffect(Unit) {
        if (userPreferences.isLoggedIn()) {
            // 이미 로그인되어 있다면 저장된 사용자 정보를 불러와서 메인 화면으로 이동
            val userData = userPreferences.getUserData()
            if (userData != null) {
                navController.navigate(
                    NavigationRoute.MainContainer(  // Main → MainContainer
                        userId = userData.userId,
                        userNickname = userData.userNickname,
                        userExtra = userData.userExtra,
                        userPw = userData.userPw
                    )
                ) {
                    // 로그인 화면을 백스택에서 제거하여 뒤로가기로 돌아갈 수 없도록 함
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
                onLoginClick = { id, pw ->
                    // 입력한 ID와 비밀번호가 저장된 정보와 일치하는지 확인
                    if (userPreferences.validateLogin(id, pw)) {
                        // 로그인 성공: 로그인 상태를 저장하고 메인 화면으로 이동
                        userPreferences.setLoggedIn(true)

                        val userData = userPreferences.getUserData()
                        if (userData != null) {
                            navController.navigate(
                                NavigationRoute.MainContainer(  // Main → MainContainer
                                    userId = userData.userId,
                                    userNickname = userData.userNickname,
                                    userExtra = userData.userExtra,
                                    userPw = userData.userPw
                                )
                            ) {
                                popUpTo<NavigationRoute.Login> { inclusive = true }
                            }
                        }
                    } else {
                        // 로그인 실패: 사용자에게 에러 메시지 표시
                        android.util.Log.e("AppNavigation", "로그인 실패: ID 또는 비밀번호가 일치하지 않습니다")
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
                    // 회원가입 정보를 SharedPreferences에 저장
                    userPreferences.saveUser(
                        signUpData.userId,
                        signUpData.password,
                        signUpData.nickname,
                        signUpData.extra
                    )

                    // 회원가입 성공 후 로그인 화면
                    navController.navigate(NavigationRoute.Login) {
                        popUpTo<NavigationRoute.SignUp> { inclusive = true }
                    }
                }
            )
        }

        composable<NavigationRoute.MainContainer> { backStackEntry ->  // Main → MainContainer
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
