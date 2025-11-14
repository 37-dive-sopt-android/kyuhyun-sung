package com.sopt.dive.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 서버가 보내는 에러 메시지: "이미 존재하는 사용자명입니다"
// 이런 상세 메시지를 받으려면 DTO 필요

@Serializable
data class ResponseSignUpDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: ResponseUserDataDto
)

@Serializable
data class ResponseUserDataDto(
    @SerialName("id")
    val id: Int,
    @SerialName("username")
    val username: String,
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("age")
    val age: Int,
    @SerialName("status")
    val status: String
)

