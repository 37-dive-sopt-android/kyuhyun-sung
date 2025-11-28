package com.sopt.dive.feature.signup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.datasource.ServicePool
import com.sopt.dive.core.data.dto.RequestSignUpDto
import com.sopt.dive.feature.signup.SignUpData
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * SignUpViewModel - 코루틴 기반 회원가입
 * 
 * Callback → 코루틴 변환 완료
 * 더 간결하고 읽기 쉬운 코드로 개선되었습니다.
 */
class SignUpViewModel : ViewModel() {

    private val authService = ServicePool.authService

    private val _id = mutableStateOf("")
    val id: State<String> = _id

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _nickname = mutableStateOf("")
    val nickname: State<String> = _nickname

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _age = mutableStateOf("")
    val age: State<String> = _age

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun onIdChange(newId: String) {
        _id.value = newId
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onNicknameChange(newNickname: String) {
        _nickname.value = newNickname
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onAgeChange(newAge: String) {
        _age.value = newAge
    }

    /**
     * 입력값 검증
     * 
     * 회원가입 전 모든 입력값이 유효한지 확인합니다.
     * null 반환 = 모든 검증 통과
     */
    fun validateInput(): String? {
        return when {
            _id.value.length !in 6..10 -> "ID는 6~10글자여야 합니다"
            _password.value.length !in 8..12 -> "PW는 8~12글자여야 합니다"
            _nickname.value.isBlank() -> "이름을 입력해주세요"
            _email.value.isBlank() -> "이메일을 입력해주세요"
            !_email.value.contains("@") -> "올바른 이메일 형식이 아닙니다"
            _age.value.isBlank() -> "나이를 입력해주세요"
            _age.value.toIntOrNull() == null -> "나이는 숫자여야 합니다"
            (_age.value.toIntOrNull() ?: 0) !in 14..100 -> "나이는 14~100 사이여야 합니다"
            else -> null
        }
    }

    fun getSignUpData(): SignUpData {
        return SignUpData(
            userId = _id.value,
            password = _password.value,
            nickname = _nickname.value,
            email = _email.value,
            age = _age.value.toIntOrNull() ?: 0
        )
    }

    /**
     * 코루틴 기반 회원가입 API 호출
     * 
     * ❌ 이전 Callback 방식 (25줄):
     * authService.signUp(request).enqueue(object : Callback<ResponseSignUpDto> {
     *     override fun onResponse(...) { }
     *     override fun onFailure(...) { }
     * })
     * 
     * ✅ 현재 코루틴 방식 (10줄):
     * viewModelScope.launch {
     *     try {
     *         val response = authService.signUp(request)
     *     } catch (e: Exception) { }
     * }
     */
    fun signUpWithApi(onSuccess: () -> Unit, onError: (String) -> Unit) {
        /**
         * viewModelScope.launch:
         * - ViewModel 생명주기에 맞춰 자동 관리
         * - 화면이 파괴되면 자동으로 취소됨
         * - 메모리 누수 방지
         */
        viewModelScope.launch {
            // 로딩 시작
            _isLoading.value = true

            /**
             * try-catch 블록:
             * - Callback의 onResponse/onFailure를 대체
             * - 더 직관적인 에러 처리
             */
            try {
                // API 요청 객체 생성
                val request = RequestSignUpDto(
                    username = _id.value,
                    password = _password.value,
                    name = _nickname.value,
                    email = _email.value,
                    age = _age.value.toIntOrNull() ?: 25
                )

                /**
                 * authService.signUp(request):
                 * - suspend 함수 호출
                 * - 네트워크 통신 중 일시 중단
                 * - 응답이 오면 자동으로 재개
                 */
                val userData = authService.signUp(request)
                
                // 여기 도달 = 네트워크 통신 성공
                
                // 로딩 종료
                _isLoading.value = false

                // API 응답 처리
                if (userData.success) {
                    // 회원가입 성공
                    Log.d("SignUp", "회원가입 성공: ${userData.data.name}")
                    onSuccess()
                } else {
                    // API에서 success = false 응답
                    Log.e("SignUp", "서버 에러: ${userData.message}")
                    onError(userData.message ?: "알 수 없는 오류")
                }

            } catch (e: HttpException) {
                /**
                 * HttpException:
                 * - HTTP 에러 코드 (400, 401, 409, 500 등)
                 * - 서버가 응답은 했지만 에러 코드를 반환
                 * 
                 * 예시:
                 * - 409: 중복된 사용자명
                 * - 400: 잘못된 요청
                 * - 500: 서버 내부 오류
                 */
                _isLoading.value = false
                
                val errorMsg = when (e.code()) {
                    409 -> "이미 존재하는 사용자명입니다"
                    400 -> "입력 정보를 확인해주세요"
                    500 -> "서버 오류가 발생했습니다"
                    else -> "회원가입 실패: ${e.message()}"
                }
                
                Log.e("SignUp", "HTTP 에러 ${e.code()}: ${e.message()}")
                onError(errorMsg)

            } catch (e: IOException) {
                /**
                 * IOException:
                 * - 네트워크 연결 문제
                 * - 서버에 도달하지 못함
                 * 
                 * 예시:
                 * - 인터넷 연결 끊김
                 * - 타임아웃
                 * - DNS 실패
                 */
                _isLoading.value = false
                
                val errorMsg = "네트워크 연결을 확인해주세요"
                Log.e("SignUp", "네트워크 오류: ${e.message}")
                onError(errorMsg)

            } catch (e: Exception) {
                /**
                 * Exception:
                 * - 그 외 모든 예외
                 * - 파싱 에러, 예상치 못한 에러 등
                 */
                _isLoading.value = false
                
                val errorMsg = "알 수 없는 오류: ${e.message}"
                Log.e("SignUp", "예외 발생: ${e.message}")
                onError(errorMsg)
            }
        }
    }
}

/**
 * ========================================
 * 코루틴 변환의 장점 (SignUpViewModel 예시)
 * ========================================
 * 
 * 코드 라인 수 비교:
 * - Callback 방식: 약 25줄
 * - 코루틴 방식: 약 15줄
 * - 40% 코드 감소!
 * 
 * 가독성:
 * - Callback: 중첩된 구조로 흐름 파악 어려움
 * - 코루틴: 순차적으로 읽힘, 흐름이 명확함
 * 
 * 에러 처리:
 * - Callback: onResponse와 onFailure로 분산
 * - 코루틴: try-catch로 한곳에 집중
 * 
 * 유지보수:
 * - Callback: 수정 시 여러 곳 변경 필요
 * - 코루틴: 한곳만 수정하면 됨
 * 
 * 
 * ========================================
 * 실행 흐름
 * ========================================
 * 
 * 사용자가 "회원가입" 버튼 클릭
 *   ↓
 * signUpWithApi() 호출
 *   ↓
 * viewModelScope.launch { } 시작
 *   ↓
 * isLoading = true
 *   ↓ (UI: 로딩 표시)
 * 
 * authService.signUp(request) 호출
 *   ↓ (코루틴 일시 중단)
 * 
 * [네트워크 통신 중]
 * - 메인 스레드 블로킹 없음
 * - UI 반응 유지
 *   ↓
 * 
 * 응답 도착 → 코루틴 재개
 *   ↓
 * isLoading = false
 *   ↓
 * 성공/실패 처리
 *   ↓
 * onSuccess() 또는 onError() 콜백 호출
 *   ↓
 * 완료!
 */
