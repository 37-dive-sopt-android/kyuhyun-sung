package com.sopt.dive.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SignUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUpScreen(
                onSignUpClick = { id, pw, nickname, extra ->
                    handleSignUp(id, pw, nickname, extra)
                }
            )
        }
    }

    private fun handleSignUp(id: String, pw: String, nickname: String, extra: String) {
        // 유효성 검사
        when {
            id.length !in 6..10 -> {
                Toast.makeText(this, "ID는 6~10글자여야 합니다", Toast.LENGTH_SHORT).show()
            }
            pw.length !in 8..12 -> {
                Toast.makeText(this, "PW는 8~12글자여야 합니다", Toast.LENGTH_SHORT).show()
            }
            nickname.isBlank() -> {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            extra.isBlank() -> {
                Toast.makeText(this, "추가 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // 회원가입 성공 - 데이터 반환
                val resultIntent = Intent().apply {
                    putExtra("userId", id)
                    putExtra("userPw", pw)
                    putExtra("userNickname", nickname)
                    putExtra("userExtra", extra)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}

// 2. 회원가입 페이지
@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, String) -> Unit
) {
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
                visualTransformation = PasswordVisualTransformation(),
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

        // 회원가입 버튼
        Button(
            onClick = { /* 회원가입 로직 구현 */ onSignUpClick(idText, pwText, nicknameText, numberText) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("회원가입", color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "SignUp Screen")
@Composable
fun SignUpScreenPreview() {
    //SignUpScreen()
}