package com.sopt.dive.core.data.datasource

import com.sopt.dive.core.data.dto.RequestLoginDto
import com.sopt.dive.core.data.dto.ResponseLoginDto
import com.sopt.dive.core.data.dto.ResponseUserDto
import retrofit2.HttpException
import java.io.IOException

/**
 * AuthRemoteDataSource - 실제 API를 호출하는 데이터 소스
 * 
 * RemoteDataSource의 역할:
 * - Retrofit Service를 래핑
 * - API 호출 로직 캡슐화
 * - 에러를 DTO로 반환 (Exception 던지지 않음)
 * 
 * 왜 Service를 직접 사용하지 않나?
 * 
 * ❌ Repository에서 Service 직접 사용:
 * class AuthRepositoryImpl(private val authService: AuthService) {
 *     override suspend fun login(...) {
 *         try {
 *             val response = authService.login(...)  // Service 직접 호출
 *             // ... 변환 로직
 *         } catch (...) {
 *             // ... 에러 처리
 *         }
 *     }
 * }
 * 
 * 문제점:
 * - Repository가 Retrofit 의존성을 알게 됨
 * - 테스트 시 Retrofit Mock이 필요함
 * - API 변경 시 Repository도 수정 필요
 * 
 * ✅ DataSource를 통한 간접 호출:
 * class AuthRepositoryImpl(private val dataSource: AuthRemoteDataSource) {
 *     override suspend fun login(...) {
 *         val response = dataSource.login(...)  // DataSource 호출
 *         // ... 변환 로직만
 *     }
 * }
 * 
 * 장점:
 * - Repository가 Retrofit을 모름
 * - 테스트 시 FakeDataSource로 교체 가능
 * - API 변경 시 DataSource만 수정
 * 
 * DataSource 계층의 이점:
 * 1. 추상화: Repository가 API 세부사항을 모름
 * 2. 테스트 용이: Fake로 교체 가능
 * 3. 유연성: 여러 API나 DB를 투명하게 전환
 */
class AuthRemoteDataSource(
    /**
     * Retrofit Service 인스턴스
     * - Retrofit이 자동 생성한 구현체
     * - suspend 함수로 API 호출
     */
    private val authService: AuthService
) {
    
    /**
     * 로그인 API 호출
     * 
     * 반환 타입:
     * - Result<ResponseLoginDto>
     * - Success: API 응답 DTO
     * - Failure: 에러 정보
     * 
     * 에러 처리:
     * - HttpException: HTTP 에러 (401, 404, 500 등)
     * - IOException: 네트워크 에러 (연결 끊김, 타임아웃)
     * - Exception: 기타 에러 (파싱 에러 등)
     * 
     * Result vs throw:
     * 
     * ❌ throw 방식:
     * suspend fun login(): ResponseLoginDto {
     *     return authService.login(...)  // 에러 시 Exception throw
     * }
     * 
     * 호출자가 try-catch 필요:
     * try {
     *     val response = dataSource.login()
     * } catch (e: HttpException) { ... }
     * 
     * ✅ Result 방식:
     * suspend fun login(): Result<ResponseLoginDto> {
     *     return try {
     *         Result.success(authService.login(...))
     *     } catch (e: Exception) {
     *         Result.failure(e)
     *     }
     * }
     * 
     * 호출자가 when으로 처리:
     * when (val result = dataSource.login()) {
     *     is Result.Success -> result.getOrNull()
     *     is Result.Failure -> result.exceptionOrNull()
     * }
     */
    suspend fun login(username: String, password: String): Result<ResponseLoginDto> {
        return try {
            // API 요청 객체 생성
            val request = RequestLoginDto(
                username = username,
                password = password
            )
            
            /**
             * API 호출:
             * - authService.login()은 suspend 함수
             * - 네트워크 통신 중 코루틴이 일시 중단
             * - 성공 시 ResponseLoginDto 반환
             * - 실패 시 Exception throw
             */
            val response = authService.login(request)
            
            // 성공 Result 반환
            Result.success(response)
            
        } catch (e: HttpException) {
            /**
             * HttpException:
             * - HTTP 에러 응답 (401, 403, 404, 500 등)
             * - 서버는 응답했지만 에러 코드 반환
             * 
             * 에러 코드별 의미:
             * - 401 Unauthorized: 인증 실패 (비밀번호 틀림)
             * - 403 Forbidden: 권한 없음 (계정 비활성화)
             * - 404 Not Found: 사용자 없음
             * - 500 Internal Server Error: 서버 오류
             */
            Result.failure(e)
            
        } catch (e: IOException) {
            /**
             * IOException:
             * - 네트워크 연결 문제
             * - 서버에 도달하지 못함
             * 
             * 원인:
             * - 인터넷 연결 끊김
             * - 서버 다운
             * - 타임아웃
             * - DNS 해석 실패
             */
            Result.failure(e)
            
        } catch (e: Exception) {
            /**
             * 기타 예외:
             * - JSON 파싱 에러
             * - Serialization 에러
             * - 예상치 못한 에러
             */
            Result.failure(e)
        }
    }
    
    /**
     * 사용자 정보 조회 API 호출
     * 
     * GET /api/users/{id}
     */
    suspend fun getUserById(userId: Int): Result<ResponseUserDto> {
        return try {
            val response = authService.getUserById(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * ========================================
 * DataSource 패턴 개념 정리
 * ========================================
 * 
 * 1. DataSource의 종류
 * 
 *    RemoteDataSource:
 *    - API 호출
 *    - 네트워크 통신
 *    - 최신 데이터
 * 
 *    LocalDataSource:
 *    - Room DB, SharedPreferences
 *    - 오프라인 사용 가능
 *    - 빠른 응답
 * 
 *    FakeDataSource:
 *    - 테스트 및 개발용
 *    - 네트워크 불필요
 *    - 즉시 응답
 * 
 * 2. Repository의 DataSource 사용
 * 
 *    단일 DataSource:
 *    class Repository(
 *        private val remoteDataSource: RemoteDataSource
 *    ) {
 *        suspend fun getData() = remoteDataSource.getData()
 *    }
 * 
 *    캐시 전략 (Cache + Remote):
 *    class Repository(
 *        private val remoteDataSource: RemoteDataSource,
 *        private val localDataSource: LocalDataSource
 *    ) {
 *        suspend fun getData() {
 *            // 1. 캐시 확인
 *            val cached = localDataSource.getData()
 *            if (cached.isValid()) return cached
 *            
 *            // 2. API 호출
 *            val remote = remoteDataSource.getData()
 *            
 *            // 3. 캐시 저장
 *            localDataSource.saveData(remote)
 *            
 *            return remote
 *        }
 *    }
 * 
 * 3. Result vs Exception
 * 
 *    Result 사용 (권장):
 *    - 에러를 값으로 표현
 *    - 컴파일러가 처리 강제
 *    - when으로 간결하게 처리
 * 
 *    Exception 사용:
 *    - 에러를 예외로 표현
 *    - try-catch 필요
 *    - 놓치기 쉬움
 * 
 * 4. 테스트 전략
 * 
 *    Unit Test:
 *    - FakeDataSource 사용
 *    - Repository 로직만 테스트
 *    - 빠르고 안정적
 * 
 *    Integration Test:
 *    - MockWebServer 사용
 *    - 실제 HTTP 통신 시뮬레이션
 *    - API 계약 검증
 * 
 *    E2E Test:
 *    - 실제 API 호출
 *    - 전체 흐름 검증
 *    - 느리고 불안정할 수 있음
 */
