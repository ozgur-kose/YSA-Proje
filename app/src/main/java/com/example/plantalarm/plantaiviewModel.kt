package com.example.plantalarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlantAiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlantAiUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = PlantAiApiService.create()

    fun predictPlantAiState(
        temperature: Float,
        humidity: Float,
        moisture: Float,
        pH: Float,
        nitrogen: Int,
        phosphorus: Int,
        potassium: Int
    ) {
        val newSensors = SensorValues(temperature, humidity, moisture, pH, nitrogen, phosphorus, potassium)


        val inputList = listOf(
            temperature,
            humidity,
            moisture,
            pH,
            nitrogen.toFloat(),
            phosphorus.toFloat(),
            potassium.toFloat()
        )

        viewModelScope.launch {
            try {
                val response = apiService.predictState(PlantAiRequest(degerler = inputList))

                if (response.hata != null) {
                    addLog("API Hatası: ${response.hata}")
                    return@launch
                }

                val newStressLevel = if (response.durum?.contains("ANOMALİ") == true) {
                    StressLevel.RED
                } else {
                    StressLevel.GREEN
                }

                val action = if (newStressLevel == StressLevel.RED) {
                    "Yapay Zeka Sinyali: Kritik durum! Otonom sulama sistemi hazır."
                } else null

                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
                val logText = "[$time] Canlı API -> ${response.durum} (${response.risk_seviyesi}) -> Güven: ${response.model_guven_skoru}"

                _uiState.update { currentState ->
                    currentState.copy(
                        stressLevel = newStressLevel,
                        sensors = newSensors,
                        actionMessage = action,
                        modelGuvenSkoru = response.model_guven_skoru ?: "%0.0",
                        logs = currentState.logs.toMutableList().apply { add(0, logText) }
                    )
                }

            } catch (e: Exception) {
                addLog("Bağlantı Başarısız: FastAPI açık mı? (10.0.2.2:8000)")
            }
        }
    }

    private fun addLog(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        _uiState.update { currentState ->
            currentState.copy(
                logs = currentState.logs.toMutableList().apply { add(0, "[$time] $message") }
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