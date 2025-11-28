package com.sopt.dive.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sopt.dive.core.data.datasource.ReqresServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val reqresService = ReqresServicePool.reqresService

    fun loadHomeItems(userId: String, userNickname: String) {
        viewModelScope.launch {
            try {
                // Reqres API 호출
                val response = reqresService.getUsers(page = 1, perPage = 10)
                
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
                
                _uiState.value = HomeUiState(items = list)
                Log.d("HomeViewModel", "Successfully loaded ${userProfiles.size} users")
                
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load users", e)
                // 에러 발생 시 기존 더미 데이터 사용
                loadFallbackData(userId, userNickname)
            }
        }
    }

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
    }
}
