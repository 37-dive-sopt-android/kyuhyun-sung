package com.sopt.dive.feature.my

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    userEmail: String,
    userAge: Int,
    userPw: String,
    onNavigateToCard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel에 유저 정보 전달
    LaunchedEffect(Unit) {
        Log.d("MyScreen", "LaunchedEffect 시작 - userId: $userId")

        // 로컬 정보 먼저 설정 (빠른 표시)
        viewModel.setUserInfo(userId, userPw, userNickname, userEmail, userAge)
        Log.d("MyScreen", "로컬 정보 설정 완료")

        // 서버에서 최신 정보 가져오기 (userId를 Int로 변환)
        val userIdInt = userId.toIntOrNull()
        Log.d("MyScreen", "userId 변환 시도: $userId -> $userIdInt")

        if (userIdInt != null) {
            Log.d("MyScreen", "API 호출 시작: userId = $userIdInt")
            viewModel.fetchUserFromServer(userIdInt)
        } else {
            Log.e("MyScreen", "userId를 Int로 변환 실패: $userId")
        }
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

        // 로딩 상태 표시
        if (uiState.isLoading) {
            Text(
                text = "📡 사용자 정보 불러오는 중...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
        }

        // 에러 상태 표시
        if (uiState.errorMessage != null) {
            Text(
                text = "⚠️ ${uiState.errorMessage}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.Red.copy(alpha = 0.1f))
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        InfoBlock(label = "ID", value = uiState.userId)
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "PW", value = uiState.userPw)
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "NICKNAME", value = uiState.userNickname)
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "EMAIL", value = uiState.userEmail)
        Spacer(modifier = Modifier.height(24.dp))

        InfoBlock(label = "AGE", value = uiState.userAge.toString())

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ 버튼들을 가로로 배치
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,  // 균등 배치
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 새로고침 버튼
            Button(
                onClick = {
                    val userIdInt = userId.toIntOrNull()
                    if (userIdInt != null) {
                        viewModel.fetchUserFromServer(userIdInt)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("새로고침")
            }

            // 카드 이동 버튼
            Button(
                onClick = { onNavigateToCard() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    contentColor = Color.Black
                )
            ) {
                Text("GO to Card")
            }
        }
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
        userEmail = "test@example.com",
        userAge = 25,
        userPw = "4444",
        onNavigateToCard = {}
    )
}