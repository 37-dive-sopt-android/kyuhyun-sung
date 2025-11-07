package com.sopt.dive.feature.signup

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class SignUpViewModel : ViewModel() {

    private val _id = mutableStateOf("")
    val id: State<String> = _id

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _nickname = mutableStateOf("")
    val nickname: State<String> = _nickname

    private val _extra = mutableStateOf("")
    val extra: State<String> = _extra

    fun onIdChange(newId: String) {
        _id.value = newId
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onNicknameChange(newNickname: String) {
        _nickname.value = newNickname
    }

    fun onExtraChange(newExtra: String) {
        _extra.value = newExtra
    }

    fun validateInput(): String? {
        return when {
            _id.value.length !in 6..10 -> "ID는 6~10글자여야 합니다"
            _password.value.length !in 8..12 -> "PW는 8~12글자여야 합니다"
            _nickname.value.isBlank() -> "닉네임을 입력해주세요"
            _extra.value.isBlank() -> "추가 정보를 입력해주세요"
            else -> null
        }
    }

    fun getSignUpData(): SignUpData {
        return SignUpData(
            userId = _id.value,
            password = _password.value,
            nickname = _nickname.value,
            extra = _extra.value
        )
    }
}
