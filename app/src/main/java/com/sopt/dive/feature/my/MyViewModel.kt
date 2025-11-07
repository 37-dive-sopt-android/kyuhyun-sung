package com.sopt.dive.feature.my

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState

    fun setUserInfo(id: String, pw: String, nickname: String, extra: String) {
        _uiState.value = MyUiState(
            userId = id,
            userPw = pw,
            userNickname = nickname,
            userExtra = extra
        )
    }
}
