package com.example.plantalarm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun plantaidashboard(viewModel: PlantAiViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var inputTemp by remember { mutableStateOf("24") }
    var inputHumidity by remember { mutableStateOf("65") }
    var inputMoisture by remember { mutableStateOf("55") }
    var inputPH by remember { mutableStateOf("6.5") }
    var inputN by remember { mutableStateOf("40") }
    var inputP by remember { mutableStateOf("50") }
    var inputK by remember { mutableStateOf("45") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(uiState.stressLevel.color.copy(alpha = 0.06f))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Plant AI", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A1A1A))
                    Text(text = "Sistem İzleme Paneli", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }

                Surface(color = uiState.stressLevel.color, shape = RoundedCornerShape(24.dp), shadowElevation = 4.dp) {
                    Text(
                        text = uiState.stressLevel.label,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Yapay Zeka Model Testi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2C3E50))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = inputTemp, onValueChange = { inputTemp = it },
                            label = { Text("Sıcaklık (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputHumidity, onValueChange = { inputHumidity = it },
                            label = { Text("Hava Nemi (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = inputMoisture, onValueChange = { inputMoisture = it },
                            label = { Text("Toprak Nemi") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputPH, onValueChange = { inputPH = it },
                            label = { Text("pH Seviyesi") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputN, onValueChange = { inputN = it },
                            label = { Text("N (Azot)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputP, onValueChange = { inputP = it },
                            label = { Text("P (Fosfor)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputK, onValueChange = { inputK = it },
                            label = { Text("K (Potasyum)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val t = inputTemp.toFloatOrNull() ?: 24f
                            val h = inputHumidity.toFloatOrNull() ?: 65f
                            val m = inputMoisture.toFloatOrNull() ?: 55f
                            val p = inputPH.toFloatOrNull() ?: 6.5f
                            val n = inputN.toIntOrNull() ?: 40
                            val phos = inputP.toIntOrNull() ?: 50
                            val pot = inputK.toIntOrNull() ?: 45

                            viewModel.predictPlantAiState(t, h, m, p, n, phos, pot)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50))
                    ) {
                        Text("Verileri Modele Gönder", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            // Yapay Zeka Performans Metrikleri Panel
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Yapay Zeka Performans Metrikleri", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "F1-Skoru", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = uiState.f1Score, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Model Güven Skoru", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = uiState.modelGuvenSkoru, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF1976D2))
                        }
                    }
                }
            }

            // --- TODO: ARKADAŞINIZIN EKLEYECEĞİ ALANLAR ---
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "[ Veri Görselleştirme Alanı ]", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(text = "Anlık sensör verileri ve nem değişim grafiği buraya eklenecektir.", fontSize = 12.sp, color = Color.Gray.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
            }

            // Otonom Aksiyon Merkezi
            if (uiState.stressLevel == StressLevel.RED && uiState.actionMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F1)), border = BorderStroke(1.dp, Color(0xFFFFCDD2))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFD32F2F), RoundedCornerShape(50)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "YAPAY ZEKA ANOMALİ SİNYALİ", color = Color(0xFFD32F2F), fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                        Text(text = uiState.actionMessage!!, color = Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Button(
                            onClick = { viewModel.triggerAutonomousAction() },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Otonom Sulama Sistemini Tetikle", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Log Paneli
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Sistem İşlem Günlüğü (Logs)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                Card(modifier = Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (uiState.logs.isEmpty()) {
                            item { Text(text = "Henüz bir simülasyon veya işlem gerçekleşmedi.", color = Color.Gray, fontSize = 13.sp) }
                        } else {
                            items(uiState.logs) { log ->
                                Surface(color = Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Text(text = log, fontSize = 13.sp, color = Color(0xFF333333), modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}