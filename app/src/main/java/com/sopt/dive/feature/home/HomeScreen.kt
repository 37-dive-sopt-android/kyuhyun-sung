package com.sopt.dive.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.dive.feature.home.components.profile.BirthdayItemComponent
import com.sopt.dive.feature.home.components.profile.FriendProfileComponent
import com.sopt.dive.feature.home.components.profile.MyProfileComponent


/**
 * Home 탭 화면
 */


@Composable
fun HomeScreen(
    userId: String,
    userNickname: String,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // ViewModel이 데이터 로드하도록
    LaunchedEffect(Unit) {
        viewModel.loadHomeItems(userId, userNickname)
    }

    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = uiState.items,
            key = { item ->
                when (item) {
                    is HomeItem.MyProfile -> "my_profile_${item.userId}"
                    is HomeItem.BirthdayItem -> "birthday_${item.name}"
                    is HomeItem.FriendProfile -> "friend_${item.name}"
                }
            }
        ) { item ->
            when (item) {
                is HomeItem.MyProfile -> {
                    MyProfileComponent(
                        userId = item.userId,
                        userNickname = item.userNickname
                    )
                }
                is HomeItem.BirthdayItem -> {
                    BirthdayItemComponent(name = item.name)
                }
                is HomeItem.FriendProfile -> {
                    FriendProfileComponent(
                        name = item.name,
                        statusMessage = item.statusMessage,
                        hasMusic = item.hasProfileMusic
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "뮤직 있음")
@Composable
private fun FriendProfileWithMusicPreview() {
    FriendProfileComponent(
        name = "ㅇㅇㅇㅇ",
        statusMessage = "간바레",
        hasMusic = true
    )
}