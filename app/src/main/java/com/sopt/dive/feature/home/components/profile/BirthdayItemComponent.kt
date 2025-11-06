package com.sopt.dive.feature.home.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
fun BirthdayItemComponent(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF9C4))
            .padding(16.dp)
    ) {
        Text(text = "오늘은 $name 님의 생일입니다!", fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Preview (showBackground = true)
@Composable
fun BirthdayItemComponentTest()
{
    BirthdayItemComponent(name = "SOPT37기")
}