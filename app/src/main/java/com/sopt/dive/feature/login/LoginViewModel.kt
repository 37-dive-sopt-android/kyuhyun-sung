package com.sopt.dive.feature.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.AuthPreferences
import com.sopt.dive.core.data.datasource.ServicePool
import com.sopt.dive.core.data.dto.RequestLoginDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * LoginViewModel with Flow 기반 입력 검증 + 코루틴 기반 API 호출
 * 
 * Flow의 핵심 개념:
 * 1. MutableStateFlow: 값을 emit(방출)할 수 있는 Flow
 * 2. StateFlow: 값을 읽기만 할 수 있는 Flow (UI에서 collect)
 * 3. map: Flow의 값을 변환 (예: String → Boolean)
 * 4. combine: 여러 Flow를 하나로 합치기
 * 5. debounce: 일정 시간 동안 입력이 없을 때만 처리
 * 
 * 코루틴의 핵심 개념:
 * 1. viewModelScope: ViewModel 생명주기에 맞춰 자동으로 취소되는 코루틴 스코프
 * 2. launch: 새로운 코루틴을 시작 (Job 반환)
 * 3. suspend: 일시 중단 가능한 함수
 * 4. try-catch: 코루틴 내 예외 처리
 */
class LoginViewModel(
    private val context: Context
) : ViewModel() {

    // ========================================
    // 1. 기본 StateFlow 정의
    // ========================================
    
    /**
     * MutableStateFlow: 값을 변경할 수 있는 Flow
     * - emit()이나 value로 새 값을 방출할 수 있음
     * - ViewModel 내부에서만 사용 (private)
     */
    private val _userId = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    
    /**
     * StateFlow: 읽기 전용 Flow
     * - UI(Screen)에서 collectAsState()로 구독
     * - 값이 변경되면 자동으로 UI가 리컴포지션됨
     */
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val authPreferences = AuthPreferences.getInstance(context)
    private val authService = ServicePool.authService

    // ========================================
    // 2. Flow 변환 - map 연산자
    // ========================================
    
    /**
     * map 연산자: Flow의 값을 변환
     * 
     * 작동 방식:
     * _userId Flow에서 값이 방출될 때마다 (예: "abc" 입력)
     *   ↓
     * map { } 블록이 실행됨
     *   ↓
     * 변환된 값을 새로운 Flow로 방출
     * 
     * 예시:
     * _userId = "a" → isIdValidFlow = false (3자 미만)
     * _userId = "abc" → isIdValidFlow = true (3자 이상)
     * _userId = "abcdefghijk" → isIdValidFlow = false (10자 초과)
     */
    private val isIdValidFlow = _userId
        .debounce(300L)  // 300ms 동안 입력이 없을 때까지 대기 (타이핑 중에는 검증 안 함)
        .map { id ->  // map: String → Boolean 변환
            // 검증 로직: 3자 이상 10자 이하
            id.length in 3..10
        }
    
    /**
     * 비밀번호 검증 Flow
     * 
     * debounce(300L):
     * - 사용자가 타이핑을 멈춘 후 300ms 대기
     * - 불필요한 검증을 방지하여 성능 향상
     * - 예: "p" → "pa" → "pas" → "pass" 입력 시
     *   "pass" 입력 후 300ms 대기 후에만 검증 실행
     */
    private val isPasswordValidFlow = _password
        .debounce(300L)
        .map { pw ->
            // 검증 로직: 6자 이상
            pw.length >= 6
        }

    // ========================================
    // 3. Flow 변환 - map으로 에러 메시지 생성
    // ========================================
    
    /**
     * ID 에러 메시지 Flow
     * 
     * map의 활용:
     * - ID 값에 따라 적절한 에러 메시지를 반환
     * - null을 반환하면 에러 없음을 의미
     */
    private val idErrorMessageFlow = _userId
        .debounce(300L)
        .map { id ->
            when {
                id.isEmpty() -> null  // 비어있으면 에러 메시지 없음
                id.length < 3 -> "ID는 3자 이상이어야 합니다"
                id.length > 10 -> "ID는 10자 이하여야 합니다"
                else -> null  // 유효하면 null
            }
        }
    
    /**
     * 비밀번호 에러 메시지 Flow
     */
    private val passwordErrorMessageFlow = _password
        .debounce(300L)
        .map { pw ->
            when {
                pw.isEmpty() -> null
                pw.length < 6 -> "비밀번호는 6자 이상이어야 합니다"
                else -> null
            }
        }

    // ========================================
    // 4. Flow 결합 - combine 연산자
    // ========================================
    
    /**
     * combine 연산자: 여러 Flow를 하나로 합치기
     * 
     * 작동 방식:
     * Flow1 ─┐
     *        ├─→ combine { a, b, c → 결과 } ─→ 새로운 Flow
     * Flow2 ─┤
     * Flow3 ─┘
     * 
     * 특징:
     * - 모든 Flow가 최소 한 번씩 값을 방출해야 combine이 실행됨
     * - 어느 하나의 Flow가 새 값을 방출하면 combine이 다시 실행됨
     * 
     * 실행 예시:
     * 1. _userId = "abc" 입력
     *    → isIdValidFlow = true
     *    → (아직 password가 없으므로 combine 실행 안 됨)
     * 
     * 2. _password = "pass123" 입력
     *    → isPasswordValidFlow = true
     *    → combine 실행: true && true && true → isLoginEnabled = true
     * 
     * 3. _userId = "ab" 입력 (너무 짧음)
     *    → isIdValidFlow = false
     *    → combine 실행: false && true && true → isLoginEnabled = false
     */
    init {
        // ViewModel이 생성될 때 combine Flow 설정
        viewModelScope.launch {
            // 5개의 Flow를 combine으로 결합
            combine(
                _userId,                    // Flow 1: ID 입력값
                _password,                  // Flow 2: PW 입력값
                isIdValidFlow,              // Flow 3: ID 검증 결과
                isPasswordValidFlow,        // Flow 4: PW 검증 결과
                idErrorMessageFlow          // Flow 5: ID 에러 메시지
            ) { id, pw, isIdValid, isPwValid, idError ->
                // combine 블록: 5개의 값을 받아서 새로운 UiState 생성
                
                // 로그인 버튼 활성화 조건:
                // 1. ID가 비어있지 않음
                // 2. PW가 비어있지 않음
                // 3. ID가 유효함
                // 4. PW가 유효함
                val isButtonEnabled = id.isNotEmpty() && 
                                    pw.isNotEmpty() && 
                                    isIdValid && 
                                    isPwValid
                
                // 새로운 UiState 생성 (불변성 유지)
                LoginUiState(
                    userId = id,
                    password = pw,
                    isIdValid = isIdValid,
                    isPasswordValid = isPwValid,
                    isLoginButtonEnabled = isButtonEnabled,
                    idErrorMessage = idError,
                    passwordErrorMessage = null  // 비밀번호 에러는 별도로 처리 가능
                )
            }.collect { newState ->
                // collect: Flow에서 방출된 값을 소비(사용)
                // 새로운 UiState를 _uiState에 방출
                _uiState.value = newState
            }
        }
    }

    // ========================================
    // 5. 입력값 업데이트 함수
    // ========================================
    
    /**
     * ID 입력값 업데이트
     * 
     * Flow의 동작:
     * 1. updateUserId("a") 호출
     *    ↓
     * 2. _userId.value = "a" (새 값 방출)
     *    ↓
     * 3. _userId를 구독하는 모든 Flow가 반응
     *    - isIdValidFlow의 debounce가 300ms 대기 시작
     *    - combine의 _userId 파라미터가 "a"로 업데이트
     *    ↓
     * 4. 300ms 후 debounce 통과
     *    ↓
     * 5. map { id -> id.length in 3..10 } 실행
     *    ↓
     * 6. combine이 다시 실행되어 새로운 UiState 생성
     *    ↓
     * 7. UI가 자동으로 리컴포지션
     */
    fun updateUserId(id: String) {
        _userId.value = id
    }

    /**
     * 비밀번호 입력값 업데이트
     * 
     * 동작은 updateUserId와 동일
     */
    fun updatePassword(pw: String) {
        _password.value = pw
    }

    // ========================================
    // 6. 코루틴 기반 API 로그인
    // ========================================
    
    /**
     * 코루틴 기반 API 로그인 함수
     * 
     * 코루틴의 핵심 개념:
     * 
     * 1. viewModelScope:
     *    - ViewModel의 생명주기에 맞춰 자동으로 관리되는 코루틴 스코프
     *    - ViewModel이 onCleared()될 때 자동으로 모든 코루틴 취소
     *    - 메모리 누수 방지
     * 
     * 2. launch:
     *    - 새로운 코루틴을 시작하는 빌더 함수
     *    - Job을 반환 (필요시 수동으로 취소 가능)
     *    - 블록 내부에서 suspend 함수 호출 가능
     * 
     * 3. suspend 함수:
     *    - authService.login()은 suspend 함수
     *    - 네트워크 호출 중 스레드를 블로킹하지 않고 일시 중단
     *    - 응답이 오면 자동으로 재개됨
     * 
     * 4. try-catch:
     *    - suspend 함수에서 발생한 예외를 잡음
     *    - HttpException: HTTP 에러 (401, 404 등)
     *    - IOException: 네트워크 연결 에러
     */
    fun loginWithApi(onSuccess: (String, String) -> Unit, onFailure: (String) -> Unit) {
        // 현재 입력된 ID, PW 가져오기
        val id = _userId.value
        val pw = _password.value

        // 입력값 검증 (combine으로 이미 검증되었지만 한번 더 체크)
        if (id.isBlank() || pw.isBlank()) {
            onFailure("ID와 비밀번호를 입력해주세요.")
            return
        }

        /**
         * viewModelScope.launch { }:
         * - 새로운 코루틴 시작
         * - 이 블록 내부에서 suspend 함수 호출 가능
         * - ViewModel이 파괴되면 자동으로 취소됨
         * 
         * 실행 흐름:
         * 1. launch { } 블록 시작
         * 2. 로딩 상태 true
         * 3. authService.login() 호출 (일시 중단)
         * 4. 네트워크 통신 중 다른 작업 가능 (UI 반응 가능)
         * 5. 응답 도착 시 자동 재개
         * 6. 성공/실패 처리
         * 7. 로딩 상태 false
         */
        viewModelScope.launch {
            // 로딩 시작 (기존 uiState 유지하면서 isLoading만 업데이트)
            _uiState.value = _uiState.value.copy(isLoading = true)

            /**
             * try-catch 블록:
             * - suspend 함수에서 발생하는 예외를 처리
             * - Callback 방식의 onResponse/onFailure를 대체
             * 
             * Callback 방식 (복잡함, 15줄):
             * authService.login(request).enqueue(object : Callback<ResponseLoginDto> {
             *     override fun onResponse(...) { }
             *     override fun onFailure(...) { }
             * })
             * 
             * 코루틴 방식 (간결함, 5줄):
             * try {
             *     val response = authService.login(request)
             * } catch (e: Exception) { }
             */
            try {
                // API 요청 객체 생성
                val request = RequestLoginDto(username = id, password = pw)
                
                /**
                 * authService.login(request):
                 * - suspend 함수 호출
                 * - 네트워크 통신 중 코루틴이 일시 중단됨
                 * - 메인 스레드는 블로킹되지 않고 다른 작업 가능
                 * - 응답이 오면 다음 줄부터 자동으로 재개
                 * 
                 * 일시 중단의 의미:
                 * - 코루틴이 잠깐 멈추고 스레드를 양보
                 * - 다른 코루틴이나 UI 작업이 실행될 수 있음
                 * - 네트워크 응답이 오면 자동으로 재개됨
                 */
                val response = authService.login(request)
                
                // 여기 도달 = 네트워크 통신 성공
                
                // 로딩 종료
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                // API 응답 처리
                if (response.success) {
                    // 로그인 성공
                    val realUserId = response.data.userId
                    Log.d("Login", "실제 userId: $realUserId")

                    // SharedPreferences에 로그인 정보 저장
                    authPreferences.saveLoginInfo(
                        userId = realUserId,
                        username = id,
                        password = pw
                    )

                    // UiState 업데이트
                    _uiState.value = _uiState.value.copy(
                        loginSuccess = true,
                        errorMessage = null
                    )

                    // 성공 콜백 호출
                    onSuccess(realUserId.toString(), pw)
                } else {
                    // API에서 success = false 응답
                    val errorMsg = response.message ?: "로그인 실패"
                    _uiState.value = _uiState.value.copy(
                        loginSuccess = false,
                        errorMessage = errorMsg
                    )
                    onFailure(errorMsg)
                }
                
            } catch (e: HttpException) {
                /**
                 * HttpException:
                 * - HTTP 에러 코드가 있는 경우 (400, 401, 404, 500 등)
                 * - 서버가 응답은 했지만 에러 코드를 반환한 경우
                 * 
                 * 예시:
                 * - 401: 인증 실패 (비밀번호 틀림)
                 * - 403: 권한 없음 (계정 비활성화)
                 * - 404: 사용자 없음
                 * - 500: 서버 에러
                 */
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                val errorMsg = when (e.code()) {
                    401 -> "아이디 또는 비밀번호가 틀렸습니다"
                    403 -> "비활성화된 사용자입니다"
                    404 -> "존재하지 않는 사용자입니다"
                    500 -> "서버 오류가 발생했습니다"
                    else -> "로그인 실패: ${e.message()}"
                }
                
                _uiState.value = _uiState.value.copy(
                    loginSuccess = false,
                    errorMessage = errorMsg
                )
                onFailure(errorMsg)
                
                Log.e("Login", "HTTP 에러: ${e.code()} - ${e.message()}")
                
            } catch (e: IOException) {
                /**
                 * IOException:
                 * - 네트워크 연결 문제 (인터넷 끊김, 타임아웃 등)
                 * - 서버에 도달하지 못한 경우
                 * 
                 * 예시:
                 * - 와이파이/데이터 꺼짐
                 * - 서버 다운
                 * - 타임아웃
                 */
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                val errorMsg = "네트워크 연결을 확인해주세요"
                _uiState.value = _uiState.value.copy(
                    loginSuccess = false,
                    errorMessage = errorMsg
                )
                onFailure(errorMsg)
                
                Log.e("Login", "네트워크 오류: ${e.message}")
                
            } catch (e: Exception) {
                /**
                 * Exception:
                 * - 그 외 모든 예외 처리
                 * - 파싱 에러, 예상치 못한 에러 등
                 */
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                val errorMsg = "알 수 없는 오류: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    loginSuccess = false,
                    errorMessage = errorMsg
                )
                onFailure(errorMsg)
                
                Log.e("Login", "예외 발생: ${e.message}")
            }
        }
    }
}

/**
 * ========================================
 * 코루틴 개념 정리
 * ========================================
 * 
 * 1. viewModelScope vs GlobalScope
 *    - viewModelScope: ViewModel에 종속, 자동 취소 (권장)
 *    - GlobalScope: 앱 전체에 종속, 수동 취소 필요 (비권장)
 * 
 * 2. launch vs async
 *    - launch: 결과를 반환하지 않음, Job 반환
 *    - async: 결과를 반환, Deferred<T> 반환
 * 
 * 3. suspend 함수
 *    - 일시 중단 가능한 함수
 *    - 코루틴 내부나 다른 suspend 함수에서만 호출 가능
 *    - 스레드를 블로킹하지 않음
 * 
 * 4. 예외 처리
 *    - try-catch로 간단히 처리
 *    - HttpException: HTTP 에러
 *    - IOException: 네트워크 에러
 *    - Exception: 기타 에러
 * 
 * 5. Callback vs 코루틴 비교
 * 
 *    ❌ Callback (복잡함):
 *    authService.login(request).enqueue(object : Callback<ResponseLoginDto> {
 *        override fun onResponse(call: Call<ResponseLoginDto>, response: Response<ResponseLoginDto>) {
 *            if (response.isSuccessful) {
 *                val data = response.body()
 *                // 성공 처리
 *            } else {
 *                // 에러 처리
 *            }
 *        }
 *        override fun onFailure(call: Call<ResponseLoginDto>, t: Throwable) {
 *            // 네트워크 에러 처리
 *        }
 *    })
 * 
 *    ✅ 코루틴 (간결함):
 *    viewModelScope.launch {
 *        try {
 *            val response = authService.login(request)
 *            // 성공 처리
 *        } catch (e: HttpException) {
 *            // HTTP 에러 처리
 *        } catch (e: IOException) {
 *            // 네트워크 에러 처리
 *        }
 *    }
 * 
 * 6. 실행 흐름 예시
 *    버튼 클릭
 *      ↓
 *    loginWithApi() 호출
 *      ↓
 *    viewModelScope.launch { } 시작
 *      ↓
 *    isLoading = true (UI 업데이트)
 *      ↓
 *    authService.login() 호출 (일시 중단)
 *      ↓
 *    [네트워크 통신 중 - 다른 작업 가능]
 *      ↓
 *    응답 도착 → 코루틴 재개
 *      ↓
 *    try 블록: 성공 처리
 *    or
 *    catch 블록: 에러 처리
 *      ↓
 *    isLoading = false (UI 업데이트)
 *      ↓
 *    onSuccess/onFailure 콜백 호출
 */
