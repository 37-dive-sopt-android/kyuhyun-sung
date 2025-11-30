package com.sopt.dive.feature.my

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.datasource.ServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * MyViewModel - 코루틴 기반 API 호출
 * 
 * Callback → 코루틴 변환 예시
 * 이 ViewModel은 사용자 정보를 서버에서 조회하는 기능을 제공합니다.
 */
class MyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState

    private val authService = ServicePool.authService

    /**
     * 기존 로컬 정보 설정 (호환성 유지)
     * 네비게이션에서 전달받은 사용자 정보를 즉시 표시
     */
    fun setUserInfo(userId: String, userPw: String, userNickname: String, userEmail: String, userAge: Int) {
        _uiState.value = MyUiState(
            userId = userId,
            userPw = userPw,
            userNickname = userNickname,
            userEmail = userEmail,
            userAge = userAge
        )
    }

    /**
     * 서버에서 사용자 정보 가져오기 (코루틴 버전)
     * 
     * 실시간으로 최신 정보를 조회하여 UI를 업데이트합니다.
     * 
     * ❌ 이전 Callback 방식 (15줄):
     * authService.getUserById(userId).enqueue(object : Callback<ResponseUserDto> {
     *     override fun onResponse(...) { }
     *     override fun onFailure(...) { }
     * })
     * 
     * ✅ 현재 코루틴 방식 (5줄):
     * viewModelScope.launch {
     *     try {
     *         val response = authService.getUserById(userId)
     *     } catch (e: Exception) { }
     * }
     */
    fun fetchUserFromServer(userId: Int) {
        /**
         * viewModelScope.launch:
         * - ViewModel 생명주기에 맞춰 자동 관리되는 코루틴 스코프
         * - ViewModel이 onCleared()되면 자동으로 취소
         * - 메모리 누수 방지
         */
        viewModelScope.launch {
            // 로딩 시작
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            /**
             * try-catch 블록:
             * - suspend 함수에서 발생하는 예외를 처리
             * - Callback의 onResponse/onFailure를 대체
             * 
             * 장점:
             * 1. 코드가 순차적으로 읽힘
             * 2. 중첩된 콜백 없음
             * 3. 에러 처리가 직관적
             */
            try {
                /**
                 * authService.getUserById(userId):
                 * - suspend 함수 호출
                 * - 네트워크 통신 중 코루틴이 일시 중단됨
                 * - 메인 스레드는 블로킹되지 않음 (UI 반응 가능)
                 * - 응답이 오면 자동으로 재개
                 * 
                 * 일시 중단의 의미:
                 * - "잠깐 멈추고 다른 작업 하세요~"
                 * - 스레드를 양보하여 UI가 끊기지 않음
                 * - 네트워크 응답이 오면 자동으로 돌아옴
                 */
                val userData = authService.getUserById(userId)
                
                /**
                 * 여기 도달 = 네트워크 통신 성공
                 * userData 객체에 API 응답이 담겨있음
                 */
                
                // 로딩 종료
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                // API 응답 처리
                if (userData.success) {
                    // 서버에서 받은 최신 정보로 업데이트
                    Log.d("MyViewModel", "사용자 정보 조회 성공: ${userData.data.name}")
                    
                    _uiState.value = _uiState.value.copy(
                        userId = userData.data.username,
                        userNickname = userData.data.name,
                        userEmail = userData.data.email,
                        userAge = userData.data.age,
                        errorMessage = null
                    )
                } else {
                    // API에서 success = false 응답
                    _uiState.value = _uiState.value.copy(
                        errorMessage = userData.message ?: "사용자 정보를 불러올 수 없습니다"
                    )
                }
                
            } catch (e: HttpException) {
                /**
                 * HttpException:
                 * - HTTP 에러 코드가 있는 경우 (400, 401, 404, 500 등)
                 * - 서버가 응답은 했지만 에러 코드를 반환
                 * 
                 * 예시:
                 * - 404: 사용자를 찾을 수 없음
                 * - 401: 인증 실패
                 * - 500: 서버 내부 오류
                 */
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                val errorMsg = when (e.code()) {
                    404 -> "사용자를 찾을 수 없습니다"
                    401 -> "인증이 필요합니다"
                    500 -> "서버 오류가 발생했습니다"
                    else -> "서버 오류: ${e.message()}"
                }
                
                _uiState.value = _uiState.value.copy(errorMessage = errorMsg)
                Log.e("MyViewModel", "HTTP 에러: ${e.code()} - ${e.message()}")
                
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
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                val errorMsg = "네트워크 연결을 확인해주세요"
                _uiState.value = _uiState.value.copy(errorMessage = errorMsg)
                Log.e("MyViewModel", "네트워크 오류: ${e.message}")
                
            } catch (e: Exception) {
                /**
                 * Exception:
                 * - 그 외 모든 예외
                 * - 파싱 에러, 예상치 못한 에러 등
                 */
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                val errorMsg = "알 수 없는 오류: ${e.message}"
                _uiState.value = _uiState.value.copy(errorMessage = errorMsg)
                Log.e("MyViewModel", "예외 발생: ${e.message}")
            }
        }
    }
}

/**
 * ========================================
 * 코루틴 변환 전후 비교 (실제 코드)
 * ========================================
 * 
 * ❌ 이전 Callback 방식 (복잡함, 25줄):
 * 
 * fun fetchUserFromServer(userId: Int) {
 *     viewModelScope.launch {
 *         _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
 * 
 *         authService.getUserById(userId).enqueue(object : Callback<ResponseUserDto> {
 *             override fun onResponse(call: Call<ResponseUserDto>, response: Response<ResponseUserDto>) {
 *                 _uiState.value = _uiState.value.copy(isLoading = false)
 * 
 *                 if (response.isSuccessful) {
 *                     val userData = response.body()
 *                     if (userData?.success == true) {
 *                         _uiState.value = _uiState.value.copy(
 *                             userId = userData.data.username,
 *                             userNickname = userData.data.name,
 *                             // ...
 *                         )
 *                     }
 *                 } else {
 *                     val errorMsg = when (response.code()) {
 *                         404 -> "사용자를 찾을 수 없습니다"
 *                         // ...
 *                     }
 *                 }
 *             }
 * 
 *             override fun onFailure(call: Call<ResponseUserDto>, t: Throwable) {
 *                 _uiState.value = _uiState.value.copy(
 *                     isLoading = false,
 *                     errorMessage = "네트워크 오류: ${t.message}"
 *                 )
 *             }
 *         })
 *     }
 * }
 * 
 * 문제점:
 * 1. 중첩이 깊음 (콜백 안에 또 콜백)
 * 2. 에러 처리가 분산됨 (onResponse, onFailure)
 * 3. 코드 흐름이 복잡함
 * 4. response.isSuccessful 체크 필요
 * 
 * 
 * ✅ 현재 코루틴 방식 (간결함, 15줄):
 * 
 * fun fetchUserFromServer(userId: Int) {
 *     viewModelScope.launch {
 *         _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
 * 
 *         try {
 *             val userData = authService.getUserById(userId)
 *             _uiState.value = _uiState.value.copy(isLoading = false)
 * 
 *             if (userData.success) {
 *                 _uiState.value = _uiState.value.copy(
 *                     userId = userData.data.username,
 *                     // ...
 *                 )
 *             }
 *         } catch (e: HttpException) {
 *             // HTTP 에러
 *         } catch (e: IOException) {
 *             // 네트워크 에러
 *         }
 *     }
 * }
 * 
 * 장점:
 * 1. 순차적으로 읽힘 (위에서 아래로)
 * 2. 에러 처리가 한곳에 모임 (try-catch)
 * 3. 중첩이 적음
 * 4. response.isSuccessful 체크 불필요 (자동)
 * 5. 코드가 40% 더 짧음
 * 
 * 
 * ========================================
 * 코루틴 실행 흐름
 * ========================================
 * 
 * 사용자가 "새로고침" 버튼 클릭
 *   ↓
 * fetchUserFromServer(userId) 호출
 *   ↓
 * viewModelScope.launch { } 시작
 *   ↓
 * isLoading = true
 *   ↓ (UI 업데이트: 로딩 표시)
 * 
 * authService.getUserById(userId) 호출
 *   ↓ (코루틴 일시 중단)
 * 
 * [네트워크 통신 중]
 * - 메인 스레드는 블로킹되지 않음
 * - 사용자는 UI와 상호작용 가능
 * - 다른 코루틴도 실행 가능
 *   ↓
 * 
 * 응답 도착 → 코루틴 자동 재개
 *   ↓
 * isLoading = false
 *   ↓
 * 데이터 처리 및 UI 업데이트
 *   ↓
 * 완료!
 */
