package com.sopt.dive.feature.login

/**
 * 로그인 화면의 UI 상태를 관리하는 데이터 클래스
 * 
 * Flow를 통해 실시간으로 업데이트되는 상태들을 포함합니다.
 */
data class LoginUiState(
    // === 입력 값 ===
    val userId: String = "",
    val password: String = "",
    
    // === 검증 상태 (Flow로 자동 계산) ===
    val isIdValid: Boolean = false,              // ID가 유효한가?
    val isPasswordValid: Boolean = false,        // 비밀번호가 유효한가?
    val isLoginButtonEnabled: Boolean = false,   // 로그인 버튼 활성화 여부 (ID + PW 모두 유효할 때)
    
    // === 에러 메시지 (Flow로 자동 계산) ===
    val idErrorMessage: String? = null,         // ID 입력 에러 메시지
    val passwordErrorMessage: String? = null,    // 비밀번호 입력 에러 메시지
    
    // === API 통신 상태 ===
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)
