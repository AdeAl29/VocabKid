package com.example.vocabkid.presentation.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total",
                    value = stats.totalWords.toString(),
                    supportingText = "kosakata",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Dikuasai",
                    value = stats.masteredWords.toString(),
                    supportingText = "kosakata",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Perlu ulang",
                    value = stats.dueWords.toString(),
                    supportingText = "hari ini",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Akurasi",
                    value = "${stats.accuracyPercent}%",
                    supportingText = "jawaban benar",
                    modifier = Modifier.weight(1f)
                )
            }

            StatCard(
                title = "Review selesai",
                value = stats.totalReviews.toString(),
                supportingText = "total latihan tersimpan"
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
                label = "Sering salah",
                value = stats.frequentlyWrongWords,
                max = stats.totalWords
            )

            Text(
                text = "Progress tersimpan di database lokal, jadi tetap ada walaupun aplikasi ditutup."
            )
        }
    }
}
