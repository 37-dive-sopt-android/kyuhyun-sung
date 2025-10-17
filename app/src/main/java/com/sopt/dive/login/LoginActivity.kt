package com.sopt.dive.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.CompositionLocalProvider
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
import com.sopt.dive.MainActivity
import com.sopt.dive.signup.SignUpActivity

class LoginActivity : ComponentActivity() {

    // SharedPreferences 이름 정의
    companion object {
        private const val PREFS_NAME = "sopt_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_PW = "user_pw"
        private const val KEY_USER_NICKNAME = "user_nickname"
        private const val KEY_USER_EXTRA = "user_extra"
    }

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

        // 자동 로그인 체크
        checkAutoLogin()

        setContent {
            LoginScreen(
                onLoginClick = { id, pw -> handleLogin(id, pw) },
                onSignUpClick = { goToSignUp() }
            )
        }
    }

    // 자동 로그인 확인 함수
    private fun checkAutoLogin() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

        // 로그인 상태가 저장되어 있으면 자동으로 MainActivity로 이동
        if (isLoggedIn) {
            val userId = prefs.getString(KEY_USER_ID, "") ?: ""
            val userPw = prefs.getString(KEY_USER_PW, "") ?: ""
            val userNickname = prefs.getString(KEY_USER_NICKNAME, "") ?: ""
            val userExtra = prefs.getString(KEY_USER_EXTRA, "") ?: ""

            // 저장된 데이터로 MainActivity 이동
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userId", userId)
                putExtra("userNickname", userNickname)
                putExtra("userExtra", userExtra)
                putExtra("userPw", userPw)
            }
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }

    private fun handleLogin(id: String, pw: String) {
        if (id == savedId && pw == savedPw) {
            // 로그인 성공 - SharedPreferences에 저장
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, true)
                putString(KEY_USER_ID, savedId)
                putString(KEY_USER_PW, savedPw)
                putString(KEY_USER_NICKNAME, savedNickname)
                putString(KEY_USER_EXTRA, savedExtra)
                apply()
            }

            Toast.makeText(this, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userId", savedId)
                putExtra("userNickname", savedNickname)
                putExtra("userExtra", savedExtra)
                putExtra("userPw", savedPw)
            }
            startActivity(intent)
            finish() // 뒤로가기 방지
        } else {
            Toast.makeText(this, "ID 또는 PW가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        signUpLauncher.launch(intent)
    }
}

// 로그인 페이지
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit
) {
    val context = LocalContext.current

    // ID와 PW 상태 관리
    var idText by remember { mutableStateOf("") }
    var pwText by remember { mutableStateOf("") }

    // PW 입력창으로 포커스 이동을 위한 FocusRequester
    val focusRequester = remember { FocusRequester() }

    // 스크롤 상태 (키보드가 올라와도 스크롤 가능하도록)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // 키보드 올라올 때 스크롤 가능
            .imePadding() // 키보드가 올라올 때 하단 패딩 자동 추가
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
                imeAction = ImeAction.Next // 다음 버튼으로 표시
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequester.requestFocus() // PW 입력창으로 포커스 이동
                }
            ),
            singleLine = true // 한 줄로 제한 (2줄 방지)
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
                .focusRequester(focusRequester), // 포커스 받을 수 있도록 설정
            textStyle = TextStyle(brush = brush),
            label = { Text("비밀번호를 입력해주세요.") },
            singleLine = true, // 한 줄로 제한
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Go // 완료 버튼으로 표시
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    onLoginClick(idText, pwText) // 엔터 시 로그인 실행
                }
            )
        )

        // 중간 공간 채우기
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

        // 회원가입 버튼 (리플 효과 제거)
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // 리플 효과 제거
                    onClick = onSignUpClick
                )
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