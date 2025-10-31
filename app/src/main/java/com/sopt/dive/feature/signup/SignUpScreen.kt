package com.sopt.dive.feature.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sopt.dive.feature.signup.components.SignUpTextFieldComponent

/**
 * 회원가입 화면 컴포저블
 *
 * @param modifier 외부에서 전달받는 Modifier (재사용성을 위해)
 * @param onSignUpClick 회원가입 버튼 클릭 시 실행될 콜백 함수
 */
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: (String, String, String, String) -> Unit
) {
    val context = LocalContext.current

    var idText by remember { mutableStateOf("") }
    var pwText by remember { mutableStateOf("") }
    var nicknameText by remember { mutableStateOf("") }
    var numberText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "SIGN UP",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        SignUpTextFieldComponent(
            label = "ID",
            value = idText,
            onValueChange = { idText = it },
            placeholder = "아이디를 입력해 주세요."
        )

        Spacer(modifier = Modifier.height(24.dp))

        SignUpTextFieldComponent(
            label = "PW",
            value = pwText,
            onValueChange = { pwText = it },
            placeholder = "비밀번호를 입력해주세요.",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        SignUpTextFieldComponent(
            label = "NICKNAME",
            value = nicknameText,
            onValueChange = { nicknameText = it },
            placeholder = "닉네임을 입력해주세요."
        )

        Spacer(modifier = Modifier.height(24.dp))

        SignUpTextFieldComponent(
            label = "가위바위보",
            value = numberText,
            onValueChange = { numberText = it },
            placeholder = "1은 가위, 2는 바위, 3은 보"
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val errorMessage = when {
                    idText.length !in 6..10 -> "ID는 6~10글자여야 합니다"
                    pwText.length !in 8..12 -> "PW는 8~12글자여야 합니다"
                    nicknameText.isBlank() -> "닉네임을 입력해주세요"
                    numberText.isBlank() -> "추가 정보를 입력해주세요"
                    else -> null
                }

                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    onSignUpClick(idText, pwText, nicknameText, numberText)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "회원가입",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "SignUp Screen")
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(onSignUpClick = { _, _, _, _ -> })
}