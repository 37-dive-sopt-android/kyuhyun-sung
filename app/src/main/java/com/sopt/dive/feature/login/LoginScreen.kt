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
import androidx.compose.runtime.remember
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
import com.sopt.dive.core.ui.noRippleClickable

/**
 * 로그인 화면 - Flow 기반 실시간 검증
 * 
 * Flow 동작 확인:
 * 1. TextField에 입력 → updateUserId/updatePassword 호출
 * 2. ViewModel의 Flow가 자동으로 검증
 * 3. uiState.collectAsState()로 상태 구독
 * 4. 상태가 변경되면 자동 리컴포지션
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
    
    /**
     * collectAsState(): StateFlow를 Compose State로 변환
     * 
     * 작동 방식:
     * 1. viewModel.uiState는 StateFlow<LoginUiState>
     * 2. collectAsState()가 이를 State<LoginUiState>로 변환
     * 3. uiState 값이 변경되면 자동으로 리컴포지션
     * 
     * 예시:
     * - 사용자가 "abc" 입력
     * - ViewModel의 combine Flow가 새 UiState 생성
     * - collectAsState()가 변경을 감지
     * - 이 Composable이 자동으로 리컴포지션됨
     */
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

        // ========================================
        // ID 입력 필드
        // ========================================
        Text(text = "ID", fontSize = 20.sp)

        val brush = remember {
            Brush.linearGradient(
                colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
            )
        }

        /**
         * ID TextField - Flow와 연동
         * 
         * 동작 순서:
         * 1. value = uiState.userId
         *    - StateFlow에서 현재 값을 읽어와 표시
         * 
         * 2. onValueChange = { viewModel.updateUserId(it) }
         *    - 사용자가 입력할 때마다 호출됨
         *    - ViewModel의 _userId Flow에 새 값 방출
         *    - Flow 체인이 자동으로 실행됨 (debounce → map → combine)
         * 
         * 3. isError = uiState.idErrorMessage != null
         *    - Flow에서 계산된 에러 메시지가 있으면 에러 상태 표시
         * 
         * 4. supportingText
         *    - 에러 메시지를 TextField 아래에 표시
         */
        OutlinedTextField(
            value = uiState.userId,  // Flow에서 오는 값
            onValueChange = { viewModel.updateUserId(it) },  // Flow로 값 전달
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(brush = brush),
            label = { Text("아이디를 입력하세요") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
            singleLine = true,
            // Flow로 계산된 에러 상태 적용
            isError = uiState.idErrorMessage != null,
            supportingText = {
                // 에러 메시지가 있으면 빨간색으로 표시
                uiState.idErrorMessage?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        // ========================================
        // 비밀번호 입력 필드
        // ========================================
        Text(text = "PW", fontSize = 20.sp)

        /**
         * 비밀번호 TextField - Flow와 연동
         * 
         * ID TextField와 동일한 방식으로 작동
         * 추가로 Go 액션으로 로그인 시도 가능
         */
        OutlinedTextField(
            value = uiState.password,  // Flow에서 오는 값
            onValueChange = { viewModel.updatePassword(it) },  // Flow로 값 전달
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
                    // Go 액션: 버튼이 활성화되어 있을 때만 로그인 시도
                    if (uiState.isLoginButtonEnabled) {
                        viewModel.loginWithApi(
                            onSuccess = { id, pw -> onLoginClick(id, pw) },
                            onFailure = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            ),
            // Flow로 계산된 에러 상태 적용
            isError = uiState.passwordErrorMessage != null,
            supportingText = {
                uiState.passwordErrorMessage?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // ========================================
        // 로그인 버튼 - Flow로 활성화 제어
        // ========================================
        
        /**
         * enabled 속성이 Flow로 제어됨
         * 
         * 버튼 활성화 조건 (ViewModel의 combine에서 계산):
         * 1. ID가 비어있지 않음
         * 2. PW가 비어있지 않음
         * 3. ID가 유효함 (3~10자)
         * 4. PW가 유효함 (6자 이상)
         * 5. 로딩 중이 아님
         * 
         * 실시간 동작:
         * - 사용자가 "ab" 입력 → 버튼 비활성화 (ID 너무 짧음)
         * - "abc" 입력 → 버튼 여전히 비활성화 (PW 없음)
         * - PW "pass123" 입력 → 버튼 활성화!
         * - ID를 "ab"로 수정 → 버튼 즉시 비활성화
         */
        Button(
            onClick = {
                viewModel.loginWithApi(
                    onSuccess = { id, pw -> onLoginClick(id, pw) },
                    onFailure = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            // Flow에서 계산된 버튼 활성화 상태 + 로딩 상태
            enabled = uiState.isLoginButtonEnabled && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                disabledContainerColor = Color.Gray  // 비활성화 시 회색
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (uiState.isLoading) "로그인 중..." else "로그인",
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

/**
 * ========================================
 * Flow 기반 UI 업데이트 흐름 정리
 * ========================================
 * 
 * 사용자 액션:
 *   TextField에 "a" 입력
 *     ↓
 * ViewModel:
 *   1. updateUserId("a") 호출
 *   2. _userId.value = "a" (Flow에 값 방출)
 *   3. debounce 300ms 대기
 *   4. map { "a".length in 3..10 } → false
 *   5. combine이 새 UiState 생성:
 *      - userId = "a"
 *      - isIdValid = false
 *      - isLoginButtonEnabled = false
 *      - idErrorMessage = "ID는 3자 이상이어야 합니다"
 *   6. _uiState.value = 새로운 UiState
 *     ↓
 * UI (Screen):
 *   1. collectAsState()가 변경 감지
 *   2. 리컴포지션 발생
 *   3. TextField에 에러 메시지 표시
 *   4. 로그인 버튼 비활성화
 * 
 * 이 모든 과정이 자동으로 실행됨!
 * Flow가 알아서 값을 전파하고 UI를 업데이트함
 */
