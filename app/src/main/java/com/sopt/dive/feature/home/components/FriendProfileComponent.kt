package com.sopt.dive.feature.home.components

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


@Preview (showBackground = true)
@Composable
fun FriendProfileComponentTest()
{
    FriendProfileComponent(name = "text", statusMessage = "text", hasMusic = true)
}
