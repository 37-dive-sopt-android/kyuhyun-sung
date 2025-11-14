package com.sopt.dive.feature.my

data class MyUiState(
    val userId: String = "",
    val userPw: String = "",
    val userNickname: String = "",
    val userEmail: String = "",    // userExtra → userEmail
    val userAge: Int = 0,           // age 추가
    val isLoading: Boolean = false,        // 로딩 상태 추가 - 사용자 정보 조회 API 추가
    val errorMessage: String? = null       // 에러 메시지 추가 - 사용자 정보 조회 API 추가
)