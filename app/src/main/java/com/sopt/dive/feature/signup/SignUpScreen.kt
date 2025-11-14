package com.sopt.dive.feature.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sopt.dive.feature.signup.components.SignUpTextFieldComponent
import com.sopt.dive.feature.signup.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: (SignUpData) -> Unit,
    viewModel: SignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    val idText = viewModel.id.value
    val pwText = viewModel.password.value
    val nicknameText = viewModel.nickname.value
    val emailText = viewModel.email.value  //  이메일 추가
    val ageText = viewModel.age.value      //  나이 추가

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "SIGN UP",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        SignUpTextFieldComponent(
            label = "ID",
            value = idText,
            onValueChange = { viewModel.onIdChange(it) },
            placeholder = "아이디를 입력해 주세요."
        )

        Spacer(modifier = Modifier.height(24.dp))

        SignUpTextFieldComponent(
            label = "PW",
            value = pwText,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholder = "비밀번호를 입력해주세요.",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        SignUpTextFieldComponent(
            label = "NAME",
            value = nicknameText,
            onValueChange = { viewModel.onNicknameChange(it) },
            placeholder = "이름을 입력해주세요."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 이메일 필드 추가
        SignUpTextFieldComponent(
            label = "EMAIL",
            value = emailText,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "이메일을 입력해주세요."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 나이 필드 추가
        SignUpTextFieldComponent(
            label = "AGE",
            value = ageText,
            onValueChange = { viewModel.onAgeChange(it) },
            placeholder = "나이를 입력해주세요."
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val errorMessage = viewModel.validateInput()
                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.signUpWithApi(
                        onSuccess = {
                            Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                            onSignUpClick(viewModel.getSignUpData())
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            enabled = !viewModel.isLoading.value
        ) {
            Text(
                text = if (viewModel.isLoading.value) "처리 중..." else "회원가입",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "SignUp Screen")
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(onSignUpClick = { _ -> })
}