package com.example.plantalarm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SensorCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}

@Composable
fun MiniLineGraph(history: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color.White, shape = RoundedCornerShape(8.dp))) {
        if (history.size < 2) return@Canvas
        val maxVal = history.maxOrNull() ?: 100f
        val minVal = history.minOrNull() ?: 0f
        val deltaY = if (maxVal - minVal == 0f) 1f else maxVal - minVal

        val widthSpace = size.width / (history.size - 1)

        for (i in 0 until history.size - 1) {
            val startX = i * widthSpace
            val startY = size.height - ((history[i] - minVal) / deltaY * size.height)

            val endX = (i + 1) * widthSpace
            val endY = size.height - ((history[i + 1] - minVal) / deltaY * size.height)

            drawLine(
                color = Color(0xFF1976D2),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 4f
            )
        }
    }
}