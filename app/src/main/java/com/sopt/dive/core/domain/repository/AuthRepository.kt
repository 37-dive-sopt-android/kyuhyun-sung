package com.sopt.dive.core.domain.repository

import com.sopt.dive.core.domain.entity.LoginResult
import com.sopt.dive.core.domain.entity.User

/**
 * AuthRepository Interface (Domain Layer)
 * 
 * Repository Pattern이란?
 * - 데이터 소스를 추상화하는 디자인 패턴
 * - 데이터가 어디서 오는지 숨기고 일관된 인터페이스 제공
 * 
 * 왜 Interface로 정의하나?
 * 
 * 의존성 역전 원칙 (Dependency Inversion Principle):
 * 
 * ❌ 잘못된 방법 (구체적인 클래스에 의존):
 * class LoginUseCase(
 *     private val repository: AuthRepositoryImpl  // 구현체에 직접 의존
 * )
 * 
 * 문제점:
 * - AuthRepositoryImpl을 변경하면 UseCase도 수정해야 함
 * - 테스트 시 실제 API를 호출해야 함 (느리고 불안정)
 * - FakeRepository로 교체가 어려움
 * 
 * ✅ 올바른 방법 (추상화에 의존):
 * class LoginUseCase(
 *     private val repository: AuthRepository  // 인터페이스에 의존
 * )
 * 
 * 장점:
 * - 구현체를 자유롭게 교체 가능 (Real → Fake)
 * - 테스트가 쉬움 (FakeRepository 사용)
 * - 여러 데이터 소스를 투명하게 사용 (API, Cache, LocalDB)
 * 
 * Repository의 역할:
 * 1. 데이터 소스 추상화 (API, DB, Cache 등)
 * 2. 데이터 접근 로직 캡슐화
 * 3. DTO → Entity 변환
 * 4. 에러 처리 및 변환
 * 
 * 예시:
 * 
 * 실제 구현 (Production):
 * class AuthRepositoryImpl : AuthRepository {
 *     override suspend fun login(username: String, password: String): LoginResult {
 *         return try {
 *             val response = api.login(...)  // 실제 API 호출
 *             LoginResult.Success(...)
 *         } catch (e: Exception) {
 *             LoginResult.Error.NetworkError()
 *         }
 *     }
 * }
 * 
 * 가짜 구현 (Test):
 * class FakeAuthRepository : AuthRepository {
 *     override suspend fun login(username: String, password: String): LoginResult {
 *         return if (username == "test" && password == "password") {
 *             LoginResult.Success(User(...))  // 즉시 성공 반환
 *         } else {
 *             LoginResult.Error.InvalidCredentials()
 *         }
 *     }
 * }
 * 
 * 캐시 + API 구현 (Advanced):
 * class CachedAuthRepository : AuthRepository {
 *     override suspend fun login(username: String, password: String): LoginResult {
 *         // 1. 캐시 확인
 *         val cached = cache.get(username)
 *         if (cached != null) return LoginResult.Success(cached)
 *         
 *         // 2. API 호출
 *         val result = api.login(...)
 *         
 *         // 3. 캐시 저장
 *         if (result is LoginResult.Success) {
 *             cache.save(result.user)
 *         }
 *         
 *         return result
 *     }
 * }
 */
interface AuthRepository {
    
    /**
     * 로그인
     * 
     * suspend 키워드:
     * - 코루틴 내에서 비동기 작업 가능
     * - 네트워크 호출, DB 조회 등 시간이 걸리는 작업 처리
     * 
     * 반환 타입 LoginResult:
     * - sealed class로 성공/실패를 명확히 구분
     * - Result<User>나 User?보다 명확하고 안전함
     * 
     * 파라미터:
     * - DTO가 아닌 원시 타입 사용 (String)
     * - Domain Layer는 Data Layer의 DTO를 모름
     * 
     * 예외 처리:
     * - 예외를 throw하지 않음
     * - 모든 에러를 LoginResult.Error로 변환하여 반환
     * - 호출자가 예외 처리를 신경쓰지 않아도 됨
     */
    suspend fun login(
        username: String,
        password: String
    ): LoginResult
    
    /**
     * 회원가입
     * 
     * User를 파라미터로 받음:
     * - 회원가입 정보가 여러 개일 수 있으므로 객체로 전달
     * - username, password, email 등을 개별 파라미터로 받으면 복잡함
     */
    suspend fun signUp(user: User): LoginResult
    
    /**
     * 사용자 정보 조회
     * 
     * Result<User>:
     * - 성공: User 객체 반환
     * - 실패: null 또는 에러 정보 반환
     */
    suspend fun getUserById(userId: Int): Result<User>
    
    /**
     * 로그인 상태 확인
     * 
     * 로컬 저장소에서 확인:
     * - SharedPreferences나 DataStore에서 토큰 확인
     * - 빠른 응답 (suspend 없어도 됨, 하지만 일관성을 위해 suspend 유지)
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * 로그아웃
     * 
     * 로컬 저장소 삭제:
     * - 토큰 삭제
     * - 사용자 정보 삭제
     */
    suspend fun logout()
    
    /**
     * 저장된 사용자 정보 가져오기
     * 
     * 로컬 저장소에서 조회:
     * - SharedPreferences나 DataStore
     * - 없으면 null 반환
     */
    suspend fun getSavedUser(): User?
}

/**
 * ========================================
 * Repository Pattern 개념 정리
 * ========================================
 * 
 * 1. Repository의 책임
 *    - 데이터 소스 추상화 (어디서 데이터를 가져오는지 숨김)
 *    - 데이터 접근 로직 캡슐화 (API 호출, DB 조회 등)
 *    - DTO ↔ Entity 변환 (Data Layer ↔ Domain Layer)
 *    - 에러 처리 및 변환 (Exception → Result)
 * 
 * 2. Repository의 구현체 종류
 *    - AuthRepositoryImpl: 실제 API 호출
 *    - FakeAuthRepository: 테스트용 가짜 구현
 *    - MockAuthRepository: 모킹 라이브러리 사용
 *    - CachedAuthRepository: 캐시 + API
 * 
 * 3. 왜 Interface로 분리하나?
 *    - 구현체 교체 용이 (의존성 역전)
 *    - 테스트 용이 (Fake 구현 사용)
 *    - 여러 데이터 소스 투명하게 사용
 * 
 * 4. Domain Layer의 역할
 *    - 비즈니스 로직 (Entity, UseCase)
 *    - Repository Interface (Data Layer 추상화)
 *    - Data Layer나 Presentation Layer에 독립적
 * 
 * 5. 데이터 흐름
 *    UI → ViewModel → UseCase → Repository → DataSource → API
 *     ↑                                         ↓
 *     Entity ← Entity ← Entity ← Entity ← DTO ← JSON
 * 
 * 6. 에러 처리 전략
 *    - Repository에서 모든 예외를 Result로 변환
 *    - UseCase와 ViewModel은 예외 처리 불필요
 *    - when으로 Result 타입만 체크하면 됨
 */
