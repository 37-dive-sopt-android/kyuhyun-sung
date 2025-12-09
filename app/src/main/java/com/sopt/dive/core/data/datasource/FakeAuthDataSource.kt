package com.sopt.dive.core.data.datasource

import com.sopt.dive.core.domain.entity.LoginResult
import com.sopt.dive.core.domain.entity.User

/**
 * FakeAuthDataSource - 테스트 및 개발용 가짜 데이터 소스
 * 
 * DataSource Pattern이란?
 * - 데이터의 출처(Source)를 추상화하는 패턴
 * - Remote (API), Local (DB), Fake (테스트용) 등 다양한 출처 가능
 * 
 * FakeDataSource의 목적:
 * 1. 빠른 개발: API 없이도 UI 개발 가능
 * 2. 안정적인 테스트: 네트워크에 의존하지 않음
 * 3. 오프라인 개발: 인터넷 없이도 작업 가능
 * 4. 엣지 케이스 테스트: 성공/실패 시나리오 쉽게 재현
 * 
 * 실제 개발 시나리오:
 * 
 * 1단계: FakeDataSource로 UI 개발
 *    - 실제 API가 준비 안 됨
 *    - FakeDataSource로 즉시 개발 시작
 *    - UI 로직에 집중
 * 
 * 2단계: RealDataSource 구현
 *    - API가 준비되면 구현
 *    - Repository에서 주입만 바꾸면 됨
 *    - UI 코드는 변경 불필요
 * 
 * 3단계: 테스트
 *    - Unit Test: FakeDataSource 사용
 *    - Integration Test: RealDataSource 사용
 * 
 * DataSource 교체 방법:
 * 
 * 개발 환경:
 * val repository = AuthRepositoryImpl(FakeAuthDataSource())
 * 
 * 프로덕션 환경:
 * val repository = AuthRepositoryImpl(AuthRemoteDataSource(api))
 * 
 * Repository는 둘 중 어떤 DataSource를 받는지 모름!
 */
class FakeAuthDataSource {
    
    /**
     * 테스트용 사용자 계정 목록
     * 
     * 실제 앱이라면:
     * - 이 데이터는 서버 DB에 저장됨
     * - FakeDataSource는 이를 시뮬레이션
     */
    private val fakeUsers = mutableListOf(
        User(
            id = 1,
            username = "test",
            password = "password",
            displayName = "테스트 사용자",
            email = "test@example.com"
        ),
        User(
            id = 2,
            username = "admin",
            password = "admin123",
            displayName = "관리자",
            email = "admin@example.com"
        ),
        User(
            id = 3,
            username = "user",
            password = "user123",
            displayName = "일반 사용자",
            email = "user@example.com"
        )
    )
    
    /**
     * 다음 사용자 ID (회원가입 시 사용)
     * 
     * 실제 앱이라면:
     * - DB가 자동으로 ID 생성 (Auto Increment)
     * - FakeDataSource는 이를 시뮬레이션
     */
    private var nextUserId = 4
    
    /**
     * 가짜 로그인 함수
     * 
     * 동작:
     * 1. fakeUsers에서 username으로 사용자 찾기
     * 2. 비밀번호 일치 확인
     * 3. 일치하면 Success, 아니면 Error 반환
     * 
     * 실제 API라면:
     * - 서버가 DB를 조회
     * - 비밀번호 해시 비교
     * - JWT 토큰 생성 및 반환
     * 
     * Fake는 즉시 응답 (네트워크 지연 없음)
     * 필요하다면 delay()로 네트워크 지연 시뮬레이션 가능
     */
    suspend fun login(username: String, password: String): LoginResult {
        // 실제 API 지연 시뮬레이션 (선택사항)
        // delay(1000)  // 1초 지연
        
        // 사용자 찾기
        val user = fakeUsers.find { it.username == username }
        
        // 사용자가 없는 경우
        if (user == null) {
            return LoginResult.Error.InvalidCredentials(
                message = "존재하지 않는 사용자입니다"
            )
        }
        
        // 비밀번호 확인
        if (user.password != password) {
            return LoginResult.Error.InvalidCredentials(
                message = "비밀번호가 틀렸습니다"
            )
        }
        
        // 로그인 성공
        return LoginResult.Success(
            user = user,
            token = "fake_jwt_token_${user.id}"  // 가짜 토큰
        )
    }
    
    /**
     * 가짜 회원가입 함수
     * 
     * 동작:
     * 1. 중복 사용자명 확인
     * 2. 새 사용자 생성 (ID 자동 할당)
     * 3. fakeUsers에 추가
     * 4. 자동 로그인 (Success 반환)
     */
    suspend fun signUp(user: User): LoginResult {
        // 중복 확인
        val existingUser = fakeUsers.find { it.username == user.username }
        if (existingUser != null) {
            return LoginResult.Error.InvalidCredentials(
                message = "이미 존재하는 사용자입니다"
            )
        }
        
        // 새 사용자 생성 (ID 할당)
        val newUser = user.copy(id = nextUserId++)
        
        // 저장
        fakeUsers.add(newUser)
        
        // 자동 로그인
        return LoginResult.Success(
            user = newUser,
            token = "fake_jwt_token_${newUser.id}"
        )
    }
    
    /**
     * ID로 사용자 조회
     * 
     * 실제 API라면:
     * - GET /api/users/{id}
     * - 서버가 DB 조회
     */
    suspend fun getUserById(userId: Int): User? {
        return fakeUsers.find { it.id == userId }
    }
    
    /**
     * 모든 사용자 조회 (개발/테스트용)
     */
    fun getAllUsers(): List<User> {
        return fakeUsers.toList()  // 복사본 반환 (불변성)
    }
    
    /**
     * 데이터 초기화 (테스트용)
     * 
     * 사용 예시:
     * @Before
     * fun setUp() {
     *     fakeDataSource.reset()
     * }
     */
    fun reset() {
        fakeUsers.clear()
        fakeUsers.addAll(
            listOf(
                User(1, "test", "password", "테스트 사용자", "test@example.com"),
                User(2, "admin", "admin123", "관리자", "admin@example.com"),
                User(3, "user", "user123", "일반 사용자", "user@example.com")
            )
        )
        nextUserId = 4
    }
}

/**
 * ========================================
 * FakeDataSource 사용 예시
 * ========================================
 * 
 * 1. Repository에서 사용:
 *    class AuthRepositoryImpl(
 *        private val dataSource: FakeAuthDataSource
 *    ) : AuthRepository {
 *        override suspend fun login(...): LoginResult {
 *            return dataSource.login(...)
 *        }
 *    }
 * 
 * 2. ViewModel 테스트:
 *    @Test
 *    fun `로그인 성공 시 UI 상태 변경`() = runTest {
 *        // Given
 *        val fakeDataSource = FakeAuthDataSource()
 *        val repository = AuthRepositoryImpl(fakeDataSource)
 *        val useCase = LoginUseCase(repository)
 *        val viewModel = LoginViewModel(useCase)
 *        
 *        // When
 *        viewModel.login("test", "password")
 *        
 *        // Then
 *        assertTrue(viewModel.uiState.value.loginSuccess)
 *    }
 * 
 * 3. UI 개발:
 *    - API가 준비 안 됐을 때
 *    - FakeDataSource로 먼저 UI 개발
 *    - 나중에 RealDataSource로 교체
 * 
 * 4. 엣지 케이스 테스트:
 *    val fakeDataSource = FakeAuthDataSource()
 *    
 *    // 성공 시나리오
 *    val success = fakeDataSource.login("test", "password")
 *    
 *    // 실패 시나리오
 *    val failure = fakeDataSource.login("wrong", "wrong")
 *    
 *    // 네트워크 에러 시뮬레이션 (직접 Result 반환)
 *    val networkError = LoginResult.Error.NetworkError()
 */
