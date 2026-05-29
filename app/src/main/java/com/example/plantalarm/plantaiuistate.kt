package com.example.plantalarm

import androidx.compose.ui.graphics.Color

enum class StressLevel(val color: Color, val label: String) {
    GREEN(Color(0xFF2E7D32), "Normal (Her Şey Yolunda)"),
    YELLOW(Color(0xFFF57F17), "Dikkat (Hafif Sapma)"),
    RED(Color(0xFFC62828), "Anomali (Yüksek Stres!)")
}

data class SensorValues(
    val nitrogen: Int = 40,
    val phosphorus: Int = 50,
    val potassium: Int = 45,
    val pH: Float = 6.5f,
    val humidity: Float = 65f,
    val temperature: Float = 24f
)

data class PlantAiUiState(
    val stressLevel: StressLevel = StressLevel.GREEN,
    val sensors: SensorValues = SensorValues(),
    val humidityHistory: List<Float> = listOf(60f, 62f, 65f, 63f, 64f, 65f),
    val logs: List<String> = emptyList(),
    val actionMessage: String? = null
)