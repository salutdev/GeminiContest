package com.example.runpython.pages.composibles

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.example.runpython.shared.Constants

@Composable
fun FormattedLevel(level: String, modifier: Modifier = Modifier) {

    val color = when (level.toLowerCase(Locale.current)) {
        Constants.easy -> Color(0xFF00AF9B)
        Constants.medium -> Color(0xFFFFA500)
        Constants.hard -> Color.Red
        else -> Color.Black
    }

    Text(
        level,
        modifier = modifier,
        color = color
    )
}