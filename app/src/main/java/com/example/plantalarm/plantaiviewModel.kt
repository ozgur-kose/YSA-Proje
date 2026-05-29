package com.example.plantalarm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

class PlantAiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlantAiUiState())
    val uiState = _uiState.asStateFlow()

    fun predictPlantAiState(
        humidity: Float,
        temperature: Float,
        pH: Float,
        nitrogen: Int,
        phosphorus: Int,
        potassium: Int
    ) {
        val newSensors = SensorValues(
            nitrogen = nitrogen,
            phosphorus = phosphorus,
            potassium = potassium,
            pH = pH,
            humidity = humidity,
            temperature = temperature
        )

        val newStressLevel = when {
            humidity < 40f -> StressLevel.RED
            temperature > 33f -> StressLevel.YELLOW
            else -> StressLevel.GREEN
        }

        val action = if (newStressLevel == StressLevel.RED) {
            "Bölge 4'te düşük nem algılandı! Otonom sulama sistemi hazır."
        } else null

        val currentHistory = _uiState.value.humidityHistory.toMutableList()
        if (currentHistory.size >= 8) currentHistory.removeAt(0)
        currentHistory.add(humidity)

        val currentLogs = _uiState.value.logs.toMutableList()
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        currentLogs.add(0, "[$time] Manuel Test -> Nem: %$humidity, Sıc: $temperature°C -> ${newStressLevel.label}")

        _uiState.update { currentState ->
            currentState.copy(
                stressLevel = newStressLevel,
                sensors = newSensors,
                humidityHistory = currentHistory,
                logs = currentLogs,
                actionMessage = action
            )
        }
    }

    fun triggerAutonomousAction() {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        _uiState.update { currentState ->
            currentState.copy(
                stressLevel = StressLevel.GREEN,
                actionMessage = null,
                logs = currentState.logs.toMutableList().apply {
                    add(0, "[$time] Otonom Sulama Başlatıldı. Sistem normale döndü.")
                }
            )
        }
    }
}