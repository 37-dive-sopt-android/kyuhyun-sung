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

@Composable
fun FriendProfileComponent(name: String, statusMessage: String, hasMusic: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Column {
            Text(text = name, fontWeight = FontWeight.SemiBold)
            Text(text = statusMessage, color = Color.Gray)
            if (hasMusic) {
                Text(text = " 프로필 뮤직 설정됨", color = Color.Blue)
            }
        }
    }
}

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
fun MyProfileComponentTest()
{

}

@Preview (showBackground = true)
@Composable
fun FriendProfileComponentTest()
{

}

@Preview (showBackground = true)
@Composable
fun BirthdayItemComponentTest()
{

}