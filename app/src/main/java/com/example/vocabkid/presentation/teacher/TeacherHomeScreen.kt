package com.example.vocabkid.presentation.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.presentation.components.KidTopBar

@Composable
fun TeacherHomeScreen(
    repository: VocabKidRepository,
    bottomContentPadding: Dp,
    onProfileClick: () -> Unit,
    onVocabularyClick: () -> Unit,
    onStudentsClick: () -> Unit
) {
    val teacher by repository.observeStudent().collectAsStateWithLifecycle(initialValue = null)
    val words by repository.observeWordsWithProgress().collectAsStateWithLifecycle(initialValue = emptyList())
    val rosterStudents by repository.observeRosterStudents().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(containerColor = Color.Transparent, topBar = { KidTopBar(title = "Ruang Guru") }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 20.dp + bottomContentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TeacherHero(name = teacher?.name ?: "Guru", onProfileClick = onProfileClick)
            Text("Ringkasan pembelajaran", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TeacherMetricCard(rosterStudents.size.toString(), "Jumlah siswa", Icons.Default.Groups, Color(0xFF176B87), Modifier.weight(1f), onStudentsClick)
                TeacherMetricCard(words.size.toString(), "Materi", Icons.AutoMirrored.Filled.MenuBook, Color(0xFF7257A8), Modifier.weight(1f), onVocabularyClick)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TeacherMetricCard(rosterStudents.map { it.grade }.distinct().size.toString(), "Kelas", Icons.Default.School, Color(0xFFB46A22), Modifier.weight(1f), onStudentsClick)
                TeacherMetricCard(rosterStudents.count { it.gender == "L" }.toString(), "Laki-laki", Icons.Default.Person, Color(0xFFB24D62), Modifier.weight(1f), onStudentsClick)
            }
            Text("Akses cepat", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TeacherAction(Icons.AutoMirrored.Filled.MenuBook, "Kelola materi belajar", "Atur kosakata yang digunakan murid untuk berlatih.", onVocabularyClick)
            TeacherAction(Icons.Default.Groups, "Lihat data siswa", "Buka daftar siswa berdasarkan kelas dan jenis kelamin.", onStudentsClick)
        }
    }
}

@Composable
private fun TeacherHero(name: String, onProfileClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(
            Brush.linearGradient(listOf(Color(0xFF123B5D), Color(0xFF176B87), Color(0xFF3B9A9C)))
        ).padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.16f)) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("DASHBOARD GURU", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            Text("Selamat datang,", color = Color.White.copy(alpha = 0.78f), style = MaterialTheme.typography.bodyLarge)
            Text(name, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Siapkan pengalaman belajar terbaik untuk murid hari ini.", color = Color.White.copy(alpha = 0.86f))
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                Text("  Lihat profil", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TeacherMetricCard(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, onClick = onClick, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Text(value, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White.copy(alpha = 0.86f), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun TeacherAction(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
