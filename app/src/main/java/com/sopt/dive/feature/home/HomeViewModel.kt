package com.sopt.dive.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.datasource.ReqresServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * HomeViewModel - 코루틴 기반 API 호출
 * 
 * 이 ViewModel에서 배울 수 있는 코루틴 개념:
 * 1. viewModelScope.launch: 코루틴 시작
 * 2. suspend 함수 호출: 네트워크 통신
 * 3. try-catch: 예외 처리
 * 4. StateFlow: 상태 관리
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val reqresService = ReqresServicePool.reqresService

    /**
     * 홈 화면 데이터 로드
     * 
     * 코루틴 실행 흐름:
     * 1. viewModelScope.launch 시작
     * 2. try 블록 진입
     * 3. reqresService.getUsers() 호출 (일시 중단)
     * 4. 네트워크 통신 중 다른 작업 가능
     * 5. 응답 도착 시 자동 재개
     * 6. 성공/실패 처리
     */
    fun loadHomeItems(userId: String, userNickname: String) {
        /**
         * viewModelScope.launch:
         * - ViewModel의 생명주기에 맞춰 자동 관리
         * - ViewModel이 파괴되면 자동으로 취소됨
         * - 메모리 누수 방지
         */
        viewModelScope.launch {
            try {
                /**
                 * suspend 함수 호출:
                 * - reqresService.getUsers()는 suspend 함수
                 * - 네트워크 통신 중 코루틴이 일시 중단됨
                 * - 메인 스레드는 블로킹되지 않고 UI 반응 가능
                 * - 응답이 오면 자동으로 다음 줄부터 재개
                 */
                val response = reqresService.getUsers(page = 1, perPage = 10)
                
                /**
                 * 여기 도달 = 네트워크 통신 성공
                 * response 객체에 API 응답 데이터가 담겨있음
                 */
                
                // API 응답을 HomeItem.UserProfile로 변환
                val userProfiles = response.data.map { user ->
                    HomeItem.UserProfile(
                        id = user.id,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        avatar = user.avatar
                    )
                }
                
                // 내 프로필 + 유저 프로필 리스트 조합
                val list = buildList<HomeItem> {
                    add(HomeItem.MyProfile(userId = userId, userNickname = userNickname))
                    addAll(userProfiles)
                }
                
                // StateFlow 업데이트 → UI 자동 리컴포지션
                _uiState.value = HomeUiState(items = list)
                Log.d("HomeViewModel", "성공: ${userProfiles.size}명의 사용자 로드")
                
            } catch (e: HttpException) {
                /**
                 * HttpException:
                 * - HTTP 에러 코드 (400, 401, 404, 500 등)
                 * - 서버가 응답은 했지만 에러 코드를 반환
                 * 
                 * 예시:
                 * - 401: 인증 실패
                 * - 404: 리소스 없음
                 * - 500: 서버 에러
                 */
                Log.e("HomeViewModel", "HTTP 에러 ${e.code()}: ${e.message()}")
                loadFallbackData(userId, userNickname)
                
            } catch (e: IOException) {
                /**
                 * IOException:
                 * - 네트워크 연결 문제
                 * - 서버에 도달하지 못함
                 * 
                 * 예시:
                 * - 인터넷 연결 끊김
                 * - 타임아웃
                 * - 서버 다운
                 */
                Log.e("HomeViewModel", "네트워크 에러: ${e.message}")
                loadFallbackData(userId, userNickname)
                
            } catch (e: Exception) {
                /**
                 * Exception:
                 * - 그 외 모든 예외
                 * - 파싱 에러, 예상치 못한 에러 등
                 */
                Log.e("HomeViewModel", "알 수 없는 에러: ${e.message}")
                loadFallbackData(userId, userNickname)
            }
        }
    }

    /**
     * 에러 발생 시 폴백 데이터 로드
     * 
     * 네트워크 에러가 발생해도 기본 데이터를 표시하여
     * 사용자 경험을 유지합니다.
     */
    private fun loadFallbackData(userId: String, userNickname: String) {
        val list = listOf<HomeItem>(
            HomeItem.MyProfile(userId = userId, userNickname = userNickname),
            HomeItem.BirthdayItem(name = "안두콩", message = "오늘 생일입니다!"),
            HomeItem.FriendProfile(name = "성규현", statusMessage = "졸려요", hasProfileMusic = true),
            HomeItem.FriendProfile(name = "엄준식", statusMessage = "SOPT 37기 화이팅!", hasProfileMusic = false),
            HomeItem.FriendProfile(name = "이름생각안나서", statusMessage = "", hasProfileMusic = false),
            HomeItem.FriendProfile(name = "추워요", statusMessage = "여행 중", hasProfileMusic = true)
        )
        _uiState.value = HomeUiState(items = list)
        Log.d("HomeViewModel", "폴백 데이터 로드 완료")
    }
}

/**
 * ========================================
 * 코루틴 vs Callback 비교 (HomeViewModel 예시)
 * ========================================
 * 
 * ❌ Callback 방식 (복잡함, 이전 코드):
 * 
 * authService.getUsers().enqueue(object : Callback<ResponseReqresUsersDto> {
 *     override fun onResponse(call: Call<ResponseReqresUsersDto>, response: Response<ResponseReqresUsersDto>) {
 *         if (response.isSuccessful) {
 *             val data = response.body()
 *             // 데이터 처리
 *         } else {
 *             // 에러 처리
 *         }
 *     }
 *     override fun onFailure(call: Call<ResponseReqresUsersDto>, t: Throwable) {
 *         // 네트워크 에러 처리
 *     }
 * })
 * 
 * ✅ 코루틴 방식 (간결함, 현재 코드):
 * 
 * viewModelScope.launch {
 *     try {
 *         val response = reqresService.getUsers()
 *         // 데이터 처리
 *     } catch (e: HttpException) {
 *         // HTTP 에러 처리
 *     } catch (e: IOException) {
 *         // 네트워크 에러 처리
 *     }
 * }
 * 
 * 장점:
 * 1. 코드가 순차적으로 읽힘 (위에서 아래로)
 * 2. 중첩된 콜백 없음 (콜백 지옥 탈출)
 * 3. 에러 처리가 try-catch로 간단함
 * 4. 자동 취소 (메모리 누수 방지)
 * 5. 테스트하기 쉬움
 */
