package com.sopt.dive.feature.my

data class MyUiState(
    val userId: String = "",
    val userPw: String = "",
    val userNickname: String = "",
    val userEmail: String = "",    // userExtra → userEmail
    val userAge: Int = 0           // age 추가
)