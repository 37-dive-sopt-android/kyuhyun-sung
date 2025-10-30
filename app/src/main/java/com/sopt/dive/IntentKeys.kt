package com.sopt.dive

// 공통 object 파일 생성
object IntentKeys {
    // 앱 전체에서 하나의 인스턴스만 존재하는 객체를 만들기.
    const val USER_ID = "userId"
    // const val은 컴파일 타임에 값이 결정되는 상수
    const val USER_PW = "userPw"
    const val USER_NICKNAME = "userNickname"
    const val USER_EXTRA = "userExtra"
}

// 왜 성능이 좋고 안전한가 ?
/*
   런타임에 객체에 접근하거나 값을 가져오는 추가적인 작업이 전혀 필요없기 때문이라곤함
   const 없이 val USER_ID = "userId"로 하면 앱이 실행할 때마다 IntentKeys 객체에서 USER_ID 값을 읽어와야 함
   -> 메모리 사용량 증가

   근데 솔직히 요즘 기기들 다 좋아서 슈퍼앱 아니면 이렇게까지 안해도 괜찮지않나,,, 생각해봅니다.

   안정성관련
    LoginActivity에서는 "userId"라고 쓰고,
    SignUpActivity에서는 실수로 "user_id"라고 쓴다면 어떻게 될까요?
    앱은 정상적으로 컴파일되지만, 실행하면 데이터가 제대로 전달되지 않습니다. 이런 버그는 찾기가 매우 어렵습니다.

    라고 하네요 (클로드 피셜)
 */


// https://kotlinlang.org/docs/object-declarations.html#companion-objects - 공식문서 읽기는 아직은 어렵나봅니다..
// https://medium.com/depayse/kotlin-%ED%81%B4%EB%9E%98%EC%8A%A4-10-object-%ED%82%A4%EC%9B%8C%EB%93%9C%EC%9D%98-%EC%82%AC%EC%9A%A9-d7fe736a3dcb
