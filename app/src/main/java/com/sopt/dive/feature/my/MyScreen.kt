package com.sopt.dive.feature.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder

@Composable
fun MyScreen(
    userId: String,
    userNickname: String,
    userEmail: String,    // userExtra → userEmail
    userAge: Int,         // age 추가
    userPw: String,
    onNavigateToCard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel에 유저 정보 전달
    LaunchedEffect(Unit) {
        viewModel.setUserInfo(userId, userPw, userNickname, userEmail, userAge)  // ✅ 파라미터 수정
    }

    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(GifDecoder.Factory()) }
            .build()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "안녕하세요 ${uiState.userNickname}님",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 40.dp),
            textAlign = TextAlign.Left,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )

        InfoBlock(label = "ID", value = uiState.userId)
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "PW", value = uiState.userPw)
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "NICKNAME", value = uiState.userNickname)
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "EMAIL", value = uiState.userEmail)  // 이메일 표시
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "AGE", value = uiState.userAge.toString())  // 나이 표시

        Button(
            onClick = { onNavigateToCard() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow,
                contentColor = Color.Black
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("GO to Card")
        }

        AsyncImage(
            model = "https://github.com/dmp100/dmp100/raw/main/gifs/gif1.gif",
            contentDescription = "GIF",
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun InfoBlock(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 20.sp)
        Text(
            modifier = Modifier.background(Color.Yellow),
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Preview(showBackground = true, name = "Main Screen")
@Composable
private fun MainScreenPreview() {
    MyScreen(
        userId = "1234",
        userNickname = "555",
        userEmail = "test@example.com",  // userExtra → userEmail
        userAge = 25,                    // age 추가
        userPw = "4444",
        onNavigateToCard = {}
    )
}