package com.sopt.dive.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseErrorDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: ResponseErrorDataDto
)

@Serializable
data class ResponseErrorDataDto(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String
)