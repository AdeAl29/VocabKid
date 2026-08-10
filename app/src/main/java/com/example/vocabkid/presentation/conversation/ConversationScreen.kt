package com.example.vocabkid.presentation.conversation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.data.local.entity.ConversationScenarioProgressEntity
import com.example.vocabkid.domain.model.ConversationPracticeStats
import com.example.vocabkid.presentation.components.EnglishTextSpeaker
import com.example.vocabkid.presentation.components.KidTopBar
import com.example.vocabkid.presentation.components.SpeakerIconButton
import com.example.vocabkid.presentation.components.rememberEnglishTextSpeaker
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel,
    bottomContentPadding: Dp = 0.dp,
    onBackClick: () -> Unit
) {
    val uiState = viewModel.uiState
    val progressRecords by viewModel.progressRecords.collectAsStateWithLifecycle()
    val practiceStats by viewModel.practiceStats.collectAsStateWithLifecycle()
    val progressByScenario = remember(progressRecords) {
        progressRecords.associateBy { progress -> progress.scenarioId }
    }
    val speaker = rememberEnglishTextSpeaker()

    BackHandler(enabled = uiState.mode != ConversationScreenMode.SCENE_LIST) {
        when (uiState.mode) {
            ConversationScreenMode.SCENE_LIST -> onBackClick()
            ConversationScreenMode.SCENARIO_LIST -> viewModel.backToScenes()
            ConversationScreenMode.CHAT -> viewModel.backToScenarioList()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            KidTopBar(
                title = when (uiState.mode) {
                    ConversationScreenMode.SCENE_LIST -> "Percakapan"
                    ConversationScreenMode.SCENARIO_LIST -> uiState.selectedScene?.title ?: "Skenario"
                    ConversationScreenMode.CHAT -> uiState.selectedScenario?.title ?: "Chat"
                },
                onBackClick = {
                    when (uiState.mode) {
                        ConversationScreenMode.SCENE_LIST -> onBackClick()
                        ConversationScreenMode.SCENARIO_LIST -> viewModel.backToScenes()
                        ConversationScreenMode.CHAT -> viewModel.backToScenarioList()
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.mode != ConversationScreenMode.CHAT) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.startRandomScenario() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null
                        )
                    },
                    text = { Text(text = "Acak") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 18.dp)
                .padding(bottom = bottomContentPadding)
        ) {
            ConversationBackdrop(
                scene = uiState.selectedScene,
                modifier = Modifier.matchParentSize()
            )

            when (uiState.mode) {
                ConversationScreenMode.SCENE_LIST -> {
                    ScenePicker(
                        state = uiState,
                        progressByScenario = progressByScenario,
                        practiceStats = practiceStats,
                        onSceneClick = viewModel::selectScene,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ConversationScreenMode.SCENARIO_LIST -> {
                    ScenarioPicker(
                        scene = uiState.selectedScene,
                        scenarios = viewModel.scenariosForSelectedScene(),
                        progressByScenario = progressByScenario,
                        onScenarioClick = viewModel::startScenario,
                        onRandomClick = { viewModel.startRandomScenario() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ConversationScreenMode.CHAT -> {
                    ScenarioChat(
                        state = uiState,
                        speaker = speaker,
                        onChoiceClick = viewModel::chooseReply,
                        onRestartClick = viewModel::restartCurrentScenario,
                        onBackToScenariosClick = viewModel::backToScenarioList,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenePicker(
    state: ConversationUiState,
    progressByScenario: Map<String, ConversationScenarioProgressEntity>,
    practiceStats: ConversationPracticeStats,
    onSceneClick: (ConversationScene) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 94.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ConversationHero(
                sceneCount = state.scenes.size,
                scenarioCount = state.scenarioCount,
                practiceStats = practiceStats
            )
        }
        items(
            items = state.scenes,
            key = { scene -> scene.id }
        ) { scene ->
            val sceneScenarios = ConversationScenarioLibrary.scenariosForScene(scene.id)
            val completedCount = sceneScenarios.count { scenario ->
                progressByScenario[scenario.id]?.isCompleted == true
            }
            SceneCard(
                scene = scene,
                scenarioCount = sceneScenarios.size,
                completedCount = completedCount,
                onClick = { onSceneClick(scene) }
            )
        }
    }
}

@Composable
private fun ConversationHero(
    sceneCount: Int,
    scenarioCount: Int,
    practiceStats: ConversationPracticeStats
) {
    val transition = rememberInfiniteTransition(label = "conversationHeroMotion")
    val floatOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "conversationHeroFloat"
    )
    val featuredCharacters = remember {
        ConversationScenarioLibrary.scenarios
            .map { scenario -> scenario.partner }
            .distinctBy { character -> character.id }
            .take(4)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        contentColor = Color.White,
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF16877A),
                            Color(0xFF4E6BE6),
                            Color(0xFFFFA43A)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val radius = 8.dp.toPx()
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.16f),
                    topLeft = Offset(size.width * 0.68f, size.height * 0.16f),
                    size = Size(size.width * 0.24f, size.height * 0.56f),
                    cornerRadius = CornerRadius(radius, radius)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.10f),
                    topLeft = Offset(size.width * 0.08f, size.height * 0.72f),
                    size = Size(size.width * 0.46f, size.height * 0.12f),
                    cornerRadius = CornerRadius(radius, radius)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(58.dp)
                            .graphicsLayer {
                                translationY = floatOffset
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.22f),
                        contentColor = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Latihan chat pilihan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$sceneCount scene dan $scenarioCount skenario percakapan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.88f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroAvatarStack(
                        characters = featuredCharacters,
                        modifier = Modifier.weight(1f)
                    )
                    HeroStatPill(
                        value = "$sceneCount",
                        label = "scene"
                    )
                    HeroStatPill(
                        value = "$scenarioCount",
                        label = "chat"
                    )
                    HeroStatPill(
                        value = "${practiceStats.completedScenarios}",
                        label = "selesai"
                    )
                }
                if (practiceStats.totalChoices > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.18f),
                        contentColor = Color.White,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
                    ) {
                        Text(
                            text = "${practiceStats.totalChoices} pilihan jawaban sudah dilatih",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SceneCard(
    scene: ConversationScene,
    scenarioCount: Int,
    completedCount: Int,
    onClick: () -> Unit
) {
    val previewCharacters = remember(scene.id) {
        ConversationScenarioLibrary.scenariosForScene(scene.id)
            .map { scenario -> scenario.partner }
            .distinctBy { character -> character.id }
            .take(2)
    }
    var visible by remember(scene.id) { mutableStateOf(false) }

    LaunchedEffect(scene.id) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(260)) + slideInHorizontally(
            animationSpec = tween(320, easing = FastOutSlowInEasing),
            initialOffsetX = { width -> width / 6 }
        )
    ) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, scene.accentColor.copy(alpha = 0.22f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SceneMiniArtwork(
                scene = scene,
                characters = previewCharacters,
                modifier = Modifier.size(74.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = scene.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = scene.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (completedCount > 0) {
                        "$completedCount / $scenarioCount skenario selesai"
                    } else {
                        "$scenarioCount skenario"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = scene.accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = scene.accentColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
    }
}

@Composable
private fun ScenarioPicker(
    scene: ConversationScene?,
    scenarios: List<ConversationScenario>,
    progressByScenario: Map<String, ConversationScenarioProgressEntity>,
    onScenarioClick: (ConversationScenario) -> Unit,
    onRandomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 94.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (scene != null) {
            item {
                SelectedSceneHeader(
                    scene = scene,
                    scenarioCount = scenarios.size,
                    completedCount = scenarios.count { scenario ->
                        progressByScenario[scenario.id]?.isCompleted == true
                    },
                    onRandomClick = onRandomClick
                )
            }
        }
        items(
            items = scenarios,
            key = { scenario -> scenario.id }
        ) { scenario ->
            ScenarioCard(
                scenario = scenario,
                progress = progressByScenario[scenario.id],
                onClick = { onScenarioClick(scenario) }
            )
        }
    }
}

@Composable
private fun SelectedSceneHeader(
    scene: ConversationScene,
    scenarioCount: Int,
    completedCount: Int,
    onRandomClick: () -> Unit
) {
    val previewCharacters = remember(scene.id) {
        ConversationScenarioLibrary.scenariosForScene(scene.id)
            .map { scenario -> scenario.partner }
            .distinctBy { character -> character.id }
            .take(2)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = scene.accentColor.copy(alpha = 0.16f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, scene.accentColor.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SceneMiniArtwork(
                scene = scene,
                characters = previewCharacters,
                modifier = Modifier.size(66.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = scene.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (completedCount > 0) {
                        "$completedCount dari $scenarioCount skenario sudah selesai"
                    } else {
                        "$scenarioCount skenario chat"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onRandomClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Acak")
            }
        }
    }
}

@Composable
private fun ScenarioCard(
    scenario: ConversationScenario,
    progress: ConversationScenarioProgressEntity?,
    onClick: () -> Unit
) {
    val scene = remember(scenario.sceneId) {
        ConversationScenarioLibrary.sceneForId(scenario.sceneId)
    }
    var visible by remember(scenario.id) { mutableStateOf(false) }

    LaunchedEffect(scenario.id) {
        delay(60)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(240)) + expandVertically(tween(260))
    ) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CharacterAvatar(
                    character = scenario.partner,
                    size = 46.dp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scenario.partner.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = scenario.partner.role,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text(
                        text = scenario.level,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            ScenarioPreviewStrip(
                scenario = scenario,
                scene = scene
            )
            if (progress != null) {
                ScenarioProgressBadge(progress = progress)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = scenario.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = scenario.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = scenario.goal,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mulai Chat")
            }
        }
    }
    }
}

@Composable
private fun ScenarioChat(
    state: ConversationUiState,
    speaker: EnglishTextSpeaker,
    onChoiceClick: (ConversationChoice) -> Unit,
    onRestartClick: () -> Unit,
    onBackToScenariosClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scenario = state.selectedScenario ?: return
    val scene = state.selectedScene
    var showHistory by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Compact progress header
        VnProgressHeader(
            scenario = scenario,
            scene = scene,
            answeredSteps = state.stepIndex.coerceAtMost(scenario.steps.size),
            isComplete = state.isComplete,
            onHistoryClick = { showHistory = !showHistory }
        )

        // Main VN stage area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Background atmosphere
            VnBackdrop(
                expression = state.currentExpression,
                accentColor = scene?.accentColor ?: MaterialTheme.colorScheme.primary,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Character portrait area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val displayChar = state.displayedCharacter ?: scenario.partner
                    CharacterPortrait(
                        character = displayChar,
                        expression = state.currentExpression,
                        isUserMoment = state.showUserMoment,
                        accentColor = scene?.accentColor ?: MaterialTheme.colorScheme.primary
                    )
                }

                // Dialogue box + choices area
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Current dialogue
                    val currentMsg = state.currentDialogue
                    if (currentMsg != null || state.isPartnerTyping) {
                        VnDialogueBox(
                            message = currentMsg,
                            isTyping = state.isPartnerTyping,
                            typingCharacter = state.typingCharacter ?: scenario.partner,
                            speaker = speaker,
                            accentColor = scene?.accentColor ?: MaterialTheme.colorScheme.primary
                        )
                    }

                    // Choice panel or completion
                    when {
                        state.isComplete -> {
                            CompletionPanel(
                                onRestartClick = onRestartClick,
                                onBackToScenariosClick = onBackToScenariosClick
                            )
                        }
                        state.activeChoices.isNotEmpty() -> {
                            VnChoicePanel(
                                choices = state.activeChoices,
                                onChoiceClick = onChoiceClick
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // History overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = showHistory,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(200)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                ),
                exit = fadeOut(tween(200)) + slideOutVertically(
                    targetOffsetY = { it / 3 }
                )
            ) {
                ChatHistoryOverlay(
                    messages = state.messages,
                    speaker = speaker,
                    onDismiss = { showHistory = false }
                )
            }
        }
    }
}

@Composable
private fun VnProgressHeader(
    scenario: ConversationScenario,
    scene: ConversationScene?,
    answeredSteps: Int,
    isComplete: Boolean,
    onHistoryClick: () -> Unit
) {
    val maxSteps = scenario.steps.size.coerceAtLeast(1)
    val progress = if (isComplete) 1f else answeredSteps.toFloat() / maxSteps.toFloat()
    val accent = scene?.accentColor ?: MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CharacterAvatar(
                    character = scenario.partner,
                    size = 36.dp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scenario.partner.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = scenario.goal,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onHistoryClick) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Riwayat",
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "${answeredSteps.coerceAtMost(scenario.steps.size)}/${scenario.steps.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 6.dp)
            )
        }
    }
}

@Composable
private fun VnBackdrop(
    expression: CharacterExpression,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "vnBackdropMotion")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vnBackdropDrift"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = when (expression) {
            CharacterExpression.HAPPY -> 0.18f
            CharacterExpression.TALKING -> 0.10f
            CharacterExpression.THINKING -> 0.06f
            CharacterExpression.IDLE -> 0.04f
        },
        animationSpec = tween(600),
        label = "vnBackdropGlow"
    )

    Canvas(modifier = modifier) {
        val shift = 20.dp.toPx() * drift
        val radius = 12.dp.toPx()

        // Soft glow behind character
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = glowAlpha),
                    accentColor.copy(alpha = glowAlpha * 0.3f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.minDimension * 0.6f
            ),
            radius = size.minDimension * 0.6f,
            center = Offset(size.width * 0.5f, size.height * 0.35f)
        )

        // Floating accent shapes
        drawRoundRect(
            color = accentColor.copy(alpha = 0.04f),
            topLeft = Offset(size.width * 0.06f, size.height * 0.08f + shift),
            size = Size(size.width * 0.28f, size.height * 0.06f),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = accentColor.copy(alpha = 0.035f),
            topLeft = Offset(size.width * 0.62f, size.height * 0.14f - shift),
            size = Size(size.width * 0.30f, size.height * 0.05f),
            cornerRadius = CornerRadius(radius, radius)
        )
    }
}

@Composable
private fun CharacterPortrait(
    character: ConversationCharacter,
    expression: CharacterExpression,
    isUserMoment: Boolean,
    accentColor: Color
) {
    val transition = rememberInfiniteTransition(label = "portraitMotion")

    // Idle floating
    val floatY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (expression) {
                    CharacterExpression.TALKING -> 800
                    CharacterExpression.HAPPY -> 600
                    else -> 2000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portraitFloat"
    )

    // Scale pulse for talking
    val scalePulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = when (expression) {
            CharacterExpression.TALKING -> 1.04f
            CharacterExpression.HAPPY -> 1.08f
            else -> 1f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (expression) {
                    CharacterExpression.TALKING -> 600
                    CharacterExpression.HAPPY -> 500
                    else -> 2000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portraitScale"
    )

    // Sway for thinking
    val swayX by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portraitSway"
    )

    val portraitSize = if (isUserMoment) 180.dp else 220.dp

    Box(
        contentAlignment = Alignment.Center
    ) {
        // Expression overlay behind avatar
        ExpressionOverlay(
            expression = expression,
            accentColor = accentColor,
            size = portraitSize + 60.dp
        )

        // Avatar with animated border
        Crossfade(
            targetState = character.id,
            animationSpec = tween(400),
            label = "portraitCrossfade"
        ) { characterId ->
            val currentCharacter = remember(characterId) { character }
            Surface(
                modifier = Modifier
                    .size(portraitSize)
                    .graphicsLayer {
                        translationY = floatY
                        translationX = if (expression == CharacterExpression.THINKING) swayX else 0f
                        scaleX = scalePulse
                        scaleY = scalePulse
                    },
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.12f),
                border = BorderStroke(
                    width = when (expression) {
                        CharacterExpression.HAPPY -> 4.dp
                        CharacterExpression.TALKING -> 3.dp
                        else -> 2.dp
                    },
                    brush = Brush.linearGradient(
                        colors = when (expression) {
                            CharacterExpression.HAPPY -> listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500),
                                Color(0xFFFF6B35)
                            )
                            CharacterExpression.TALKING -> listOf(
                                accentColor,
                                accentColor.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.6f)
                            )
                            CharacterExpression.THINKING -> listOf(
                                Color(0xFF9C88FF),
                                Color(0xFF7C4DFF),
                                Color(0xFFB388FF)
                            )
                            else -> listOf(
                                accentColor.copy(alpha = 0.3f),
                                accentColor.copy(alpha = 0.15f)
                            )
                        }
                    )
                ),
                shadowElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = currentCharacter.avatarRes),
                    contentDescription = currentCharacter.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Character name badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 14.dp),
            shape = RoundedCornerShape(20.dp),
            color = accentColor.copy(alpha = 0.88f),
            contentColor = Color.White,
            shadowElevation = 4.dp
        ) {
            Text(
                text = character.name,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ExpressionOverlay(
    expression: CharacterExpression,
    accentColor: Color,
    size: Dp
) {
    val transition = rememberInfiniteTransition(label = "expressionOverlay")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "expressionPhase"
    )
    val sparkleAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleAlpha"
    )

    when (expression) {
        CharacterExpression.HAPPY -> {
            Canvas(modifier = Modifier.size(size)) {
                val center = this.center
                val radius = this.size.minDimension * 0.42f
                val starCount = 8
                repeat(starCount) { i ->
                    val angle = Math.toRadians((phase + i * (360f / starCount)).toDouble())
                    val x = center.x + radius * cos(angle).toFloat()
                    val y = center.y + radius * sin(angle).toFloat()
                    val starSize = 4.dp.toPx() * sparkleAlpha
                    drawCircle(
                        color = Color(0xFFFFD700).copy(alpha = sparkleAlpha * 0.7f),
                        radius = starSize,
                        center = Offset(x, y)
                    )
                }
            }
        }
        CharacterExpression.THINKING -> {
            Canvas(modifier = Modifier.size(size)) {
                val center = this.center
                val bubbleCount = 3
                repeat(bubbleCount) { i ->
                    val yOff = -20.dp.toPx() - i * 18.dp.toPx()
                    val xOff = 30.dp.toPx() + i * 8.dp.toPx()
                    val bubbleSize = (6.dp.toPx() + i * 3.dp.toPx()) * sparkleAlpha
                    drawCircle(
                        color = Color(0xFF9C88FF).copy(alpha = sparkleAlpha * 0.5f),
                        radius = bubbleSize,
                        center = Offset(center.x + xOff, center.y + yOff)
                    )
                }
            }
        }
        CharacterExpression.TALKING -> {
            Canvas(modifier = Modifier.size(size)) {
                val center = this.center
                // Sound wave arcs
                repeat(3) { i ->
                    val waveRadius = 30.dp.toPx() + i * 12.dp.toPx()
                    val alpha = (0.3f - i * 0.08f) * sparkleAlpha
                    drawCircle(
                        color = accentColor.copy(alpha = alpha.coerceAtLeast(0.05f)),
                        radius = waveRadius,
                        center = Offset(center.x + 50.dp.toPx(), center.y),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2.dp.toPx()
                        )
                    )
                }
            }
        }
        CharacterExpression.IDLE -> { /* No overlay */ }
    }
}

@Composable
private fun VnDialogueBox(
    message: ConversationMessage?,
    isTyping: Boolean,
    typingCharacter: ConversationCharacter,
    speaker: EnglishTextSpeaker,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.22f)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isTyping) {
                // Typing state
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterAvatar(character = typingCharacter, size = 28.dp)
                    Text(
                        text = typingCharacter.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
                Row(
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TypingDots()
                    Text(
                        text = "sedang mengetik...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (message != null) {
                // Message display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterAvatar(character = message.character, size = 28.dp)
                    Text(
                        text = message.character.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (message.isUser) MaterialTheme.colorScheme.primary else accentColor,
                        modifier = Modifier.weight(1f)
                    )
                    if (!message.isUser) {
                        Text(
                            text = message.character.role,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SpeakerIconButton(
                        text = message.english,
                        contentDescription = "Dengarkan kalimat",
                        speaker = speaker
                    )
                }

                AnimatedContent(
                    targetState = message.id,
                    transitionSpec = {
                        (fadeIn(tween(300)) + slideInVertically { it / 4 })
                            .togetherWith(fadeOut(tween(150)))
                    },
                    label = "dialogueContent"
                ) { messageId ->
                    val currentMsg = remember(messageId) { message }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TypewriterText(
                            text = currentMsg.english,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            enabled = !currentMsg.isUser
                        )
                        TypewriterText(
                            text = currentMsg.indonesian,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            enabled = !currentMsg.isUser
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VnChoicePanel(
    choices: List<ConversationChoice>,
    onChoiceClick: (ConversationChoice) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.70f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Pilih jawaban kamu",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            choices.forEachIndexed { index, choice ->
                AnimatedChoiceButton(
                    index = index,
                    choice = choice,
                    onClick = { onChoiceClick(choice) }
                )
            }
        }
    }
}

@Composable
private fun ChatHistoryOverlay(
    messages: List<ConversationMessage>,
    speaker: EnglishTextSpeaker,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat Chat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDismiss) {
                    Text(text = "Tutup")
                }
            }

            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada pesan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = messages,
                        key = { msg -> msg.id }
                    ) { message ->
                        HistoryChatBubble(
                            message = message,
                            speaker = speaker
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryChatBubble(
    message: ConversationMessage,
    speaker: EnglishTextSpeaker
) {
    val bubbleColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isUser) {
            CharacterAvatar(character = message.character, size = 28.dp)
            Spacer(modifier = Modifier.width(6.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(0.82f),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = if (message.isUser) 8.dp else 2.dp,
                bottomEnd = if (message.isUser) 2.dp else 8.dp
            ),
            color = bubbleColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.character.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    SpeakerIconButton(
                        text = message.english,
                        contentDescription = "Dengarkan",
                        speaker = speaker
                    )
                }
                Text(
                    text = message.english,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = message.indonesian,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(6.dp))
            CharacterAvatar(character = message.character, size = 28.dp)
        }
    }
}

@Composable
private fun ScenarioProgressHeader(
    scenario: ConversationScenario,
    scene: ConversationScene?,
    answeredSteps: Int,
    isComplete: Boolean
) {
    val maxSteps = scenario.steps.size.coerceAtLeast(1)
    val progress = if (isComplete) {
        1f
    } else {
        answeredSteps.toFloat() / maxSteps.toFloat()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, (scene?.accentColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.20f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (scene != null) {
                    SceneMiniArtwork(
                        scene = scene,
                        characters = listOf(scenario.partner, ConversationScenarioLibrary.userCharacter),
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    CharacterAvatar(
                        character = scenario.partner,
                        size = 42.dp
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scene?.title ?: scenario.partner.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${scenario.partner.name}: ${scenario.goal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${answeredSteps.coerceAtMost(scenario.steps.size)} / ${scenario.steps.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 8.dp)
            )
        }
    }
}

@Composable
private fun AnimatedChatBubble(
    message: ConversationMessage,
    speaker: EnglishTextSpeaker,
    animateText: Boolean
) {
    var visible by remember(message.id) { mutableStateOf(false) }

    LaunchedEffect(message.id) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + slideInHorizontally(
            animationSpec = tween(260, easing = FastOutSlowInEasing),
            initialOffsetX = { width ->
                if (message.isUser) width / 4 else -width / 4
            }
        )
    ) {
        ChatBubble(
            message = message,
            speaker = speaker,
            animateText = animateText
        )
    }
}

@Composable
private fun ChatBubble(
    message: ConversationMessage,
    speaker: EnglishTextSpeaker,
    animateText: Boolean
) {
    val bubbleColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            CharacterAvatar(character = message.character, size = 34.dp)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(0.82f),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = if (message.isUser) 8.dp else 2.dp,
                bottomEnd = if (message.isUser) 2.dp else 8.dp
            ),
            color = bubbleColor,
            contentColor = contentColor,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = message.character.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = contentColor.copy(alpha = 0.78f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = message.character.role,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.62f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    SpeakerIconButton(
                        text = message.english,
                        contentDescription = "Dengarkan kalimat",
                        speaker = speaker
                    )
                }
                TypewriterText(
                    text = message.english,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    enabled = animateText
                )
                TypewriterText(
                    text = message.indonesian,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.78f),
                    enabled = animateText
                )
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            CharacterAvatar(character = message.character, size = 34.dp)
        }
    }
}

@Composable
private fun TypingIndicator(
    character: ConversationCharacter
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        CharacterAvatar(character = character, size = 34.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(0.62f),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = 2.dp,
                bottomEnd = 8.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TypingDots()
                Text(
                    text = "${character.name} sedang mengetik...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "typingDots")
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val yOffset by transition.animateFloat(
                initialValue = 0f,
                targetValue = -5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 420,
                        delayMillis = index * 110,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "typingDot$index"
            )
            Surface(
                modifier = Modifier
                    .size(7.dp)
                    .offset(y = yOffset.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {}
        }
    }
}

@Composable
private fun GlassReplyDock(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier.fillMaxWidth(),
        enter = fadeIn(tween(180)) + expandVertically(
            animationSpec = tween(220, easing = FastOutSlowInEasing),
            expandFrom = Alignment.Bottom
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.26f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.48f)
                        )
                    )
                )
                .padding(top = 32.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ChoicePanel(
    choices: List<ConversationChoice>,
    onChoiceClick: (ConversationChoice) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.70f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Pilih jawaban kamu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            choices.forEachIndexed { index, choice ->
                AnimatedChoiceButton(
                    index = index,
                    choice = choice,
                    onClick = { onChoiceClick(choice) }
                )
            }
        }
    }
}

@Composable
private fun AnimatedChoiceButton(
    index: Int,
    choice: ConversationChoice,
    onClick: () -> Unit
) {
    var visible by remember(choice.english) { mutableStateOf(false) }

    LaunchedEffect(choice.english) {
        delay(index * 90L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)) + slideInHorizontally(
            animationSpec = tween(220, easing = FastOutSlowInEasing),
            initialOffsetX = { width -> width / 5 }
        )
    ) {
        ChoiceButton(
            index = index,
            choice = choice,
            onClick = onClick
        )
    }
}

@Composable
private fun ChoiceButton(
    index: Int,
    choice: ConversationChoice,
    onClick: () -> Unit
) {
    var pressed by remember(choice.english) { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "choiceScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                onClick = {
                    pressed = true
                    onClick()
                }
            ),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (index == 0) 0.62f else 0.46f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = ('A' + index).toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = choice.english,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = choice.indonesian,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
private fun CompletionPanel(
    onRestartClick: () -> Unit,
    onBackToScenariosClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.68f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.28f)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Skenario selesai",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kamu sudah menyelesaikan chat pilihan ini.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRestartClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ulangi")
                }
                OutlinedButton(
                    onClick = onBackToScenariosClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Skenario")
                }
            }
        }
    }
}

@Composable
private fun ConversationBackdrop(
    scene: ConversationScene?,
    modifier: Modifier = Modifier
) {
    val accent = scene?.accentColor ?: MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val transition = rememberInfiniteTransition(label = "conversationBackdropMotion")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "conversationBackdropDrift"
    )

    Canvas(modifier = modifier) {
        val radius = 8.dp.toPx()
        val shift = 14.dp.toPx() * drift
        drawRoundRect(
            color = accent.copy(alpha = 0.055f),
            topLeft = Offset(size.width * 0.04f, size.height * 0.03f + shift),
            size = Size(size.width * 0.68f, size.height * 0.18f),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = surface.copy(alpha = 0.26f),
            topLeft = Offset(size.width * 0.28f, size.height * 0.24f - shift),
            size = Size(size.width * 0.64f, size.height * 0.12f),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = accent.copy(alpha = 0.045f),
            topLeft = Offset(size.width * 0.10f, size.height * 0.76f - shift),
            size = Size(size.width * 0.82f, size.height * 0.16f),
            cornerRadius = CornerRadius(radius, radius)
        )
    }
}

@Composable
private fun HeroAvatarStack(
    characters: List<ConversationCharacter>,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "heroAvatarMotion")
    Box(
        modifier = modifier.heightIn(min = 58.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        characters.forEachIndexed { index, character ->
            val bob by transition.animateFloat(
                initialValue = 0f,
                targetValue = if (index % 2 == 0) -5f else 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1400 + index * 120,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "heroAvatarBob$index"
            )
            Surface(
                modifier = Modifier
                    .offset(x = (index * 28).dp, y = bob.dp)
                    .size(48.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.26f),
                border = BorderStroke(2.dp, Color.White.copy(alpha = 0.72f))
            ) {
                CharacterAvatar(
                    character = character,
                    size = 48.dp
                )
            }
        }
    }
}

@Composable
private fun HeroStatPill(
    value: String,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.20f),
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.82f)
            )
        }
    }
}

@Composable
private fun SceneMiniArtwork(
    scene: ConversationScene,
    characters: List<ConversationCharacter>,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "sceneArtworkMotion${scene.id}")
    val lift by transition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sceneArtworkLift${scene.id}"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        scene.accentColor.copy(alpha = 0.96f),
                        scene.accentColor.copy(alpha = 0.56f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.54f)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = 8.dp.toPx()
            drawRoundRect(
                color = Color.White.copy(alpha = 0.24f),
                topLeft = Offset(size.width * 0.18f, size.height * 0.20f),
                size = Size(size.width * 0.64f, size.height * 0.20f),
                cornerRadius = CornerRadius(radius, radius)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.16f),
                topLeft = Offset(size.width * 0.10f, size.height * 0.54f),
                size = Size(size.width * 0.80f, size.height * 0.22f),
                cornerRadius = CornerRadius(radius, radius)
            )
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.08f),
                topLeft = Offset(size.width * 0.18f, size.height * 0.82f),
                size = Size(size.width * 0.64f, size.height * 0.08f),
                cornerRadius = CornerRadius(radius, radius)
            )
        }

        Text(
            text = scene.badge,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .graphicsLayer {
                    translationY = lift
                },
            horizontalArrangement = Arrangement.spacedBy((-8).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            characters.take(2).forEach { character ->
                Surface(
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.28f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f))
                ) {
                    CharacterAvatar(
                        character = character,
                        size = 30.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenarioPreviewStrip(
    scenario: ConversationScenario,
    scene: ConversationScene?
) {
    val accent = scene?.accentColor ?: MaterialTheme.colorScheme.primary
    val transition = rememberInfiniteTransition(label = "scenarioPreviewMotion${scenario.id}")
    val nudge by transition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scenarioPreviewNudge${scenario.id}"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = accent.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.16f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(58.dp),
                contentAlignment = Alignment.Center
            ) {
                CharacterAvatar(
                    character = ConversationScenarioLibrary.userCharacter,
                    size = 34.dp
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(38.dp)
                        .offset(x = nudge.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(2.dp, accent.copy(alpha = 0.48f))
                ) {
                    CharacterAvatar(
                        character = scenario.partner,
                        size = 38.dp
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Chat dengan ${scenario.partner.name}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
                Text(
                    text = "${scenario.steps.size} giliran pilihan, 2 jawaban tiap langkah",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.TipsAndUpdates,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ScenarioProgressBadge(
    progress: ConversationScenarioProgressEntity
) {
    val containerColor = if (progress.isCompleted) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.56f)
    }
    val contentColor = if (progress.isCompleted) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }
    val text = if (progress.isCompleted) {
        "Selesai ${progress.completedCount}x • ${progress.choiceCount} jawaban"
    } else {
        "Progress ${progress.completedSteps}/${progress.totalSteps} • lanjutkan kapan saja"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TypewriterText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    enabled: Boolean = false
) {
    var visibleCount by remember(text, enabled) {
        mutableStateOf(if (enabled) 0 else text.length)
    }

    LaunchedEffect(text, enabled) {
        if (!enabled) {
            visibleCount = text.length
            return@LaunchedEffect
        }

        visibleCount = 0
        text.indices.forEach { index ->
            delay(28)
            visibleCount = index + 1
        }
    }

    Text(
        text = text.take(visibleCount),
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = fontWeight
    )
}

@Composable
private fun CharacterAvatar(
    character: ConversationCharacter,
    size: Dp
) {
    Image(
        painter = painterResource(id = character.avatarRes),
        contentDescription = character.name,
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
