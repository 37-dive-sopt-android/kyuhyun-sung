package com.sopt.dive.feature.card

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.sopt.dive.feature.card.component.CardContent


@Composable
fun FlippingCardScreen(
    onNavigateBack: () -> Unit,  // ✅ NavController 제거
    modifier: Modifier = Modifier
) {
    var isFront by remember { mutableStateOf(true) }

    val targetRotationY = if (isFront) 0f else 180f
    val rotationYAnim by animateFloatAsState(
        targetValue = targetRotationY,
        animationSpec = tween(durationMillis = 800),
        label = "rotationYAnimation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(250.dp)
                .height(350.dp)
                .graphicsLayer {
                    rotationY = rotationYAnim
                    cameraDistance = 8 * density
                }
        ) {
            //  불필요한 Box 제거 + 조건부 Modifier
            CardContent(
                isFront = rotationYAnim <= 90f,
                modifier = if (rotationYAnim > 90f) {
                    Modifier.graphicsLayer { rotationY = 180f }
                } else {
                    Modifier
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { isFront = !isFront },
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
        ) {
            Text(if (isFront) "카드 뒤집기" else "다시 뒤집기")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateBack,  // 콜백 사용
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("뒤로가기")
        }
    }
}

@Preview(showBackground = true, name = "Flipping Card Preview")
@Composable
private fun CardScreenPreview() {
    FlippingCardScreen(
        onNavigateBack = {  }
    )
}