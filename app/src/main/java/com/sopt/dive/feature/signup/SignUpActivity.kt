package com.sopt.dive.feature.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sopt.dive.core.common.IntentKeys

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

