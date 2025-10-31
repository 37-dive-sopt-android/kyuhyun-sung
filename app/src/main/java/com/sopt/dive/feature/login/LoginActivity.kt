package com.sopt.dive.feature.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.sopt.dive.core.common.IntentKeys
import com.sopt.dive.feature.main.MainActivity
import com.sopt.dive.feature.signup.SignUpActivity

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
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
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
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
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


