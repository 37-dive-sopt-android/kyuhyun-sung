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
        enableEdgeToEdge()
        setContent {
            DiveTheme{
                MainScreen()
                //SignUpScreen()
                //LoginScreen()
            }
        }
    }
}

// 1. 로그인 페이지
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    // ID와 PW를 각각 별도의 상태로 관리
    var idText by remember { mutableStateOf("") }
    var pwText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize() // 화면 전체를 채움
            .padding(horizontal = 24.dp) // 좌우 여백 24dp 적용
    ) {
        // 상단 타이틀
        Text(
            text = "Welcome To Sopt",
            modifier = Modifier
                .fillMaxWidth() // 가로 전체를 채움
                .padding(top = 50.dp, bottom = 40.dp), // 상하 여백 적용
            textAlign = TextAlign.Center, // 텍스트 중앙 정렬
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        // ID 입력 라벨
        Text(text = "ID", fontSize = 20.sp )

        // 무지개맛
        val brush = remember {
            Brush.linearGradient(
                colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
            )
        }
        // ID 입력 필드
        OutlinedTextField(
            value = idText,
            onValueChange = { idText = it }, // 입력값 변경 시 상태 업데이트
            modifier = Modifier
                .fillMaxWidth(), // 가로 전체를 채움
            //.background(Color.LightGray, shape = RoundedCornerShape(4.dp)), // 배경
            textStyle = androidx.compose.ui.text.TextStyle(brush = brush), // 무지개맛 텍스트 스타일
            label = { Text("아이디를 입력하세요") },
        )

//        // 기본 머티리얼디자인 진짜 개못생겼네
//        TextField(
//            value = idText,
//            onValueChange = { idText = it }, // 입력값 변경 시 상태 업데이트
//            modifier = Modifier.fillMaxWidth(), // 가로 전체를 채움
//            textStyle = androidx.compose.ui.text.TextStyle(brush = brush), // 무지개맛 텍스트 스타일
//            label = { Text("Material 진짜 개못생겼네") },
//        )

        // ID와 PW 입력 필드 사이 고정 간격 (24dp)
        Spacer(modifier = Modifier.height(24.dp))

        // PW 입력 라벨
        Text(text = "PW", fontSize = 20.sp )

        // PW 입력 필드
        OutlinedTextField(
            value = pwText,
            onValueChange = { pwText = it }, // 입력값 변경 시 상태 업데이트
            modifier = Modifier.fillMaxWidth(), // 가로 전체를 채움
            textStyle = androidx.compose.ui.text.TextStyle(brush = brush), // 무지개맛 텍스트 스타일
            label = { Text("비밀번호를 입력해주세요.") }
        )

        // 중간 공간을 모두 채워서 하단 버튼들을 화면 아래로 밀어냄
        // weight(1f)는 남은 공간을 모두 차지하라는 의미
        Spacer(modifier = Modifier.weight(1f))

        // 로그인 버튼 (검은 배경, 둥근 모서리)
        Button(
            onClick = { /* 로그인 로직 구현 */ },
            modifier = Modifier.fillMaxWidth(), // 가로 전체를 채움
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black), // 검은색 배경
            shape = RoundedCornerShape(8.dp) // 모서리를 8dp만큼 둥글게
        ) {
            Text("로그인", color = Color.White)

            // 회원가입 버튼과 로그인 버튼 사이 고정 간격 (16dp)
            Spacer(modifier = Modifier.height(16.dp))

        }
        // 회원가입 버튼 (투명 배경, 밑줄 텍스트)
        Button(
            onClick = {
                // MainActivity로 이동하면서 이전 액티비티 스택을 모두 제거
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally), // 버튼을 가로 중앙 정렬
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // 투명 배경
        ) {
            Text(
                "회원가입",
                color = Color.Black,
                textDecoration = TextDecoration.Underline // 텍스트에 밑줄 추가
            )
        }
        // 로그인 버튼과 화면 하단 사이 고정 간격 (24dp)
        Spacer(modifier = Modifier.height(24.dp))
    }
}


// 2. 회원가입 페이지
@Composable
fun SignUpScreen() {
    val context = LocalContext.current
    // ID와 PW를 각각 별도의 상태로 관리
    var idText by remember { mutableStateOf("") }
    var pwText by remember { mutableStateOf("") }
    var nicknameText by remember { mutableStateOf("") }
    var numberText by remember { mutableStateOf("") }

    Column( // Q :이거 컴포넌트 효율적으로 쓰는 방법없음 ? 참조로 바든 방법이라든가 이거 코드너무커진다
        modifier = Modifier
            .fillMaxSize() // 화면 전체를 채움
            .padding(horizontal = 24.dp) // 좌우 여백 24dp 적용
    ) {
        // 상단 타이틀
        Text(
            text = "SIGN UP",
            modifier = Modifier
                .fillMaxWidth() // 가로 전체를 채움
                .padding(top = 50.dp, bottom = 40.dp), // 상하 여백 적용
            textAlign = TextAlign.Center, // 텍스트 중앙 정렬
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        // ID 입력
        Column(modifier = Modifier) {
            Text(text = "ID", fontSize = 20.sp)

            BasicTextField(
                value = idText,
                onValueChange = { idText = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        if (idText.isEmpty()) {
                            Text(
                                text = "아이디를 입력해 주세요.",
                                fontSize = 18.sp,
                                color = Color.Gray,
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PW 입력
        Column(modifier = Modifier) {
            Text(text = "PW", fontSize = 20.sp)

            BasicTextField(
                value = pwText,
                onValueChange = { pwText = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        if (pwText.isEmpty()) {
                            Text(
                                text = "비밀번호를 입력해주세요.",
                                fontSize = 18.sp,
                                color = Color.Gray,
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // NICKNAME 입력
        Column(modifier = Modifier) {
            Text(text = "NICKNAME", fontSize = 20.sp)

            BasicTextField(
                value = nicknameText,
                onValueChange = { nicknameText = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        if (nicknameText.isEmpty()) {
                            Text(
                                text = "닉네임을 입력해주세요.",
                                fontSize = 18.sp,
                                color = Color.Gray,
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        Column(modifier = Modifier) {
            Text(text = "가위바위보", fontSize = 20.sp)

            BasicTextField(
                value = numberText,
                onValueChange = { numberText = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        if (numberText.isEmpty()) {
                            Text(
                                text = "1은 가위, 2는 바위, 3은 보 ",
                                fontSize = 18.sp,
                                color = Color.Gray,
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // 중간 공간을 모두 채워서 하단 버튼들을 화면 아래로 밀어냄
        Spacer(modifier = Modifier.weight(1f))

        // 로그인 버튼 (검은 배경, 둥근 모서리)
        Button(
            onClick = { /* 회원가입 로직 구현 */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("회원가입", color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}



// 3. 메인페이지
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
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
            text = "안녕하세요",
                //"안녕하세요 ${userInput}입니다.",
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
                text = "dmp100",
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
                text = "d",
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
                text = "d",
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
                text = "d",
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



// 처음 짯던 코드
//@Preview(showBackground = true)
//@Composable
//fun TestPreview()
//{
//    LoginScreen()
//    SignUpScreen()
//    MainScreen()
//}

// 일일이 이렇게 세개 나열하는게 나을듯.
@Preview(showBackground = true, name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}

@Preview(showBackground = true, name = "SignUp Screen")
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}

@Preview(showBackground = true, name = "Main Screen")
@Composable
fun MainScreenPreview() {
    MainScreen()
}


//// Column이나 Row로 배치 Preview 배치
//@Preview(showBackground = true)
//@Composable
//fun MultiScreenPreview() {
//    Column {
//        LoginScreen()
//        HorizontalDivider()
//        // 앱에 구분선을 구현
//        SignUpScreen()
//        HorizontalDivider()
//        MainScreen()
//    }
//}

// 코드길어지니까 유사XML되가는듯 개꼴보기싫네