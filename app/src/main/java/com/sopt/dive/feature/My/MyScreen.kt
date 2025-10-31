package com.sopt.dive.feature.My


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    modifier: Modifier = Modifier,
    userId: String,
    userNickname: String,
    userExtra: String,
    userPw: String
) {
    val context = LocalContext.current

    // Coil 라이브러리를 사용하여 GIF 이미지를 로드하기 위한 ImageLoader 설정
    // GifDecoder를 추가하여 GIF 포맷 지원
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
            }
            .build()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // 환영 메시지
        Text(
            text = "안녕하세요 ${userNickname}님",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 40.dp),
            textAlign = TextAlign.Left,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )

        // 사용자 ID 표시
        Column {
            Text(text = "ID", fontSize = 20.sp)

            Text(
                modifier = Modifier.background(Color.Yellow),
                text = userId,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 사용자 비밀번호 표시
        Column {
            Text(text = "PW", fontSize = 20.sp)

            Text(
                modifier = Modifier.background(Color.Yellow),
                text = userPw,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 사용자 닉네임 표시
        Column {
            Text(text = "NICKNAME", fontSize = 20.sp)

            Text(
                modifier = Modifier.background(Color.Yellow),
                text = userNickname,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 추가 정보 (가위바위보 결과) 표시
        Column {
            Text(text = "승자는 ?", fontSize = 20.sp)

            Text(
                modifier = Modifier.background(Color.Yellow),
                text = userExtra,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // GIF 이미지 표시
        // AsyncImage를 사용하여 네트워크에서 GIF를 로드
        AsyncImage(
            model = "https://github.com/dmp100/dmp100/raw/main/gifs/gif1.gif",
            contentDescription = "GIF",
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
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
        userPw = "4444"
    )
}