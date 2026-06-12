package com.example.vocabkid.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.presentation.components.BigMenuButton
import com.example.vocabkid.presentation.components.KidTopBar
import com.example.vocabkid.presentation.components.StatCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStudyClick: () -> Unit,
    onQuizClick: () -> Unit,
    onVocabularyClick: () -> Unit,
    onProgressClick: () -> Unit
) {
    val student by viewModel.student.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { KidTopBar(title = "VocabKid") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Halo, ${student?.name ?: "Siswa"}!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kelas ${student?.grade ?: "-"} SD",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (stats.dueToday > 0) {
                            "${stats.dueToday} kata siap diulang hari ini"
                        } else {
                            "Semua latihan hari ini sudah beres"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Pilih sesi singkat dan lanjutkan dari kata yang paling perlu dilatih.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Hari ini",
                    value = stats.dueToday.toString(),
                    supportingText = "kata perlu diulang",
                    icon = Icons.Default.Refresh,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Dikuasai",
                    value = stats.masteredWords.toString(),
                    supportingText = "kosakata",
                    icon = Icons.Default.CheckCircle,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Latihan",
                    value = stats.reviewsToday.toString(),
                    supportingText = "selesai hari ini",
                    icon = Icons.Default.BarChart,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Kosakata",
                    value = stats.totalWords.toString(),
                    supportingText = "tersedia",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BigMenuButton(
                    text = "Mulai Belajar",
                    supportingText = "Flashcard kata yang jatuh tempo",
                    icon = Icons.Default.PlayArrow,
                    onClick = onStudyClick
                )
                BigMenuButton(
                    text = "Latihan Kuis",
                    supportingText = "Uji arti kata dengan pilihan ganda",
                    icon = Icons.Default.Quiz,
                    onClick = onQuizClick
                )
                BigMenuButton(
                    text = "Daftar Kosakata",
                    supportingText = "Cari, tambah, dan cek detail kata",
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = onVocabularyClick
                )
                BigMenuButton(
                    text = "Progress Belajar",
                    supportingText = "Lihat perkembangan latihanmu",
                    icon = Icons.Default.BarChart,
                    onClick = onProgressClick
                )
            }
        }
    }
}
