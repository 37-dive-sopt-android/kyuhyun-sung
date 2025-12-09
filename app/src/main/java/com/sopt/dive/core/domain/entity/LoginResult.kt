package com.sopt.dive.core.domain.entity

/**
 * LoginResult - 로그인 결과를 나타내는 Sealed Class
 * 
 * Sealed Class란?
 * - "봉인된 클래스" (제한된 상속)
 * - 특정 타입들만 상속할 수 있도록 제한
 * - when 표현식에서 모든 경우를 강제로 처리하게 만듦
 * 
 * 왜 Sealed Class를 사용하나?
 * 
 * ❌ Boolean/Int로 상태 표현 (나쁜 방법):
 * val success: Boolean  // true/false만 표현 가능
 * val errorCode: Int?   // 에러 코드 저장
 * 
 * 문제점:
 * - 성공 시 에러 코드는? → null로 처리 (혼란스러움)
 * - 에러 타입이 여러 개면? → errorCode로 구분 (가독성 떨어짐)
 * - when으로 처리할 때 컴파일러가 체크 안 함 (버그 발생 가능)
 * 
 * ✅ Sealed Class (좋은 방법):
 * sealed class Result {
 *     data class Success(...) : Result()
 *     data class Error(...) : Result()
 * }
 * 
 * 장점:
 * - 명확한 타입 표현 (Success, Error가 별도 타입)
 * - when 표현식에서 컴파일러가 모든 경우를 체크
 * - 각 타입별로 다른 데이터를 안전하게 저장
 * 
 * 실제 사용 예시:
 * when (result) {
 *     is LoginResult.Success -> {
 *         // result.user 사용 가능 (타입 안전)
 *         navigateToHome(result.user)
 *     }
 *     is LoginResult.Error -> {
 *         // result.message 사용 가능 (타입 안전)
 *         showError(result.message)
 *     }
 * }
 * // 컴파일러가 모든 경우를 체크했으므로 else 불필요!
 */
sealed class LoginResult {
    
    /**
     * 로그인 성공
     * 
     * data class:
     * - equals(), hashCode(), toString(), copy() 자동 생성
     * - 불변 객체로 안전함
     * 
     * 포함 정보:
     * - user: 로그인한 사용자 정보
     * - token: 인증 토큰 (선택사항)
     */
    data class Success(
        val user: User,
        val token: String? = null
    ) : LoginResult() {
        /**
         * 성공 메시지 생성
         * - UI에서 토스트나 스낵바에 사용
         */
        fun getWelcomeMessage(): String {
            return "${user.username}님 환영합니다!"
        }
    }
    
    /**
     * 로그인 실패
     * 
     * 실패 타입:
     * - InvalidCredentials: 잘못된 인증 정보
     * - NetworkError: 네트워크 에러
     * - ServerError: 서버 에러
     * - Unknown: 알 수 없는 에러
     */
    sealed class Error : LoginResult() {
        /**
         * 모든 에러의 공통 속성
         */
        abstract val message: String
        
        /**
         * 잘못된 인증 정보 (401)
         * - 아이디/비밀번호가 틀림
         */
        data class InvalidCredentials(
            override val message: String = "아이디 또는 비밀번호가 틀렸습니다"
        ) : Error()
        
        /**
         * 네트워크 에러
         * - 인터넷 연결 끊김
         * - 타임아웃
         */
        data class NetworkError(
            override val message: String = "네트워크 연결을 확인해주세요",
            val cause: Throwable? = null
        ) : Error()
        
        /**
         * 서버 에러 (500)
         */
        data class ServerError(
            override val message: String = "서버 오류가 발생했습니다",
            val code: Int? = null
        ) : Error()
        
        /**
         * 알 수 없는 에러
         */
        data class Unknown(
            override val message: String = "알 수 없는 오류가 발생했습니다",
            val cause: Throwable? = null
        ) : Error()
    }
}

/**
 * Sealed Class vs Enum 비교
 * 
 * Enum (제한적):
 * enum class LoginState {
 *     SUCCESS,  // 데이터를 담을 수 없음
 *     ERROR     // 에러 메시지를 어떻게 전달?
 * }
 * 
 * Sealed Class (유연함):
 * sealed class LoginResult {
 *     data class Success(val user: User) : LoginResult()
 *     data class Error(val message: String) : LoginResult()
 * }
 * 
 * 차이점:
 * - Enum: 값만 표현 (상태만 저장)
 * - Sealed Class: 값 + 데이터 표현 (상태 + 관련 데이터 저장)
 */
