package com.sopt.dive.feature.card

import androidx.compose.ui.graphics.Color

internal data class CardData(  // internal로 변경
    val imageUrl: String,
    val backgroundColor: Color,
    val text: String
)