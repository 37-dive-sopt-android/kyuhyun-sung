package com.sopt.dive.feature.my

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.datasource.ServicePool
import com.sopt.dive.core.data.dto.ResponseUserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState

    private val authService = ServicePool.authService  // API 서비스 추가

    /**
     * 기존 로컬 정보 설정 (호환성 유지)
     * 네비게이션에서 전달받은 사용자 정보를 즉시 표시
     */
    fun setUserInfo(userId: String, userPw: String, userNickname: String, userEmail: String, userAge: Int) {
        _uiState.value = MyUiState(
            userId = userId,
            userPw = userPw,
            userNickname = userNickname,
            userEmail = userEmail,
            userAge = userAge
        )
    }

    /**
     * 서버에서 사용자 정보 가져오기
     * 실시간으로 최신 정보를 조회하여 UI를 업데이트
     */
    fun fetchUserFromServer(userId: Int) {
        viewModelScope.launch {  // viewModelScope.launch 추가
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authService.getUserById(userId).enqueue(object : Callback<ResponseUserDto> {
                override fun onResponse(call: Call<ResponseUserDto>, response: Response<ResponseUserDto>) {
                    _uiState.value = _uiState.value.copy(isLoading = false)

                    if (response.isSuccessful) {
                        val userData = response.body()
                        if (userData?.success == true) {  // success 체크 추가
                            Log.d("MyScreen", "사용자 정보 조회 성공: ${userData.data.name}")

                            _uiState.value = _uiState.value.copy(
                                userId = userData.data.username,  // .data 추가
                                userNickname = userData.data.name,
                                userEmail = userData.data.email,
                                userAge = userData.data.age,
                                errorMessage = null
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = userData?.message ?: "사용자 정보를 불러올 수 없습니다"
                            )
                        }
                    } else {
                        val errorMsg = when (response.code()) {
                            404 -> "사용자를 찾을 수 없습니다"
                            else -> "서버 오류: ${response.message()}"
                        }
                        _uiState.value = _uiState.value.copy(errorMessage = errorMsg)
                    }
                }

                override fun onFailure(call: Call<ResponseUserDto>, t: Throwable) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "네트워크 오류: ${t.message}"
                    )
                    Log.e("MyScreen", "네트워크 오류: ${t.message}")
                }
            })
        }
    }
}