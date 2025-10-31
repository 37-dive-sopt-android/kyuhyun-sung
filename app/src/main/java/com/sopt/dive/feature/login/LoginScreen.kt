package com.sopt.dive.feature.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sopt.dive.core.ui.noRippleClickable

// 로그인 화면 컴포저블
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit
) {
    val context = LocalContext.current

    // 사용자가 입력한 ID와 PW를 저장하는 상태 변수
    var idText by remember { mutableStateOf("") }
    var pwText by remember { mutableStateOf("") }

    // 키보드에서 "다음" 버튼을 눌렀을 때 비밀번호 입력으로 포커스를 이동시키기 위한 객체
    val focusRequester = remember { FocusRequester() }

    // 키보드가 올라와도 화면을 스크롤할 수 있도록 하는 상태
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()  // 키보드가 올라올 때 자동으로 패딩 추가
            .padding(horizontal = 24.dp)
    ) {
        // 상단 타이틀
        Text(
            text = "Welcome To Sopt",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        // ID 입력 라벨
        Text(text = "ID", fontSize = 20.sp)

        // 무지개 텍스트 스타일
        val brush = remember {
            Brush.linearGradient(
                colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
            )
        }

        // ID 입력 필드
        OutlinedTextField(
            value = idText,
            onValueChange = { idText = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(brush = brush),
            label = { Text("아이디를 입력하세요") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequester.requestFocus() // "다음" 버튼으로 비밀번호 입력으로 이동
                }
            ),
            // singleLine = true는 두 가지 효과: 1) 한 줄 제한, 2) 엔터 키를 IME 액션으로 변경
            // maxLines = 1과 달리 키보드 동작이 변경되므로 로그인 필드에 적합
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PW 입력 라벨
        Text(text = "PW", fontSize = 20.sp)

        // PW 입력 필드
        OutlinedTextField(
            value = pwText,
            onValueChange = { pwText = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester), // ID 입력에서 "다음"을 누르면 포커스가 여기로 이동
            textStyle = TextStyle(brush = brush),
            label = { Text("비밀번호를 입력해주세요.") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),  // 비밀번호 마스킹 처리
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    onLoginClick(idText, pwText) // "완료" 버튼으로 로그인 실행
                }
            )
        )

        // 중간 공간을 채워서 하단 요소들을 아래로 밀어냄
        Spacer(modifier = Modifier.weight(1f))

        // 로그인 버튼
        Button(
            onClick = { onLoginClick(idText, pwText) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("로그인", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 회원가입 버튼
        // noRippleClickable 확장 함수를 사용하여 리플 효과 제거
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .noRippleClickable(onSignUpClick)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "회원가입",
                color = Color.Black,
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}