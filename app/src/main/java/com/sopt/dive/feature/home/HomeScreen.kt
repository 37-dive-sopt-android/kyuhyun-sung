package com.sopt.dive.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


/**
 * Home 탭 화면
 */

// com.sopt.dive.feature.home.HomeScreen.kt

@Composable
fun HomeScreen(
    userId: String,
    userNickname: String,
    modifier: Modifier = Modifier
) {
    // 1. 표시할 데이터 목록 생성 (단순 문자열이나 간단한 data class)
    val dummyList = remember {
        listOf(
            "Welcome, $userNickname!",
            "Your ID: $userId",
            "SOPT 37기 안드로이드 화이팅",
            "123213123123123",
            "898989898989899",
            "afafafafafafafa",
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // 아이템 간 간격
    ) {
        // items()를 사용하여 리스트 항목을 반복
        items(dummyList) { text ->
            // 각 항목에 대한 간단한 컴포넌트
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Text(text = text, fontWeight = if (text.contains("Welcome")) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}