package com.example.vocabkid.presentation.vocabulary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
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
import com.example.vocabkid.domain.model.DateUtils
import com.example.vocabkid.presentation.components.EmptyMessage
import com.example.vocabkid.presentation.components.KidTopBar

@Composable
fun VocabularyDetailScreen(
    wordId: Long,
    viewModel: VocabularyViewModel,
    onBackClick: () -> Unit
) {
    val detail by viewModel.observeWord(wordId).collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            KidTopBar(
                title = "Detail Kosakata",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        val item = detail
        if (item == null) {
            EmptyMessage(
                title = "Kosakata tidak ditemukan",
                message = "Data mungkin sudah dihapus.",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            val progress = item.progress

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
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = item.word.englishWord,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = item.word.indonesianMeaning,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(item.word.category) }
                        )
                    }
                }

                DetailRow(title = "Contoh kalimat", value = item.word.exampleSentence)
                DetailRow(title = "Status", value = progress?.status ?: "Baru")
                DetailRow(
                    title = "Terakhir dilatih",
                    value = DateUtils.formatDate(progress?.lastReviewedDate)
                )
                DetailRow(
                    title = "Ulang lagi",
                    value = DateUtils.formatDate(progress?.dueDate)
                )
                DetailRow(
                    title = "Jawaban benar",
                    value = (progress?.correctCount ?: 0).toString()
                )
                DetailRow(
                    title = "Perlu dicoba lagi",
                    value = (progress?.wrongCount ?: 0).toString()
                )
                DetailRow(
                    title = "Putaran latihan",
                    value = "${progress?.repetition ?: 0} kali"
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
