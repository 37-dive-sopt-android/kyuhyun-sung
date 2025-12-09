package com.sopt.dive.core.data.repository

import com.sopt.dive.core.data.datasource.FakeAuthDataSource
import com.sopt.dive.core.domain.entity.LoginResult
import com.sopt.dive.core.domain.entity.User
import com.sopt.dive.core.domain.repository.AuthRepository

/**
 * AuthRepositoryImpl - Repository 인터페이스의 실제 구현
 * 
 * Repository Implementation의 역할:
 * 1. DataSource 호출
 * 2. DTO → Entity 변환 (Mapper 사용)
 * 3. 여러 DataSource 조합 (필요시)
 * 4. 캐싱 로직 (필요시)
 * 5. 에러 처리 및 변환
 */
class AuthRepositoryImpl(
    /**
     * DataSource 주입
     * 
     * 현재는 Fake만 사용:
     * - API 준비 전에도 개발 가능
     * - 테스트 용이
     * - 빠른 프로토타이핑
     * 
     * 나중에 Real로 교체:
     * - 생성자 파라미터만 변경
     * - Repository 코드는 변경 불필요
     */
    private val dataSource: FakeAuthDataSource
) : AuthRepository {
    
    /**
     * 로그인 구현
     * 
     * 로직:
     * 1. DataSource 호출
     * 2. 결과 반환 (Fake는 이미 LoginResult 반환)
     */
    override suspend fun login(
        username: String,
        password: String
    ): LoginResult {
        return dataSource.login(username, password)
    }
    
    /**
     * 회원가입 구현
     */
    override suspend fun signUp(user: User): LoginResult {
        return dataSource.signUp(user)
    }
    
    /**
     * 사용자 조회 구현
     */
    override suspend fun getUserById(userId: Int): Result<User> {
        val user = dataSource.getUserById(userId)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("사용자를 찾을 수 없습니다"))
        }
    }
    
    /**
     * 로그인 상태 확인
     */
    override suspend fun isLoggedIn(): Boolean {
        // Fake 구현: 항상 false
        // 실제: SharedPreferences에서 확인
        return false
    }
    
    /**
     * 로그아웃
     */
    override suspend fun logout() {
        // Fake 구현: 아무것도 안 함
        // 실제: SharedPreferences.clear()
    }
    
    /**
     * 저장된 사용자 정보 가져오기
     */
    override suspend fun getSavedUser(): User? {
        // Fake 구현: null 반환
        // 실제: SharedPreferences에서 조회
        return null
    }
}
