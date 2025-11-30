package com.sopt.dive.feature.signup

data class SignUpData(
    val userId: String,
    val password: String,
    val nickname: String,
    val email: String,    // 이메일 추가
    val age: Int          // 나이 추가 (Int로 변경)
)