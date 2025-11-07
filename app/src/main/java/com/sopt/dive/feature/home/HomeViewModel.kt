package com.sopt.dive.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadHomeItems(userId: String, userNickname: String) {
        viewModelScope.launch {
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
}
