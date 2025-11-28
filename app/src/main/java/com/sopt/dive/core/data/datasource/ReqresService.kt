package com.sopt.dive.core.data.datasource

import com.sopt.dive.core.data.dto.ResponseReqresUsersDto
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.GET

/**
 * ReqresService - 코루틴 기반 API 서비스
 * 
 * suspend 함수로 구현하여 코루틴 내에서 간단하게 호출 가능
 */
interface ReqresService {
    
    /**
     * 사용자 리스트 조회 API
     * 
     * suspend 키워드:
     * - 네트워크 호출이 완료될 때까지 코루틴을 일시 중단
     * - 메인 스레드를 블로킹하지 않음
     * - try-catch로 간단한 에러 처리
     * 
     * 사용 예시:
     * viewModelScope.launch {
     *     try {
     *         val response = reqresService.getUsers()
     *         // 성공 처리
     *     } catch (e: Exception) {
     *         // 에러 처리
     *     }
     * }
     */
    @GET("users")
    suspend fun getUsers(
        @Header("x-api-key") apiKey: String = "reqres-free-v1",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): ResponseReqresUsersDto
}
