package com.sopt.dive

import android.content.Intent
import android.graphics.Insets.add
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import com.sopt.dive.ui.theme.DiveTheme
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LoginActivity에서 받은 데이터
        val userId = intent.getStringExtra(IntentKeys.USER_ID) ?: ""
        val userNickname = intent.getStringExtra(IntentKeys.USER_NICKNAME) ?: ""
        val userExtra = intent.getStringExtra(IntentKeys.USER_EXTRA) ?: ""
        val userPw = intent.getStringExtra(IntentKeys.USER_PW) ?: ""


        enableEdgeToEdge()
        setContent {
            DiveTheme{
                MainScreen(
                    userId = userId,
                    userNickname = userNickname,
                    userExtra = userExtra,
                    userPw = userPw
                )
                //LoginScreen()
                //SignUpScreen()
            }
        }
    }
}

// 3. 메인페이지
@Composable
fun MainScreen(
    userId: String,
    userNickname: String,
    userExtra: String,
    userPw: String
) {
    val context = LocalContext.current

    // ImageLoader를 Composable 내부에서 생성
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
            }
            .build()
    }
    Column(
        modifier = Modifier
            .fillMaxSize() // 화면 전체를 채움
            .padding(horizontal = 24.dp) // 좌우 여백 24dp 적용
    ) {
        // 자기소개 페이지
        Text(
            text = "안녕하세요 ${userNickname}님",
            modifier = Modifier
                .fillMaxWidth() // 가로 전체를 채움
                .padding(top = 50.dp, bottom = 40.dp), // 상하 여백 적용
            textAlign = TextAlign.Left,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )

        Column(modifier = Modifier)
        {
            Text(text = "ID", fontSize = 20.sp)

            Text(
                modifier = Modifier
                    .background(Color.Yellow),  // 배경색
                text = userId,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp

            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier)
        {
            Text(text = "PW", fontSize = 20.sp)

            Text(
                modifier = Modifier
                    .background(Color.Yellow),  // 배경색
                text = userPw,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp

            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier)
        {
            Text(text = "NICKNAME", fontSize = 20.sp)

            Text(
                modifier = Modifier
                    .background(Color.Yellow),  // 배경색
                text = userNickname,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp

            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier)
        {
            Text(text = "승자는 ?", fontSize = 20.sp)

            Text(
                modifier = Modifier
                    .background(Color.Yellow),  // 배경색
                text = userExtra,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        // GIF 표시
        AsyncImage(
            model = "https://github.com/dmp100/dmp100/raw/main/gifs/gif1.gif",
            contentDescription = "GIF",
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,  // 잘라서 채우기
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, name = "Main Screen")
@Composable
fun MainScreenPreview() {
    //MainScreen()
}
