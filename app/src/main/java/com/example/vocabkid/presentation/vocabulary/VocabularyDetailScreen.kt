package com.example.vocabkid.presentation.vocabulary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.domain.model.DateUtils
import com.example.vocabkid.presentation.components.CategoryChip
import com.example.vocabkid.presentation.components.CategoryIllustrationImage
import com.example.vocabkid.presentation.components.EmptyMessage
import com.example.vocabkid.presentation.components.KidTopBar
import com.example.vocabkid.presentation.components.SpeakerIconButton
import com.example.vocabkid.presentation.components.categoryThemeContainerColor
import com.example.vocabkid.presentation.components.categoryThemeContentColor
import com.example.vocabkid.presentation.components.rememberEnglishTextSpeaker

@Composable
fun VocabularyDetailScreen(
    wordId: Long,
    viewModel: VocabularyViewModel,
    onBackClick: () -> Unit
) {
    val detail by viewModel.observeWord(wordId).collectAsStateWithLifecycle(initialValue = null)
    var editingWord by remember { mutableStateOf<WordEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            KidTopBar(
                title = "Detail Kosakata",
                onBackClick = onBackClick,
                actions = {
                    detail?.word?.let {
                        IconButton(onClick = { editingWord = it }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit kosakata"
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus kosakata"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val item = detail
        if (item == null) {
            EmptyMessage(
                title = "Kosakata tidak ditemukan",
                message = "Data mungkin sudah dihapus.",
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(start = 16.dp, top = 4.dp, end = 16.dp)
            )
        } else {
            val progress = item.progress
            val speaker = rememberEnglishTextSpeaker()
            val categoryContainerColor = categoryThemeContainerColor(item.word.category)
            val categoryContentColor = categoryThemeContentColor(item.word.category)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = categoryContainerColor,
                        contentColor = categoryContentColor
                    )
                ) {
                    CategoryIllustrationImage(
                        category = item.word.category,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop,
                        fallbackIconSize = 64.dp
                    )
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = item.word.englishWord,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = categoryContentColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 52.dp)
                            )
                            SpeakerIconButton(
                                text = item.word.englishWord,
                                contentDescription = "Dengarkan kata ${item.word.englishWord}",
                                speaker = speaker,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                        Text(
                            text = item.word.indonesianMeaning,
                            style = MaterialTheme.typography.headlineSmall,
                            color = categoryContentColor
                        )
                        CategoryChip(category = item.word.category)
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

    // Edit dialog
    editingWord?.let { word ->
        DetailWordFormDialog(
            title = "Edit Kosakata",
            initialWord = word,
            onDismiss = { editingWord = null },
            onSave = { english, meaning, category, example ->
                viewModel.updateWord(
                    word.copy(
                        englishWord = english.trim(),
                        indonesianMeaning = meaning.trim(),
                        category = category.trim(),
                        exampleSentence = example.trim()
                    )
                )
                editingWord = null
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        detail?.word?.let { word ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus kosakata?") },
                text = { Text("Kosakata \"${word.englishWord}\" akan dihapus dari daftar belajar.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteWord(word)
                            showDeleteDialog = false
                            onBackClick()
                        }
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
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

@Composable
private fun DetailWordFormDialog(
    title: String,
    initialWord: WordEntity,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var englishWord by remember { mutableStateOf(initialWord.englishWord) }
    var indonesianMeaning by remember { mutableStateOf(initialWord.indonesianMeaning) }
    var category by remember { mutableStateOf(initialWord.category) }
    var exampleSentence by remember { mutableStateOf(initialWord.exampleSentence) }
    val canSave = englishWord.isNotBlank() &&
        indonesianMeaning.isNotBlank() &&
        category.isNotBlank() &&
        exampleSentence.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = englishWord,
                    onValueChange = { englishWord = it },
                    label = { Text("Kata Inggris") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = indonesianMeaning,
                    onValueChange = { indonesianMeaning = it },
                    label = { Text("Arti Indonesia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = exampleSentence,
                    onValueChange = { exampleSentence = it },
                    label = { Text("Contoh kalimat") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        englishWord,
                        indonesianMeaning,
                        category,
                        exampleSentence
                    )
                },
                enabled = canSave
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
