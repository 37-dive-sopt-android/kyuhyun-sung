package com.sopt.dive.feature.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val context: Context // ViewModelFactory를 따로 만들어서 DI 형태로 추후 리팩토링
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val userPreferences = UserPreferences.getInstance(context)

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
}
