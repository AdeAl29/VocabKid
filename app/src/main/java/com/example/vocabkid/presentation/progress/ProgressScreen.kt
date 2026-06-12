package com.example.vocabkid.presentation.progress

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
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
import com.example.vocabkid.presentation.components.KidTopBar
import com.example.vocabkid.presentation.components.ProgressLine
import com.example.vocabkid.presentation.components.StatCard

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onBackClick: () -> Unit
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val masteredPercent = if (stats.totalWords == 0) {
        0
    } else {
        (stats.masteredWords * 100) / stats.totalWords
    }

    Scaffold(
        topBar = {
            KidTopBar(
                title = "Progress Belajar",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$masteredPercent% kosakata dikuasai",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${stats.masteredWords} dari ${stats.totalWords} kata sudah terasa lebih familiar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    ProgressLine(
                        label = "Target penguasaan",
                        value = stats.masteredWords,
                        max = stats.totalWords
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total",
                    value = stats.totalWords.toString(),
                    supportingText = "kosakata",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Dikuasai",
                    value = stats.masteredWords.toString(),
                    supportingText = "kata",
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
                    title = "Ulang",
                    value = stats.dueWords.toString(),
                    supportingText = "hari ini",
                    icon = Icons.Default.Refresh,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Akurasi",
                    value = "${stats.accuracyPercent}%",
                    supportingText = "jawaban benar",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            StatCard(
                title = "Total latihan",
                value = stats.totalReviews.toString(),
                supportingText = "flashcard dan kuis",
                icon = Icons.Default.BarChart,
                accentColor = MaterialTheme.colorScheme.tertiary
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Ritme latihan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                ProgressLine(
                    label = "Kosakata dikuasai",
                    value = stats.masteredWords,
                    max = stats.totalWords
                )
                ProgressLine(
                    label = "Akurasi jawaban",
                    value = stats.accuracyPercent,
                    max = 100
                )
                ProgressLine(
                    label = "Masih perlu dibantu",
                    value = stats.frequentlyWrongWords,
                    max = stats.totalWords
                )
            }

            if (stats.frequentlyWrongWords > 0) {
                StatCard(
                    title = "Fokus berikutnya",
                    value = stats.frequentlyWrongWords.toString(),
                    supportingText = "kata yang sebaiknya dilatih lagi",
                    icon = Icons.Default.ErrorOutline,
                    accentColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
