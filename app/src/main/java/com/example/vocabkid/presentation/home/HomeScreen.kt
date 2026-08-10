package com.example.vocabkid.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.data.local.entity.StudentEntity
import com.example.vocabkid.domain.model.StudentAvatar
import com.example.vocabkid.presentation.components.KidTopBar
import com.example.vocabkid.presentation.components.DifficultWordsDialog
import com.example.vocabkid.presentation.components.RobotCoach
import com.example.vocabkid.presentation.components.StudentAvatarBadge

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onPronunciationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStoryClick: () -> Unit,
    onStudyClick: () -> Unit,
    onConversationClick: () -> Unit,
    bottomContentPadding: Dp = 0.dp
) {
    val student by viewModel.student.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val difficultWords by viewModel.difficultWords.collectAsStateWithLifecycle()
    var isDifficultWordsDialogOpen by rememberSaveable { mutableStateOf(false) }

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
        topBar = { KidTopBar(title = "VocabKid") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 16.dp)
                .padding(bottom = bottomContentPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 86.dp)
            ) {
                HomeProfileSummary(
                    student = student,
                    dueToday = stats.dueToday,
                    onClick = onProfileClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(end = 104.dp)
                        .fillMaxWidth()
                )
                RobotCoach(
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onPronunciationClick = onPronunciationClick,
                    onStoryClick = onStoryClick,
                    onConversationClick = onConversationClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            HomeLearningCarousel(
                dueToday = stats.dueToday,
                isDarkTheme = isDarkTheme
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeVisualStatCard(
                    type = HomeStatVisual.Today,
                    value = stats.dueToday.toString(),
                    label = "Hari ini",
                    supportingText = "kata perlu diulang",
                    modifier = Modifier.weight(1f)
                )
                HomeVisualStatCard(
                    type = HomeStatVisual.Mastered,
                    value = stats.masteredWords.toString(),
                    label = "Dikuasai",
                    supportingText = "kosakata sudah kuat",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeVisualStatCard(
                    type = HomeStatVisual.Practice,
                    value = stats.frequentlyWrongWords.toString(),
                    label = "Kata Sulit",
                    supportingText = "sering salah",
                    onClick = { isDifficultWordsDialogOpen = true },
                    modifier = Modifier.weight(1f)
                )
                HomeVisualStatCard(
                    type = HomeStatVisual.Vocabulary,
                    value = stats.totalWords.toString(),
                    label = "Kosakata",
                    supportingText = "kata tersedia",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HomeProfileSummary(
    student: StudentEntity?,
    dueToday: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = student?.name?.takeIf { it.isNotBlank() } ?: "Siswa"
    val gradeText = student?.grade?.let { "Kelas $it SD" } ?: "Kelas - SD"
    val practiceText = if (dueToday > 0) {
        "$dueToday kata"
    } else {
        "Beres"
    }
    val avatar = StudentAvatar.fromId(student?.avatar)

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 78.dp)
                .padding(start = 8.dp, top = 8.dp, end = 14.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.surface),
                shadowElevation = 2.dp
            ) {
                StudentAvatarBadge(
                    avatar = avatar,
                    modifier = Modifier.size(64.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = gradeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.86f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = practiceText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
