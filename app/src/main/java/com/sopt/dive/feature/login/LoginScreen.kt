package com.sopt.dive.feature.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sopt.dive.core.data.UserPreferences
import com.sopt.dive.core.ui.noRippleClickable

/**
 * 로그인 화면
 *
 * 사용자로부터 ID와 비밀번호를 입력받아 SharedPreferences에 저장된 정보와 비교
 * 일치하면 로그인 성공, 일치하지 않으면 에러 메시지를 표시
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = LoginViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Welcome To Sopt",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(text = "ID", fontSize = 20.sp)

        val brush = remember {
            Brush.linearGradient(
                colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
            )
        }

        OutlinedTextField(
            value = uiState.userId,
            onValueChange = { viewModel.updateUserId(it) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(brush = brush),
            label = { Text("아이디를 입력하세요") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "PW", fontSize = 20.sp)

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = TextStyle(brush = brush),
            label = { Text("비밀번호를 입력해주세요.") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
                onGo = {
                    viewModel.validateLogin(
                        onSuccess = { id, pw -> onLoginClick(id, pw) },
                        onFailure = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.loginWithApi(  // validateLogin → loginWithApi
                    onSuccess = { id, pw -> onLoginClick(id, pw) },
                    onFailure = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            enabled = !uiState.isLoading,  // 로딩 중 버튼 비활성화
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (uiState.isLoading) "로그인 중..." else "로그인",  // 로딩 표시
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .noRippleClickable(onSignUpClick)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("회원가입", color = Color.Black, textDecoration = TextDecoration.Underline)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
