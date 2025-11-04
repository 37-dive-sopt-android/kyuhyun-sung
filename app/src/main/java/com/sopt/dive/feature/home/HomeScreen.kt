package com.sopt.dive.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.dive.feature.home.components.BirthdayItemComponent
import com.sopt.dive.feature.home.components.FriendProfileComponent
import com.sopt.dive.feature.home.components.MyProfileComponent


/**
 * Home 탭 화면
 */


@Composable
fun HomeScreen(
    userId: String,
    userNickname: String,
    modifier: Modifier = Modifier
) {
    // 기존 dummyList를 HomeItem 리스트로 변경
    val homeItemList = remember(userId, userNickname) {

        /*
            remember의 역할:
            Compose는 화면이 업데이트될 때마다 함수를 다시 호출합니다(리컴포지션).
            remember는 "이 값을 기억해둬서 리컴포지션 때 다시 계산하지 마"라고 말하는 것입니다.

            remember(userId, userNickname)에서 괄호 안의 값들은 "이 값들이 변경되면 다시 계산해"
         */

        listOf<HomeItem>(
            // listOf<HomeItem>이라고 명시한 이유는 이 리스트가 HomeItem 타입을 담는다는 것을 명확히 하기 위함
            // 1. 내 프로필
            HomeItem.MyProfile(
                userId = userId,
                userNickname = userNickname
            ),

            // 2. 생일
            HomeItem.BirthdayItem(
                name = "안두콩",
                message = "오늘 생일입니다!"
            ),

            // 3. 프로필 뮤직 O
            HomeItem.FriendProfile(
                name = "성규현",
                statusMessage = "졸려요",
                hasProfileMusic = true
            ),
            // 4. 프로필 뮤직이 X
            HomeItem.FriendProfile(
                name = "엄준식",
                statusMessage = "SOPT 37기 화이팅!",
                hasProfileMusic = false
            ),

            // 5. 상태 메시지가 X
            HomeItem.FriendProfile(
                name = "이름생각안나서",
                statusMessage = "",
                hasProfileMusic = false
            ),
            HomeItem.FriendProfile(
                name = "추워요",
                statusMessage = "여행 중",
                hasProfileMusic = true
            ),
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // items() 함수에 key 파라미터 추가
        items(
            items = homeItemList,
            key = { item ->
                // 각 아이템을 고유하게 식별하는 key함수 생성
                when (item) { // item의 실제 타입을 확인
                    is HomeItem.MyProfile -> "my_profile_${item.userId}"
                    is HomeItem.BirthdayItem -> "birthday_${item.name}"
                    is HomeItem.FriendProfile -> "friend_${item.name}"
                }
            }
        ) { item ->
            // when 표현식으로 sealed interface의 각 타입 처리
            // 각 타입에 맞는 컴포넌트를 호출합니다 (값,또는 타입 체크)
            when (item) {
                is HomeItem.MyProfile -> {
                    MyProfileComponent(
                        userId = item.userId,
                        userNickname = item.userNickname
                    )
                }

                is HomeItem.BirthdayItem -> {
                    BirthdayItemComponent(
                        name = item.name
                    )
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