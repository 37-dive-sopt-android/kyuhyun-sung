package com.sopt.dive.feature.card.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.sopt.dive.feature.card.CardData

/**
 * 카드의 앞면과 뒷면 내용을 표시하는 컴포저블
 */
// component/CardContent.kt
@Composable
internal fun CardContent(  // private → internal
    isFront: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // CardData 활용
    val cardData = remember(isFront) {
        if (isFront) {
            CardData(
                imageUrl = "https://github.com/dmp100/dmp100/raw/main/gifs/gif10.gif",
                backgroundColor = Color(0xFF4CAF50),
                text = "Front Card"
            )
        } else {
            CardData(
                imageUrl = "https://github.com/dmp100/dmp100/raw/main/gifs/gif2.gif",
                backgroundColor = Color(0xFF2196F3),
                text = "Back Card"
            )
        }
    }

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    // Card 제거하고 Column에 직접 스타일 적용
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(cardData.backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = cardData.imageUrl,
            contentDescription = null,
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = cardData.text,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}