package com.sopt.dive.feature.signup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 회원가입 화면에서 사용하는 커스텀 텍스트 필드 컴포저블
 *
 * BasicTextField를 감싸서 일관된 디자인을 적용합니다.
 *
 * @param label 입력 필드 위에 표시될 레이블
 * @param value 현재 입력된 텍스트
 * @param onValueChange 텍스트가 변경될 때 호출되는 콜백
 * @param placeholder 입력 필드가 비어있을 때 표시될 힌트 텍스트
 * @param isPassword 비밀번호 필드인 경우 true (마스킹 처리됨)
 */
@Composable
fun SignUpTextFieldComponent(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 20.sp)

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 18.sp,
                            color = Color.Gray,
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}