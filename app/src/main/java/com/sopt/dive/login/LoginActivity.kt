package com.sopt.dive.login

import android.annotation.SuppressLint
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
import com.sopt.dive.IntentKeys
import com.sopt.dive.MainActivity
import com.sopt.dive.noRippleClickable
import com.sopt.dive.signup.SignUpActivity

class LoginActivity : ComponentActivity() {

    // 회원가입 결과를 받을 launcher
    // registerForActivityResult는 회원가입 화면을 열고 결과를 받아오는 메커니즘
    private val signUpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 회원가입 성공 시 전달받은 데이터를 저장
            // IntentKeys 객체를 사용하여 키 값의 일관성 유지
            savedId = result.data?.getStringExtra(IntentKeys.USER_ID) ?: ""
            savedPw = result.data?.getStringExtra(IntentKeys.USER_PW) ?: ""
            savedNickname = result.data?.getStringExtra(IntentKeys.USER_NICKNAME) ?: ""
            savedExtra = result.data?.getStringExtra(IntentKeys.USER_EXTRA) ?: ""

            Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
        }
    }

    // 회원가입으로 받아온 사용자 정보를 임시 저장
    // 실제 앱에서는 데이터베이스나 서버에 저장해야 하지만, 과제에서는 메모리에 저장
    private var savedId = ""
    private var savedPw = ""
    private var savedNickname = ""
    private var savedExtra = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 앱 시작 시 자동 로그인 상태 확인
        checkAutoLogin()

        setContent {
            LoginScreen(
                onLoginClick = { id, pw -> handleLogin(id, pw) },
                onSignUpClick = { goToSignUp() }
            )
        }
    }

    // 자동 로그인 확인 함수
    // SharedPreferences에 로그인 정보가 저장되어 있으면 자동으로 MainActivity로 이동
    private fun checkAutoLogin() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

        if (isLoggedIn) {
            // 저장된 사용자 정보 불러오기
            val userId = prefs.getString(KEY_USER_ID, "") ?: ""
            val userPw = prefs.getString(KEY_USER_PW, "") ?: ""
            val userNickname = prefs.getString(KEY_USER_NICKNAME, "") ?: ""
            val userExtra = prefs.getString(KEY_USER_EXTRA, "") ?: ""

            // MainActivity로 자동 이동
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(IntentKeys.USER_ID, userId)
                putExtra(IntentKeys.USER_NICKNAME, userNickname)
                putExtra(IntentKeys.USER_EXTRA, userExtra)
                putExtra(IntentKeys.USER_PW, userPw)
            }
            startActivity(intent)
            finish() // 로그인 화면 종료 (뒤로가기 시 로그인 화면이 다시 나타나지 않도록)
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun handleLogin(id: String, pw: String) {
        if (id == savedId && pw == savedPw) {
            // 로그인 성공 - SharedPreferences에 정보 저장하여 자동 로그인 구현
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {  // 코틀린 스코프 함수로 코드 간결화
                putBoolean(KEY_IS_LOGGED_IN, true)
                putString(KEY_USER_ID, savedId)
                putString(KEY_USER_PW, savedPw)
                putString(KEY_USER_NICKNAME, savedNickname)
                putString(KEY_USER_EXTRA, savedExtra)
                apply()  // SharedPreferences.Editor의 저장 메서드 (실제 저장 수행)
            }

            Toast.makeText(this, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()

            // MainActivity로 이동하면서 사용자 정보 전달
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(IntentKeys.USER_ID, savedId)
                putExtra(IntentKeys.USER_NICKNAME, savedNickname)
                putExtra(IntentKeys.USER_EXTRA, savedExtra)
                putExtra(IntentKeys.USER_PW, savedPw)
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

    companion object {
        // SharedPreferences에서 사용할 상수들
        // 문자열을 상수로 관리하여 오타 방지 및 유지보수 용이
        private const val PREFS_NAME = "sopt_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_PW = "user_pw"
        private const val KEY_USER_NICKNAME = "user_nickname"
        private const val KEY_USER_EXTRA = "user_extra"
    }
}


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