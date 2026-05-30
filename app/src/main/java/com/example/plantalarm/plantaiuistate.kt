package com.example.plantalarm

import androidx.compose.ui.graphics.Color

enum class StressLevel(val color: Color, val label: String) {
    GREEN(Color(0xFF2E7D32), "Normal (Her Şey Yolunda)"),
    YELLOW(Color(0xFFF57F17), "Dikkat (Hafif Sapma)"),
    RED(Color(0xFFC62828), "Anomali (Yüksek Stres!)")
}

data class SensorValues(
    val temperature: Float = 24f,
    val humidity: Float = 65f,
    val moisture: Float = 55f,
    val pH: Float = 6.5f,
    val nitrogen: Int = 40,
    val phosphorus: Int = 50,
    val potassium: Int = 45
)

data class PlantAiUiState(
    val stressLevel: StressLevel = StressLevel.GREEN,
    val sensors: SensorValues = SensorValues(),
    val logs: List<String> = emptyList(),
    val actionMessage: String? = null,
    val f1Score: String = "%88.5",
    val rocAuc: String = "0.912",
    val modelGuvenSkoru: String = "% 0.0"
)