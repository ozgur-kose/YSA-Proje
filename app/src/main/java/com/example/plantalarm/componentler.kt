package com.example.plantalarm

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernLeafIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 4.dp, bottomStart = 4.dp)
            )
    )
}

@Composable
fun AnimatedCircularGauge(value: Float, maxValue: Float, title: String, unit: String, activeColor: Color) {
    val animatedValue by animateFloatAsState(
        targetValue = (value / maxValue).coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = "gaugeAnimation"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
            CircularProgressIndicator(
                progress = { 1f }, color = Color.LightGray.copy(alpha = 0.3f), strokeWidth = 8.dp, modifier = Modifier.fillMaxSize()
            )
            CircularProgressIndicator(
                progress = { animatedValue }, color = activeColor, strokeWidth = 8.dp, modifier = Modifier.fillMaxSize()
            )
            Text(text = "${value.toInt()}$unit", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2C3E50))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MiniSensorCard(title: String, value: String, iconColor: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(iconColor, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
            }
        }
    }
}

@Composable
fun AnimatedNPKBar(label: String, value: Int, maxValue: Int, color: Color) {
    val animatedProgress by animateFloatAsState(targetValue = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f), animationSpec = tween(1000), label = "npkAnimation")
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(24.dp))
        Box(modifier = Modifier.weight(1f).height(10.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(50))) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(animatedProgress).background(color, RoundedCornerShape(50)))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = value.toString(), fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(30.dp))
    }
}


@Composable
fun ModernSmartLineGraph(history: List<Float>, lineColor: Color, gradientColor: Color, modifier: Modifier = Modifier) {

    Canvas(modifier = modifier.background(Color.White, RoundedCornerShape(16.dp)).padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)) {
        if (history.size < 2) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height

        val dataMin = history.minOrNull() ?: 0f
        val dataMax = history.maxOrNull() ?: 100f

        val diff = dataMax - dataMin
        val buffer = if (diff < 2f) 5f else diff * 0.3f

        val graphMin = (dataMin - buffer).coerceAtLeast(0f)
        val graphMax = dataMax + buffer
        val range = if (graphMax - graphMin == 0f) 1f else (graphMax - graphMin)

        val xStep = canvasWidth / (history.size - 1).coerceAtLeast(1)

        val path = Path()
        val fillPath = Path()


        val textPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#424242")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        history.forEachIndexed { index, value ->
            val x = index * xStep
            val scaledY = canvasHeight - (((value - graphMin) / range) * canvasHeight)

            if (index == 0) {
                path.moveTo(x, scaledY)
                fillPath.moveTo(x, canvasHeight)
                fillPath.lineTo(x, scaledY)
            } else {
                path.lineTo(x, scaledY)
                fillPath.lineTo(x, scaledY)
            }

            if (index == history.size - 1) {
                fillPath.lineTo(x, canvasHeight)
                fillPath.close()
            }


            val textX = when (index) {
                0 -> x + 15f
                history.size - 1 -> x - 15f
                else -> x
            }


            drawContext.canvas.nativeCanvas.drawText(
                String.format(java.util.Locale.US, "%.1f", value),
                textX,
                scaledY - 25f,
                textPaint
            )
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(gradientColor.copy(alpha = 0.7f), gradientColor.copy(alpha = 0.05f)),
                startY = 0f,
                endY = canvasHeight
            )
        )

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 6f)
        )
    }
}