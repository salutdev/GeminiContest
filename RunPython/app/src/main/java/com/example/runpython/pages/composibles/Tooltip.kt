package com.example.runpython.pages.composibles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup

@Composable
fun Tooltip(
    visible: Boolean,
    message: String,
    anchorModifier: Modifier = Modifier
) {
    if (visible) {
        Popup(alignment = Alignment.TopCenter) {
            Box(
                modifier = anchorModifier
                    .background(Color.Black)
                    .padding(8.dp)
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}