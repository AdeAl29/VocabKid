package com.example.vocabkid.presentation.vocabulary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import com.example.vocabkid.presentation.components.EmptyMessage
import com.example.vocabkid.presentation.components.KidTopBar

@Composable
fun VocabularyScreen(
    viewModel: VocabularyViewModel,
    onBackClick: () -> Unit,
    onDetailClick: (Long) -> Unit
) {
    val words by viewModel.words.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<WordEntity?>(null) }
    var deletingWord by remember { mutableStateOf<WordEntity?>(null) }

    Scaffold(
        topBar = {
            KidTopBar(
                title = "Daftar Kosakata",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah kosakata"
                )
            }
        }
    ) { paddingValues ->
        if (words.isEmpty()) {
            EmptyMessage(
                title = "Belum ada kosakata",
                message = "Tambahkan kosakata baru untuk mulai belajar.",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    top = 12.dp,
                    bottom = 96.dp
                )
            ) {
                items(words, key = { it.word.id }) { item ->
                    VocabularyItemCard(
                        item = item,
                        onDetailClick = { onDetailClick(item.word.id) },
                        onEditClick = { editingWord = item.word },
                        onDeleteClick = { deletingWord = item.word }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        WordFormDialog(
            title = "Tambah Kosakata",
            initialWord = null,
            onDismiss = { showAddDialog = false },
            onSave = { english, meaning, category, example ->
                viewModel.addWord(english, meaning, category, example)
                showAddDialog = false
            }
        )
    }

    editingWord?.let { word ->
        WordFormDialog(
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

    deletingWord?.let { word ->
        AlertDialog(
            onDismissRequest = { deletingWord = null },
            title = { Text("Hapus kosakata?") },
            text = { Text("Kosakata \"${word.englishWord}\" dan progressnya akan dihapus.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWord(word)
                        deletingWord = null
                    }
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingWord = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun VocabularyItemCard(
    item: WordWithProgressEntity,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.word.englishWord,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.word.indonesianMeaning,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onDetailClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Detail kosakata"
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit kosakata"
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus kosakata"
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = onDetailClick,
                    label = { Text(item.word.category) }
                )
                AssistChip(
                    onClick = onDetailClick,
                    label = { Text(item.progress?.status ?: "Baru") }
                )
            }
        }
    }
}

@Composable
private fun WordFormDialog(
    title: String,
    initialWord: WordEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var englishWord by remember(initialWord?.id) {
        mutableStateOf(initialWord?.englishWord ?: "")
    }
    var indonesianMeaning by remember(initialWord?.id) {
        mutableStateOf(initialWord?.indonesianMeaning ?: "")
    }
    var category by remember(initialWord?.id) {
        mutableStateOf(initialWord?.category ?: "")
    }
    var exampleSentence by remember(initialWord?.id) {
        mutableStateOf(initialWord?.exampleSentence ?: "")
    }
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
