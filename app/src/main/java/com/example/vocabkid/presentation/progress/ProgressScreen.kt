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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.presentation.components.DifficultWordsDialog
import com.example.vocabkid.presentation.components.KidTopBar
import com.example.vocabkid.presentation.components.ProgressLine

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    bottomContentPadding: Dp = 0.dp,
    onBackClick: (() -> Unit)? = null,
    onStudyClick: () -> Unit = {}
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val difficultWords by viewModel.difficultWords.collectAsStateWithLifecycle()
    var isDifficultWordsDialogOpen by rememberSaveable { mutableStateOf(false) }
    val masteredPercent = if (stats.totalWords == 0) {
        0
    } else {
        (stats.masteredWords * 100) / stats.totalWords
    }

    if (isDifficultWordsDialogOpen) {
        DifficultWordsDialog(
            words = difficultWords,
            onDismiss = { isDifficultWordsDialogOpen = false },
            onPracticeClick = onStudyClick
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
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
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 18.dp)
                .padding(bottom = bottomContentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                ProgressVisualStatCard(
                    type = ProgressStatVisual.Total,
                    value = stats.totalWords.toString(),
                    label = "Total",
                    supportingText = "kosakata",
                    modifier = Modifier.weight(1f)
                )
                ProgressVisualStatCard(
                    type = ProgressStatVisual.Mastered,
                    value = stats.masteredWords.toString(),
                    label = "Dikuasai",
                    supportingText = "kata",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProgressVisualStatCard(
                    type = ProgressStatVisual.Due,
                    value = stats.dueWords.toString(),
                    label = "Ulang",
                    supportingText = "hari ini",
                    modifier = Modifier.weight(1f)
                )
                ProgressVisualStatCard(
                    type = ProgressStatVisual.Accuracy,
                    value = "${stats.accuracyPercent}%",
                    label = "Akurasi",
                    supportingText = "jawaban benar",
                    modifier = Modifier.weight(1f)
                )
            }

            ProgressVisualStatCard(
                type = ProgressStatVisual.Reviews,
                value = stats.totalReviews.toString(),
                label = "Total latihan",
                supportingText = "flashcard dan kuis"
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

            ProgressVisualStatCard(
                type = ProgressStatVisual.Focus,
                value = stats.frequentlyWrongWords.toString(),
                label = "Kata Sulit",
                supportingText = "tekan untuk melihat",
                onClick = { isDifficultWordsDialogOpen = true }
            )
        }
    }
}
