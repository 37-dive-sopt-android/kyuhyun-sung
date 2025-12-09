package com.sopt.dive.core.domain.entity

/**
 * User Entity (Domain Layer의 핵심 모델)
 * 
 * Entity란?
 * - 비즈니스 로직의 핵심 데이터 모델
 * - 앱의 "진짜 데이터"를 표현
 * - DTO(Data Transfer Object)와 달리 API 응답 형식에 독립적
 * - 불변(immutable) 객체로 설계하여 안전성 보장
 * 
 * DTO vs Entity 차이:
 * 
 * DTO (Data Transfer Object):
 * - API 응답/요청 형식 그대로 표현
 * - JSON 필드명과 일치 (@SerialName 사용)
 * - 네트워크 계층에 종속적
 * - 예: ResponseLoginDto, RequestLoginDto
 * 
 * Entity:
 * - 앱 내부에서 사용하는 깔끔한 모델
 * - 비즈니스 로직에 필요한 필드만 포함
 * - 네트워크, DB에 독립적
 * - 예: User, LoginResult
 * 
 * 왜 분리하나?
 * 1. API 변경에 유연함 (API가 바뀌어도 Entity는 안 바꿔도 됨)
 * 2. 테스트 용이함 (네트워크 없이 테스트 가능)
 * 3. 코드 가독성 향상 (불필요한 API 필드 제거)
 */
data class User(
    /**
     * 사용자 고유 ID
     * - DB/API에서 관리하는 식별자
     * - nullable: 회원가입 전에는 ID가 없을 수 있음
     */
    val id: Int? = null,
    
    /**
     * 사용자 이름 (로그인용)
     * - 3~10자의 영문/숫자
     */
    val username: String,
    
    /**
     * 비밀번호
     * - 실제로는 해시된 값을 저장하는 것이 안전
     * - 여기서는 학습용으로 평문 사용
     */
    val password: String,
    
    /**
     * 표시 이름 (선택사항)
     * - 화면에 보여줄 이름
     * - username과 다를 수 있음
     */
    val displayName: String? = null,
    
    /**
     * 이메일 (선택사항)
     */
    val email: String? = null
) {
    /**
     * Entity의 유효성 검증 로직
     * 
     * 장점:
     * - 비즈니스 규칙을 Entity 내부에 캡슐화
     * - ViewModel이나 UseCase에서 재사용 가능
     * - 테스트가 쉬움
     */
    fun isValid(): Boolean {
        return username.length in 3..10 && password.length >= 6
    }
    
    /**
     * 비밀번호 유효성 검증
     */
    fun isPasswordValid(): Boolean = password.length >= 6
    
    /**
     * 사용자명 유효성 검증
     */
    fun isUsernameValid(): Boolean = username.length in 3..10
}
