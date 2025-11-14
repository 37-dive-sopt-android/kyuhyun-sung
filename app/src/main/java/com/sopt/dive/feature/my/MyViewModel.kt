package com.sopt.dive.feature.my

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState

    fun setUserInfo(userId: String, userPw: String, userNickname: String, userEmail: String, userAge: Int) {
        _uiState.value = MyUiState(
            userId = userId,
            userPw = userPw,
            userNickname = userNickname,
            userEmail = userEmail,    // userExtra → userEmail
            userAge = userAge         // age 추가
        )
    }
}