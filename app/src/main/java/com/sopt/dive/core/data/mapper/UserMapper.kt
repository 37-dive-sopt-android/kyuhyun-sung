package com.sopt.dive.core.data.mapper

import com.sopt.dive.core.data.dto.ResponseLoginDto
import com.sopt.dive.core.data.dto.ResponseUserDto
import com.sopt.dive.core.domain.entity.User

/**
 * Mapper - DTO와 Entity 간의 변환을 담당
 * 
 * Mapper란?
 * - 한 데이터 형식을 다른 형식으로 변환하는 함수들의 모음
 * - DTO (Data Transfer Object) ↔ Entity 변환
 * 
 * 왜 Mapper가 필요한가?
 * 
 * DTO vs Entity:
 * 
 * DTO (ResponseLoginDto):
 * {
 *   "success": true,
 *   "code": "SUCCESS",
 *   "message": "로그인 성공",
 *   "data": {
 *     "userId": 123,
 *     "message": "환영합니다"
 *   }
 * }
 * 
 * Entity (User):
 * User(
 *   id = 123,
 *   username = "john",
 *   password = "******"
 * )
 * 
 * 차이점:
 * - DTO: API 응답 형식 그대로 (중첩된 구조, 불필요한 필드)
 * - Entity: 앱 내부에서 사용하기 쉬운 형식 (평평한 구조, 필요한 필드만)
 * 
 * Mapper의 역할:
 * 1. DTO → Entity 변환 (API 응답을 앱 내부 모델로)
 * 2. Entity → DTO 변환 (앱 내부 모델을 API 요청으로)
 * 3. 필드명 매핑 (API와 앱의 필드명이 다를 때)
 * 4. 타입 변환 (String → Int, Date 등)
 * 5. 기본값 설정 (null → 기본값)
 * 
 * Extension Function을 사용하는 이유:
 * 
 * ❌ 일반 함수 (불편함):
 * object UserMapper {
 *     fun mapToEntity(dto: ResponseLoginDto, username: String): User {
 *         return User(...)
 *     }
 * }
 * 
 * 사용:
 * val user = UserMapper.mapToEntity(dto, username)  // 길고 불편
 * 
 * ✅ Extension Function (간편함):
 * fun ResponseLoginDto.toEntity(username: String): User {
 *     return User(...)
 * }
 * 
 * 사용:
 * val user = dto.toEntity(username)  // 간결하고 직관적
 * 
 * 장점:
 * - 코드가 간결함
 * - 메서드 체이닝 가능
 * - IDE 자동완성 지원
 */

/**
 * ResponseLoginDto → User 변환
 * 
 * API 응답에서 사용자 엔티티 생성:
 * - API는 userId만 제공
 * - username과 password는 로그인 시 입력한 값 사용
 * 
 * @param username 로그인 시 입력한 사용자명
 * @param password 로그인 시 입력한 비밀번호
 * @return User 엔티티
 */
fun ResponseLoginDto.toEntity(username: String, password: String): User {
    return User(
        id = this.data.userId,           // DTO의 중첩된 필드 접근
        username = username,              // 파라미터로 전달받은 값
        password = password,              // 파라미터로 전달받은 값
        displayName = username,           // 기본값으로 username 사용
        email = null                      // API가 제공하지 않으면 null
    )
}

/**
 * ResponseUserDto → User 변환
 * 
 * 사용자 정보 조회 API 응답 변환:
 * - API가 사용자 상세 정보 제공
 * - password는 보안상 API에서 제공하지 않음
 * 
 * 실제 API 응답 구조:
 * {
 *   "success": true,
 *   "data": {
 *     "id": 1,
 *     "username": "john",
 *     "name": "John Doe",    // displayName이 아닌 name
 *     "email": "john@example.com",
 *     "age": 25,
 *     "status": "active"
 *   }
 * }
 * 
 * @return User 엔티티 (password는 빈 문자열)
 */
fun ResponseUserDto.toEntity(): User {
    return User(
        id = this.data.id,
        username = this.data.username,
        password = "",  // 보안상 API에서 제공하지 않음
        displayName = this.data.name,  // API의 name → Entity의 displayName
        email = this.data.email
    )
}
