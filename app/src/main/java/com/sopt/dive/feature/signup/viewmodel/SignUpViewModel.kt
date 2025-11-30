package com.sopt.dive.feature.signup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.sopt.dive.core.data.datasource.ServicePool
import com.sopt.dive.core.data.dto.RequestSignUpDto
import com.sopt.dive.core.data.dto.ResponseSignUpDto
import com.sopt.dive.feature.signup.SignUpData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
I  {"username":"11111111","password":"Skhsky10@","name":"qwer","email":"23213@naver.com","age":100}
I  --> END POST (96-byte body)
I  <-- 200 http://15.164.129.239/api/v1/users (18ms)
 */

class SignUpViewModel : ViewModel() {

    private val authService = ServicePool.authService

    private val _id = mutableStateOf("")
    val id: State<String> = _id

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _nickname = mutableStateOf("")
    val nickname: State<String> = _nickname

    //  이메일과 나이 필드 추가
    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _age = mutableStateOf("")
    val age: State<String> = _age

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun onIdChange(newId: String) {
        _id.value = newId
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onNicknameChange(newNickname: String) {
        _nickname.value = newNickname
    }

    //  이메일과 나이 변경 함수 추가
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onAgeChange(newAge: String) {
        _age.value = newAge
    }

    fun validateInput(): String? {
        return when {
            _id.value.length !in 6..10 -> "ID는 6~10글자여야 합니다"
            _password.value.length !in 8..12 -> "PW는 8~12글자여야 합니다"
            _nickname.value.isBlank() -> "이름을 입력해주세요"
            _email.value.isBlank() -> "이메일을 입력해주세요"
            !_email.value.contains("@") -> "올바른 이메일 형식이 아닙니다"
            _age.value.isBlank() -> "나이를 입력해주세요"
            _age.value.toIntOrNull() == null -> "나이는 숫자여야 합니다"
            (_age.value.toIntOrNull() ?: 0) !in 14..100 -> "나이는 14~100 사이여야 합니다"
            else -> null
        }
    }

    fun getSignUpData(): SignUpData {
        return SignUpData(
            userId = _id.value,
            password = _password.value,
            nickname = _nickname.value,
            email = _email.value,
            age = _age.value.toIntOrNull() ?: 0
        )
    }

    fun signUpWithApi(onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true

        val request = RequestSignUpDto(
            username = _id.value,
            password = _password.value,
            name = _nickname.value,
            email = _email.value,  // ✅ 실제 이메일 사용
            age = _age.value.toIntOrNull() ?: 25  // ✅ 실제 나이 사용
        )

        authService.signUp(request).enqueue(object : Callback<ResponseSignUpDto> {
            override fun onResponse(call: Call<ResponseSignUpDto>, response: Response<ResponseSignUpDto>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val userData = response.body()
                    if (userData?.success == true) {
                        Log.d("SignUp", "회원가입 성공: ${userData.data.name}")
                        onSuccess()
                    } else {
                        Log.e("SignUp", "서버 에러: ${userData?.message}")
                        onError(userData?.message ?: "알 수 없는 오류")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        409 -> "이미 존재하는 사용자명입니다"
                        else -> "회원가입 실패: ${response.message()}"
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ResponseSignUpDto>, t: Throwable) {
                _isLoading.value = false
                val errorMsg = "네트워크 에러: ${t.message}"
                Log.e("SignUp", errorMsg)
                onError(errorMsg)
            }
        })
    }
}