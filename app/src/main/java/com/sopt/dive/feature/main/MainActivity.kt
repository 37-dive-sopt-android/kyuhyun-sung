package com.sopt.dive.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sopt.dive.core.common.IntentKeys
import com.sopt.dive.core.ui.theme.DiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LoginActivity에서 Intent로 전달받은 사용자 데이터
        val userId = intent.getStringExtra(IntentKeys.USER_ID) ?: ""
        val userNickname = intent.getStringExtra(IntentKeys.USER_NICKNAME) ?: ""
        val userExtra = intent.getStringExtra(IntentKeys.USER_EXTRA) ?: ""
        val userPw = intent.getStringExtra(IntentKeys.USER_PW) ?: ""

        enableEdgeToEdge()
        setContent {
            DiveTheme {
                MainScreen(
                    userId = userId,
                    userNickname = userNickname,
                    userExtra = userExtra,
                    userPw = userPw
                )
            }
        }
    }
}
