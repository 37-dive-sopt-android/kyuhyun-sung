package com.sopt.dive.core.data.datasource

import com.sopt.dive.core.data.dto.ResponseReqresUsersDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReqresService {
    @GET("users")
    suspend fun getUsers(
        @Header("x-api-key") apiKey: String = "reqres-free-v1",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): ResponseReqresUsersDto
}
