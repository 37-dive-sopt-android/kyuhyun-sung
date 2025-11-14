package com.sopt.dive.core.data.datasource

import com.sopt.dive.core.data.dto.RequestLoginDto
import com.sopt.dive.core.data.dto.RequestSignUpDto
import com.sopt.dive.core.data.dto.ResponseLoginDto
import com.sopt.dive.core.data.dto.ResponseSignUpDto
import com.sopt.dive.core.data.dto.ResponseUserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    // 회원가입
    @POST("/api/v1/users")
    fun signUp(
        @Body request: RequestSignUpDto
    ): Call<ResponseSignUpDto>

    // 로그인
    @POST("/api/v1/auth/login")
    fun login(
        @Body request: RequestLoginDto
    ): Call<ResponseLoginDto>

    // 사용자 정보 조회
    @GET("/api/v1/users/{id}")
    fun getUserById(
        @Path("id") userId: Int
    ): Call<ResponseUserDto>
}