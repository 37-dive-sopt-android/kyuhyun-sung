package com.sopt.dive.feature.home

/**
 * HomeScreen의 LazyColumn에 들어갈 모든 아이템 타입을 정의하는 sealed interface
 * Type-Safe하게 다양한 UI를 하나의 리스트로 표현할 수 있게 합니다.
 * 이 파일안에서만 정의하여 여 공통타입 역할을 담당합니다.
 */
sealed interface HomeItem {

    // 1. 유저 본인의 프로필 (가장 상단에 하나만 나옴)
    data class MyProfile(
        val userId: String,
        val userNickname: String
    ) : HomeItem // <-HomeItem 인터페이스를 구현하는 클래스 : MyProfile("user123", "규현") 형태 인스턴스 생성


    // 2. 생일 알림/카드 (예: "오늘 생일인 친구")
    data class BirthdayItem(
        val name: String,
        val message: String
    ) : HomeItem

    // 3. 프로필 뮤직 설정 유무에 따른 친구 프로필
    data class FriendProfile(
        val name: String,
        val statusMessage: String,
        val hasProfileMusic: Boolean
    ) : HomeItem

}