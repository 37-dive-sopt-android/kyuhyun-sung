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

/**
 * My 탭 화면
 * 1주차에서 만든 메인페이지를 여기로 옮겼습니다
 */

// 메인 화면 컴포저블
// 로그인한 사용자의 정보를 표시
@Composable
fun MyScreen(
    userId: String,
    userNickname: String,
    userExtra: String,
    userPw: String,
    onNavigateToCard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel에 유저 정보 전달
    LaunchedEffect(Unit) {
        viewModel.setUserInfo(userId, userPw, userNickname, userExtra)
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

        InfoBlock(label = "승자는 ?", value = uiState.userExtra)

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

// Preview 함수는 UI 확인용으로만 사용되므로 private으로 선언
@Preview(showBackground = true, name = "Main Screen")
@Composable
private fun MainScreenPreview() {
    MyScreen(
        userId = "1234",
        userNickname = "555",
        userExtra = "421412",
        userPw = "4444",
        onNavigateToCard = {} // 더미는 이렇게
    )
}