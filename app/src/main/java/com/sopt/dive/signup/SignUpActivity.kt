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
import com.sopt.dive.IntentKeys

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

    // 회원가입 유효성 검사 및 결과 반환
    // Toast 메시지 로직을 통합하여 코드 중복 제거
    private fun handleSignUp(id: String, pw: String, nickname: String, extra: String) {
        // 유효성 검사를 통과하지 못하면 에러 메시지 반환, 모두 통과하면 null 반환
        val errorMessage = when {
            id.length !in 6..10 -> "ID는 6~10글자여야 합니다"
            pw.length !in 8..12 -> "PW는 8~12글자여야 합니다"
            nickname.isBlank() -> "닉네임을 입력해주세요"
            extra.isBlank() -> "추가 정보를 입력해주세요"
            else -> null
        }

        if (errorMessage != null) {
            // 유효성 검사 실패 시 Toast 표시
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            // 모든 유효성 검사 통과 시 데이터를 LoginActivity로 반환
            val resultIntent = Intent().apply {
                putExtra(IntentKeys.USER_ID, id)
                putExtra(IntentKeys.USER_PW, pw)
                putExtra(IntentKeys.USER_NICKNAME, nickname)
                putExtra(IntentKeys.USER_EXTRA, extra)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}

// 회원가입 화면 컴포저블
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: (String, String, String, String) -> Unit
) {
    // 각 입력 필드의 상태를 별도로 관리
    var idText by remember { mutableStateOf("") }
    var pwText by remember { mutableStateOf("") }
    var nicknameText by remember { mutableStateOf("") }
    var numberText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // 상단 타이틀
        Text(
            text = "SIGN UP",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        // ID 입력 필드
        // BasicTextField를 사용하여 커스텀 디자인 적용
        Column {
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
                            // fillMaxWidth를 사용하므로 horizontal padding은 불필요
                            .padding(vertical = 16.dp)
                    ) {
                        // placeholder 텍스트 표시
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

        // PW 입력 필드
        Column {
            Text(text = "PW", fontSize = 20.sp)

            BasicTextField(
                value = pwText,
                onValueChange = { pwText = it },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),  // 비밀번호 마스킹
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(vertical = 16.dp)
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

        // NICKNAME 입력 필드
        Column {
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
                            .padding(vertical = 16.dp)
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

        // 추가 정보 입력 필드 (가위바위보)
        Column {
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
                            .padding(vertical = 16.dp)
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

        // 중간 공간을 채워서 버튼을 화면 하단으로 배치
        Spacer(modifier = Modifier.weight(1f))

        // 회원가입 버튼
        Button(
            onClick = { onSignUpClick(idText, pwText, nicknameText, numberText) },
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

// Preview 함수는 UI 확인용으로만 사용되므로 private으로 선언
@Preview(showBackground = true, name = "SignUp Screen")
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(onSignUpClick = { _, _, _, _ -> })
}