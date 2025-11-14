package com.sopt.dive.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLoginDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: ResponseLoginDataDto
)

@Serializable
data class ResponseLoginDataDto(
    @SerialName("userId")
    val userId: Int,
    @SerialName("message")
    val message: String
)