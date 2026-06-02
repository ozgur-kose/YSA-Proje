package com.example.plantalarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _isAutoFetchRunning = MutableStateFlow(false)
    val isAutoFetchRunning = _isAutoFetchRunning.asStateFlow()

    private var autoFetchJob: Job? = null

    fun toggleAutoFetch() {
        _isAutoFetchRunning.value = !_isAutoFetchRunning.value

        if (_isAutoFetchRunning.value) {
            addLog("Canlı API veri akışı BAŞLATILDI.")
            autoFetchJob?.cancel()

            autoFetchJob = viewModelScope.launch {
                while (_isAutoFetchRunning.value) {
                    try {

                        val liveData = apiService.getLiveSensorData()


                        predictPlantAiState(
                           
                            sensorId = liveData.sensor_id ?: "Bilinmeyen Konum",
                            temperature = liveData.temperature,
                            humidity = liveData.humidity,
                            moisture = liveData.moisture,
                            pH = liveData.pH,
                            nitrogen = liveData.nitrogen,
                            phosphorus = liveData.phosphorus,
                            potassium = liveData.potassium
                        )
                    } catch (e: Exception) {
                        addLog("Veri Çekilemedi: ${e.localizedMessage ?: "Bağlantı Hatası"}")
                    }

                    delay(5000) // 5 saniyede bir yeni veriyi kontrol et
                }
            }
        } else {
            addLog("Canlı veri akışı DURDURULDU.")
            autoFetchJob?.cancel()
            autoFetchJob = null
        }
    }


    fun predictPlantAiState(
        sensorId: String,
        temperature: Float, humidity: Float, moisture: Float,
        pH: Float, nitrogen: Int, phosphorus: Int, potassium: Int
    ) {
        val newSensors = SensorValues(temperature, humidity, moisture, pH, nitrogen, phosphorus, potassium)
        val inputList = listOf(temperature, humidity, moisture, pH, nitrogen.toFloat(), phosphorus.toFloat(), potassium.toFloat())

        viewModelScope.launch {
            try {
                val response = apiService.predictState(PlantAiRequest(degerler = inputList))

                if (response.hata != null) {
                    addLog("API Hatası: ${response.hata}")
                    return@launch
                }

                val newStressLevel = if (response.durum?.contains("ANOMALİ") == true) StressLevel.RED else StressLevel.GREEN


                // Anomali varsa konum bilgisini (Z04-N12) karta ekliyoruz
                val action = if (newStressLevel == StressLevel.RED) {
                    "📍 Müdahale Noktası: $sensorId\n🔍 Tespit: ${response.teshis ?: "Bilinmeyen Anomali"}\n💡 Çözüm: ${response.aksiyon ?: "Manuel kontrol sağlayın."}"
                } else null

                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
                val logText = "[$time] Canlı Analiz -> ${response.durum} -> Güven: ${response.model_guven_skoru}"

                _uiState.update { currentState ->
                    val updatedMoistHistory = (currentState.moistureHistory + moisture).takeLast(8)
                    val updatedTempHistory = (currentState.temperatureHistory + temperature).takeLast(8)
                    val updatedHumHistory = (currentState.humidityHistory + humidity).takeLast(8)

                    currentState.copy(
                        stressLevel = newStressLevel,
                        sensors = newSensors,
                        moistureHistory = updatedMoistHistory,
                        temperatureHistory = updatedTempHistory,
                        humidityHistory = updatedHumHistory,
                        actionMessage = action,
                        modelGuvenSkoru = response.model_guven_skoru ?: "%0.0",
                        logs = currentState.logs.toMutableList().apply { add(0, logText) }
                    )
                }

            } catch (e: Exception) {
                addLog("Tahmin Hatası: Sunucu bağlantısı kesildi.")
            }
        }
    }

    fun addLog(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        _uiState.update { currentState ->
            currentState.copy(logs = currentState.logs.toMutableList().apply { add(0, "[$time] $message") })
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