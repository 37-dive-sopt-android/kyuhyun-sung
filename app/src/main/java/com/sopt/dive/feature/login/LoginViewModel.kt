package com.sopt.dive.feature.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.UserPreferences
import com.sopt.dive.core.data.datasource.ServicePool
import com.sopt.dive.core.data.dto.RequestLoginDto
import com.sopt.dive.core.data.dto.ResponseLoginDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val userPreferences = UserPreferences.getInstance(context)
    private val authService = ServicePool.authService  // API Service 추가

    fun updateUserId(id: String) {
        _uiState.value = _uiState.value.copy(userId = id)
    }

    fun updatePassword(pw: String) {
        _uiState.value = _uiState.value.copy(password = pw)
    }

    fun validateLogin(onSuccess: (String, String) -> Unit, onFailure: (String) -> Unit) {
        val id = _uiState.value.userId
        val pw = _uiState.value.password

        viewModelScope.launch {
            if (userPreferences.validateLogin(id, pw)) {
                _uiState.value = _uiState.value.copy(
                    loginSuccess = true,
                    errorMessage = null
                )
                onSuccess(id, pw)
            } else {
                _uiState.value = _uiState.value.copy(
                    loginSuccess = false,
                    errorMessage = "ID 또는 비밀번호가 일치하지 않습니다."
                )
                onFailure("ID 또는 비밀번호가 일치하지 않습니다.")
            }
        }
    }

    // API 로그인 함수
    fun loginWithApi(onSuccess: (String, String) -> Unit, onFailure: (String) -> Unit) {
        val id = _uiState.value.userId
        val pw = _uiState.value.password

        // 입력값 검증
        if (id.isBlank() || pw.isBlank()) {
            onFailure("ID와 비밀번호를 입력해주세요.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        val request = RequestLoginDto(username = id, password = pw)

        authService.login(request).enqueue(object : Callback<ResponseLoginDto> {
            override fun onResponse(call: Call<ResponseLoginDto>, response: Response<ResponseLoginDto>) {
                _uiState.value = _uiState.value.copy(isLoading = false)

                if (response.isSuccessful) {
                    val loginData = response.body()
                    if (loginData?.success == true) {  // success 체크 추가
                        Log.d("Login", "로그인 성공: ${loginData.data.message}")  //.data 추가

                        _uiState.value = _uiState.value.copy(
                            loginSuccess = true,
                            errorMessage = null
                        )
                        onSuccess(id, pw)
                    } else {
                        onFailure(loginData?.message ?: "로그인 실패")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "아이디 또는 비밀번호가 틀렸습니다"
                        403 -> "비활성화된 사용자입니다"
                        404 -> "존재하지 않는 사용자입니다"
                        else -> "로그인 실패: ${response.message()}"
                    }

                    _uiState.value = _uiState.value.copy(
                        loginSuccess = false,
                        errorMessage = errorMsg
                    )
                    onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<ResponseLoginDto>, t: Throwable) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                val errorMsg = "네트워크 오류: ${t.message}"
                Log.e("Login", errorMsg)

                _uiState.value = _uiState.value.copy(
                    loginSuccess = false,
                    errorMessage = errorMsg
                )
                onFailure(errorMsg)
            }
        })
    }
}