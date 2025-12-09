package com.sopt.dive.core.data.datasource

import com.sopt.dive.core.data.dto.RequestLoginDto
import com.sopt.dive.core.data.dto.RequestSignUpDto
import com.sopt.dive.core.data.dto.ResponseLoginDto
import com.sopt.dive.core.data.dto.ResponseSignUpDto
import com.sopt.dive.core.data.dto.ResponseUserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * AuthService - 코루틴 기반 API 서비스
 * 
 * Callback 방식 vs 코루틴 방식 비교:
 * 
 * ❌ Callback 방식 (구식):
 * fun login(@Body request: RequestLoginDto): Call<ResponseLoginDto>
 * 
 * ✅ 코루틴 방식 (최신):
 * suspend fun login(@Body request: RequestLoginDto): ResponseLoginDto
 * 
 * 차이점:
 * 1. suspend 키워드 추가
 * 2. Call<T> → T 직접 반환
 * 3. 에러 처리가 try-catch로 간단해짐
 */
interface AuthService {
    
    /**
     * 회원가입 API
     * 
     * suspend 키워드:
     * - 이 함수는 "일시 중단 가능한 함수"임을 의미
     * - 네트워크 통신 중에는 스레드를 블로킹하지 않고 다른 작업 가능
     * - 오직 코루틴 내부나 다른 suspend 함수에서만 호출 가능
     * 
     * 반환 타입:
     * - Call<ResponseSignUpDto> → ResponseSignUpDto로 단순화
     * - Retrofit이 자동으로 응답을 파싱하여 반환
     * - 에러 발생 시 예외를 throw함
     */
    @POST("/api/v1/users")
    suspend fun signUp(
        @Body request: RequestSignUpDto
    ): ResponseSignUpDto

    /**
     * 로그인 API
     * 
     * 사용 예시:
     * viewModelScope.launch {  // 코루틴 시작
     *     try {
     *         val response = authService.login(request)  // 네트워크 호출 (일시 중단)
     *         // 성공 처리
     *     } catch (e: Exception) {
     *         // 에러 처리
     *     }
     * }
     */
    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body request: RequestLoginDto
    ): ResponseLoginDto

    /**
     * 사용자 정보 조회 API
     */
    @GET("/api/v1/users/{id}")
    suspend fun getUserById(
        @Path("id") userId: Int
    ): ResponseUserDto
}

/**
 * ========================================
 * suspend 함수 개념 정리
 * ========================================
 * 
 * 1. suspend 키워드의 의미
 *    - "일시 중단 가능한 함수"
 *    - 코루틴 내에서만 호출 가능
 *    - 네트워크/DB 작업처럼 시간이 걸리는 작업에 사용
 * 
 * 2. 장점
 *    - 콜백 지옥 탈출
 *    - 순차적으로 읽기 쉬운 코드
 *    - 자동 취소 처리
 * 
 * 3. 일시 중단의 의미
 *    - 함수 실행 중 다른 작업을 할 수 있음
 *    - 메인 스레드를 블로킹하지 않음
 *    - 네트워크 응답이 오면 자동으로 재개됨
 * 
 * 4. 에러 처리
 *    - HTTP 에러 → HttpException 발생
 *    - 네트워크 에러 → IOException 발생
 *    - try-catch로 간단히 처리 가능
 */
