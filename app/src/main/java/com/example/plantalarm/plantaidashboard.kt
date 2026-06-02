package com.example.plantalarm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun plantaidashboard(viewModel: PlantAiViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val isAutoFetchRunning by viewModel.isAutoFetchRunning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8F4))
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // BAŞLIK VE LOGO
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ModernLeafIllustration(modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("SPlantAI", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B4332))
                    Text("Akıllı Sera Paneli", color = Color(0xFF52796F), fontSize = 14.sp)
                }
            }
            Surface(color = uiState.stressLevel.color.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Text(
                    text = uiState.stressLevel.label, color = uiState.stressLevel.color,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // DAİRESEL GÖSTERGELER
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            AnimatedCircularGauge(value = uiState.sensors.moisture, maxValue = 100f, title = "Toprak Nemi", unit = "%", activeColor = Color(0xFF29B6F6))
            AnimatedCircularGauge(value = uiState.sensors.temperature, maxValue = 50f, title = "Sıcaklık", unit = "°C", activeColor = Color(0xFFFFA726))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // MİNİ KARTLAR
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MiniSensorCard(title = "Hava Nemi", value = "%${uiState.sensors.humidity.toInt()}", iconColor = Color(0xFF26C6DA), modifier = Modifier.weight(1f))
            MiniSensorCard(title = "Toprak pH", value = String.format(java.util.Locale.US, "%.1f", uiState.sensors.pH), iconColor = Color(0xFFAB47BC), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // NPK BARLARI
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Toprak Besin Değerleri (NPK)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2C3E50))
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedNPKBar(label = "N", value = uiState.sensors.nitrogen, maxValue = 100, color = Color(0xFFEC407A))
                AnimatedNPKBar(label = "P", value = uiState.sensors.phosphorus, maxValue = 100, color = Color(0xFF26A69A))
                AnimatedNPKBar(label = "K", value = uiState.sensors.potassium, maxValue = 100, color = Color(0xFF5C6BC0))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ANOMALİ ALARM KARTI (XAI Destekli)
        if (uiState.stressLevel == StressLevel.RED && uiState.actionMessage != null) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F1)), border = BorderStroke(1.dp, Color(0xFFFFCDD2))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚨 ANOMALİ TESPİT EDİLDİ", color = Color(0xFFD32F2F), fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = uiState.actionMessage!!, color = Color.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.triggerAutonomousAction() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                        Text("Otonom Çözümü Uygula")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // OTOMATİK VERİ ÇEKME BUTONU
        Button(
            onClick = { viewModel.toggleAutoFetch() },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isAutoFetchRunning) Color(0xFFD32F2F) else Color(0xFF2E7D32))
        ) {
            Text(text = if (isAutoFetchRunning) "Veri Akışını Durdur" else "Otomatik Sensör Akışını Başlat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // AKILLI ESNEK ÖLÇEKLİ GRAFİKLER
        Text("💧 Toprak Nemi Geçmişi", fontWeight = FontWeight.Bold, color = Color(0xFF1B4332))
        Spacer(modifier = Modifier.height(8.dp))
        ModernSmartLineGraph(
            history = uiState.moistureHistory.ifEmpty { listOf(0f, 0f) },
            lineColor = Color(0xFF29B6F6),
            gradientColor = Color(0xFF81D4FA),
            modifier = Modifier.fillMaxWidth().height(140.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("🔥 Ortam Sıcaklığı Geçmişi", fontWeight = FontWeight.Bold, color = Color(0xFF1B4332))
        Spacer(modifier = Modifier.height(8.dp))
        ModernSmartLineGraph(
            history = uiState.temperatureHistory.ifEmpty { listOf(0f, 0f) },
            lineColor = Color(0xFFFFA726),
            gradientColor = Color(0xFFFFCC80),
            modifier = Modifier.fillMaxWidth().height(140.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // YAPAY ZEKA METRİKLERİ VE LOGLAR
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "F1-Skoru", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text(text = uiState.f1Score, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                }
            }
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Güven Skoru", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text(text = uiState.modelGuvenSkoru, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1976D2))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Sistem İşlem Günlüğü", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B4332))
            Card(modifier = Modifier.fillMaxWidth().height(140.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (uiState.logs.isEmpty()) {
                        item { Text(text = "Akışı başlattığınızda loglar buraya düşecektir...", color = Color.Gray, fontSize = 12.sp) }
                    } else {
                        items(uiState.logs) { log ->
                            Surface(color = Color(0xFFF1F8F4), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(text = log, fontSize = 12.sp, color = Color(0xFF333333), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}