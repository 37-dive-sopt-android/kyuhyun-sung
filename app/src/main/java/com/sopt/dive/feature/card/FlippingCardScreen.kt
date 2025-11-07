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


/**
 * 카드의 앞면과 뒷면 내용을 표시하는 컴포저블
 */
@Composable
fun CardContent(isFront: Boolean) {
    val context = LocalContext.current

    //  GIF 로딩 가능한 ImageLoader
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val imageUrl = if (isFront) {
        // GIF로 변경
        "https://github.com/dmp100/dmp100/raw/main/gifs/gif10.gif"
    } else {
        "https://github.com/dmp100/dmp100/raw/main/gifs/gif2.gif"
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFront) Color(0xFF4CAF50) else Color(0xFF2196F3)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ GIF 표시
            AsyncImage(
                model = imageUrl,
                contentDescription = if (isFront) "Front GIF" else "Back GIF",
                imageLoader = imageLoader, // 반드시 추가
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isFront) "Front Card" else "Back Card",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FlippingCardScreen(navController: NavController) {
    var isFront by remember { mutableStateOf(true) }

    val targetRotationY = if (isFront) 0f else 180f

    val rotationYAnim by animateFloatAsState(
        targetValue = targetRotationY,
        animationSpec = tween(durationMillis = 800),
        label = "rotationYAnimation"
    )

    Column(
        modifier = Modifier
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
            if (rotationYAnim <= 90f) {
                CardContent(isFront = true)
            } else {
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    CardContent(isFront = false)
                }
            }
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
            onClick = { navController.popBackStack() },
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
    val navController = rememberNavController()
    FlippingCardScreen(navController = navController)
}
