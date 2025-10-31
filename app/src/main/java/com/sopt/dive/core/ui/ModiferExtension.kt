package com.sopt.dive.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * 리플 효과(Ripple Effect)를 제거한 클릭 가능한 Modifier 확장 함수
 *
 * Material Design의 기본 리플 효과를 제거하고 싶을 때 사용합니다.
 * 텍스트 버튼이나 링크처럼 시각적 피드백이 불필요한 요소에 적합합니다.
 *
 * @param onClick 클릭 시 실행될 람다 함수
 * @return 클릭 가능하지만 리플 효과가 없는 Modifier
 */
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },  // 상호작용 추적
        indication = null,  // indication을 null로 설정하여 리플 효과 제거
        onClick = onClick
    )
}
