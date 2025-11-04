package com.sopt.dive.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun MyProfileComponent(userId: String, userNickname: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp)
    ) {
        Column {
            Text(text = "내 프로필", fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = userNickname, color = Color.Black)
            Text(text = "ID: $userId", color = Color.Gray)
        }
    }
}

@Preview (showBackground = true)
@Composable
fun MyProfileComponentTest()
{
    MyProfileComponent(userId = "1234", userNickname = "text")
}