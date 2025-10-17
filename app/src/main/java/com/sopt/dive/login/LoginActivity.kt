package com.sopt.dive.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sopt.dive.MainActivity
import com.sopt.dive.signup.SignUpActivity
import kotlin.jvm.java

class LoginActivity : ComponentActivity() {

    // 회원가입 결과를 받을 launcher
    private val signUpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 회원가입에서 받은 데이터
            savedId = result.data?.getStringExtra("userId") ?: ""
            savedPw = result.data?.getStringExtra("userPw") ?: ""
            savedNickname = result.data?.getStringExtra("userNickname") ?: ""
            savedExtra = result.data?.getStringExtra("userExtra") ?: ""

            Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
        }
    }

    private var savedId = ""
    private var savedPw = ""
    private var savedNickname = ""
    private var savedExtra = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                onLoginClick = { id, pw -> handleLogin(id, pw) },
                onSignUpClick = { goToSignUp() }
            )
        }
    }

    private fun handleLogin(id: String, pw: String) {
        if (id == savedId && pw == savedPw) {
            // 로그인 성공
            Toast.makeText(this, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userId", savedId)
                putExtra("userNickname", savedNickname)
                putExtra("userExtra", savedExtra)
                putExtra("userPw", savedPw)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "ID 또는 PW가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        signUpLauncher.launch(intent)
    }
}

// 1. 로그인 페이지
@Composable
fun LoginScreen(
    // 클릭시 데이터 값 추가.
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit
) {
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
            textStyle = TextStyle(brush = brush), // 무지개맛 텍스트 스타일
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
            textStyle = TextStyle(brush = brush), // 무지개맛 텍스트 스타일
            label = { Text("비밀번호를 입력해주세요.") },
            visualTransformation = PasswordVisualTransformation()  // 이 줄 追加

        )

        // 중간 공간을 모두 채워서 하단 버튼들을 화면 아래로 밀어냄
        // weight(1f)는 남은 공간을 모두 차지하라는 의미
        Spacer(modifier = Modifier.weight(1f))

        // 로그인 버튼 (검은 배경, 둥근 모서리)
        Button(
            onClick = { /* 로그인 로직 구현 */ onLoginClick(idText, pwText) },
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
            onClick = onSignUpClick,
//            onClick = {
//                //  SignUpActivity로 이동하면서 이전 액티비티 스택을 모두 제거 -> Activity에서 이미 goToSignUp() 함수로 처리
//                val intent = Intent(context, SignUpActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                }
//                context.startActivity(intent)
//            },
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

// 일일이 이렇게 세개 나열하는게 나을듯.
@Preview(showBackground = true, name = "Login Screen")
@Composable
fun LoginScreenPreview() {
//    LoginScreen(
//        onLoginClick = TODO(),
//        onSignUpClick = TODO()
//    )
}