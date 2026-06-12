package com.example.vocabkid.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Halo, ${student?.name ?: "Siswa"}! ⭐",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kelas ${student?.grade ?: "-"} SD",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Hari ini",
                    value = stats.dueToday.toString(),
                    supportingText = "kata perlu diulang",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Dikuasai",
                    value = stats.masteredWords.toString(),
                    supportingText = "kosakata",
                    modifier = Modifier.weight(1f)
                )
            }

            StatCard(
                title = "Latihan hari ini",
                value = stats.reviewsToday.toString(),
                supportingText = "review flashcard dan kuis"
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BigMenuButton(
                    text = "Mulai Belajar",
                    icon = Icons.Default.PlayArrow,
                    onClick = onStudyClick
                )
                BigMenuButton(
                    text = "Latihan Kuis",
                    icon = Icons.Default.Quiz,
                    onClick = onQuizClick
                )
                BigMenuButton(
                    text = "Daftar Kosakata",
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = onVocabularyClick
                )
                BigMenuButton(
                    text = "Progress Belajar",
                    icon = Icons.Default.BarChart,
                    onClick = onProgressClick
                )
            }

            Text(
                text = "Belajar sedikit hari ini membuat ingatan lebih kuat besok. 📚",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
