package com.sopt.dive.core.domain.usecase

import com.sopt.dive.core.domain.entity.LoginResult
import com.sopt.dive.core.domain.repository.AuthRepository

/**
 * LoginUseCase - 로그인 비즈니스 로직을 담당하는 UseCase
 * 
 * UseCase란?
 * - "사용 사례"를 표현하는 클래스
 * - 하나의 비즈니스 기능을 수행
 * - ViewModel과 Repository 사이의 중간 계층
 * 
 * UseCase의 역할:
 * 1. 비즈니스 로직 실행 (검증, 변환, 조합 등)
 * 2. Repository 호출 및 결과 처리
 * 3. 여러 Repository를 조합 (필요시)
 * 4. 재사용 가능한 비즈니스 로직 제공
 * 
 * UseCase가 필요한 이유:
 * 
 * ❌ UseCase 없이 (ViewModel이 Repository 직접 호출):
 * class LoginViewModel(private val repository: AuthRepository) {
 *     fun login(username: String, password: String) {
 *         viewModelScope.launch {
 *             // 검증 로직
 *             if (username.isBlank()) {
 *                 _uiState.value = LoginUiState(error = "ID를 입력하세요")
 *                 return@launch
 *             }
 *             if (password.isBlank()) {
 *                 _uiState.value = LoginUiState(error = "비밀번호를 입력하세요")
 *                 return@launch
 *             }
 *             
 *             // API 호출
 *             val result = repository.login(username, password)
 *             
 *             // 결과 처리
 *             when (result) {
 *                 is LoginResult.Success -> { /* ... */ }
 *                 is LoginResult.Error -> { /* ... */ }
 *             }
 *         }
 *     }
 * }
 * 
 * 문제점:
 * - ViewModel이 비즈니스 로직과 UI 로직을 모두 처리 (책임 과다)
 * - 로그인 기능이 여러 화면에서 필요하면 코드 중복
 * - 테스트 시 ViewModel 전체를 테스트해야 함 (복잡함)
 * 
 * ✅ UseCase 사용 (권장):
 * class LoginViewModel(private val loginUseCase: LoginUseCase) {
 *     fun login(username: String, password: String) {
 *         viewModelScope.launch {
 *             val result = loginUseCase(username, password)  // 간결!
 *             
 *             when (result) {
 *                 is LoginResult.Success -> { /* UI 업데이트만 */ }
 *                 is LoginResult.Error -> { /* UI 업데이트만 */ }
 *             }
 *         }
 *     }
 * }
 * 
 * 장점:
 * - ViewModel은 UI 로직에만 집중
 * - UseCase는 비즈니스 로직에만 집중 (단일 책임 원칙)
 * - UseCase를 여러 ViewModel에서 재사용 가능
 * - UseCase만 따로 테스트 가능 (테스트 용이)
 * 
 * UseCase 네이밍 규칙:
 * - 동사 + 명사 형태: LoginUseCase, SignUpUseCase
 * - 또는: DoSomethingUseCase 형태
 * 
 * UseCase의 특징:
 * 1. 하나의 기능만 수행 (Single Responsibility)
 * 2. suspend 함수로 구현 (코루틴 사용)
 * 3. operator fun invoke() 사용 (호출을 간결하게)
 */
class LoginUseCase(
    /**
     * Repository Interface에 의존
     * - 구현체를 모름 (의존성 역전)
     * - 테스트 시 FakeRepository 주입 가능
     */
    private val authRepository: AuthRepository
) {
    
    /**
     * operator fun invoke()
     * 
     * invoke()의 특별한 점:
     * - 객체를 함수처럼 호출 가능
     * 
     * 일반 함수 호출:
     * val result = loginUseCase.execute(username, password)
     * 
     * invoke 사용 시:
     * val result = loginUseCase(username, password)  // 간결!
     * 
     * 장점:
     * - 코드가 간결해짐
     * - UseCase를 함수형 프로그래밍처럼 사용 가능
     * - 가독성 향상
     */
    suspend operator fun invoke(
        username: String,
        password: String
    ): LoginResult {
        /**
         * 비즈니스 로직 실행 순서:
         * 1. 입력 검증 (빠른 실패)
         * 2. Repository 호출
         * 3. 결과 후처리 (필요시)
         */
        
        // ========================================
        // 1. 입력 검증 (빠른 실패 전략)
        // ========================================
        
        /**
         * 빠른 실패 (Fail Fast) 전략:
         * - 문제가 있으면 즉시 에러 반환
         * - 불필요한 네트워크 호출 방지
         * - 사용자에게 빠른 피드백 제공
         */
        
        // 빈 값 검증
        if (username.isBlank()) {
            return LoginResult.Error.InvalidCredentials(
                message = "아이디를 입력해주세요"
            )
        }
        
        if (password.isBlank()) {
            return LoginResult.Error.InvalidCredentials(
                message = "비밀번호를 입력해주세요"
            )
        }
        
        // 길이 검증
        if (username.length < 3 || username.length > 10) {
            return LoginResult.Error.InvalidCredentials(
                message = "아이디는 3~10자여야 합니다"
            )
        }
        
        if (password.length < 6) {
            return LoginResult.Error.InvalidCredentials(
                message = "비밀번호는 6자 이상이어야 합니다"
            )
        }
        
        // ========================================
        // 2. Repository를 통한 로그인 수행
        // ========================================
        
        /**
         * Repository 호출:
         * - suspend 함수이므로 네트워크 호출 가능
         * - Repository가 모든 에러를 Result로 변환
         * - UseCase는 예외 처리 불필요
         */
        val result = authRepository.login(
            username = username.trim(),  // 공백 제거
            password = password
        )
        
        // ========================================
        // 3. 결과 후처리 (필요한 경우)
        // ========================================
        
        /**
         * 여기서 추가 비즈니스 로직 수행 가능:
         * - 로그인 성공 시 분석 이벤트 전송
         * - 실패 횟수 카운트
         * - 로그 기록
         * - 캐시 업데이트 등
         */
        
        // 예시: 로그인 성공 시 로그 기록
        if (result is LoginResult.Success) {
            // Analytics.logEvent("login_success")
            // LogManager.log("User ${result.user.username} logged in")
        }
        
        return result
    }
}

/**
 * ========================================
 * UseCase 패턴 개념 정리
 * ========================================
 * 
 * 1. UseCase의 책임
 *    - 비즈니스 로직 실행 (검증, 변환, 조합)
 *    - Repository 호출 및 조합
 *    - 재사용 가능한 기능 제공
 * 
 * 2. UseCase vs ViewModel
 * 
 *    UseCase:
 *    - 비즈니스 로직 (플랫폼 독립적)
 *    - 재사용 가능
 *    - 쉬운 테스트
 *    - Android 의존성 없음
 * 
 *    ViewModel:
 *    - UI 로직 (UI 상태 관리)
 *    - 화면 특화
 *    - LifecycleOwner 의존
 *    - Android 의존성 있음
 * 
 * 3. UseCase의 구조
 *    - Repository를 주입받음 (의존성 주입)
 *    - operator fun invoke() 사용 (간결한 호출)
 *    - suspend 함수로 구현 (비동기 작업)
 * 
 * 4. 언제 UseCase를 만드나?
 * 
 *    UseCase 필요:
 *    - 복잡한 비즈니스 로직
 *    - 여러 Repository 조합
 *    - 재사용 가능한 로직
 *    - 테스트가 중요한 로직
 * 
 *    UseCase 불필요:
 *    - 단순 CRUD (Repository 직접 호출)
 *    - 한 번만 사용되는 로직
 *    - 비즈니스 로직 없음
 * 
 * 5. UseCase 테스트 예시
 *    @Test
 *    fun `빈 아이디로 로그인 시 에러 반환`() = runTest {
 *        // Given
 *        val useCase = LoginUseCase(fakeRepository)
 *        
 *        // When
 *        val result = useCase("", "password")
 *        
 *        // Then
 *        assertTrue(result is LoginResult.Error.InvalidCredentials)
 *    }
 * 
 * 6. 여러 Repository 조합 예시
 *    class ComplexUseCase(
 *        private val authRepository: AuthRepository,
 *        private val userRepository: UserRepository,
 *        private val analyticsRepository: AnalyticsRepository
 *    ) {
 *        suspend operator fun invoke() {
 *            val user = authRepository.getCurrentUser()
 *            val profile = userRepository.getProfile(user.id)
 *            analyticsRepository.logEvent("profile_viewed")
 *            return profile
 *        }
 *    }
 */
