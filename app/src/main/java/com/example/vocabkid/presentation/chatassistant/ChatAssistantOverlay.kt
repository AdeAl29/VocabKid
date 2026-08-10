package com.example.vocabkid.presentation.chatassistant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// ─── Main Overlay Composable ────────────────────────────────────────────────────

@Composable
fun ChatAssistantOverlay(
    viewModel: ChatAssistantViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        // Overlay panel
        AnimatedVisibility(
            visible = uiState.isOverlayVisible,
            enter = fadeIn(tween(200)) + slideInVertically(tween(350, easing = FastOutSlowInEasing)) { it / 3 },
            exit = fadeOut(tween(150)) + slideOutVertically(tween(250)) { it / 3 }
        ) {
            ChatOverlayPanel(
                messages = uiState.messages,
                isLoading = uiState.isLoading,
                onSendMessage = viewModel::sendMessage,
                onClearChat = viewModel::clearChat,
                onClose = viewModel::hideOverlay
            )
        }

        // FAB — hidden when overlay is open
        AnimatedVisibility(
            visible = !uiState.isOverlayVisible,
            enter = scaleIn(tween(250)) + fadeIn(tween(200)),
            exit = scaleOut(tween(200)) + fadeOut(tween(150)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 120.dp)
        ) {
            ChatFab(onClick = viewModel::showOverlay)
        }
    }
}

// ─── Floating Action Button ─────────────────────────────────────────────────────

@Composable
private fun ChatFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fabPulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabPulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabGlowAlpha"
    )

    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size(66.dp)
                .scale(pulseScale * 1.12f)
                .clip(CircleShape)
                .background(primary.copy(alpha = glowAlpha * 0.3f))
        )

        // Main FAB
        Surface(
            modifier = Modifier
                .size(58.dp)
                .scale(pulseScale)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = primary.copy(alpha = 0.4f),
                    spotColor = primary.copy(alpha = 0.5f)
                ),
            shape = CircleShape,
            color = primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.SmartToy,
                    contentDescription = "Buka Chat Assistant",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ─── Chat Overlay Panel ─────────────────────────────────────────────────────────

@Composable
private fun ChatOverlayPanel(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onClose: () -> Unit
) {
    val listState = rememberLazyListState()

    // Auto-scroll to latest message
    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* block clicks behind overlay */ }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 8.dp)
                .imePadding(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
            shadowElevation = 24.dp,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                ChatHeader(
                    onClose = onClose,
                    onClearChat = onClearChat
                )

                // Messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        ChatBubble(message = message)
                    }

                    // Typing indicator
                    if (isLoading) {
                        item(key = "typing_indicator") {
                            TypingIndicator()
                        }
                    }

                    // Suggestion chips after welcome or when no user messages yet
                    val hasUserMessages = messages.any { it.role == ChatRole.USER }
                    if (!hasUserMessages && !isLoading) {
                        item(key = "suggestions") {
                            SuggestionChipsRow(
                                onChipClick = onSendMessage
                            )
                        }
                    }
                }

                // Input area
                ChatInputBar(
                    isLoading = isLoading,
                    onSendMessage = onSendMessage
                )
            }
        }
    }
}

// ─── Header ─────────────────────────────────────────────────────────────────────

@Composable
private fun ChatHeader(
    onClose: () -> Unit,
    onClearChat: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.10f),
                            primary.copy(alpha = 0.04f)
                        )
                    )
                )
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Avatar + Title
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = primary.copy(alpha = 0.14f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VocabKid Assistant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Powered by Gemini AI ✨",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Clear chat button
                IconButton(
                    onClick = onClearChat,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = "Hapus percakapan",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// ─── Chat Bubble ────────────────────────────────────────────────────────────────

@Composable
private fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == ChatRole.USER

    val bubbleEnterAnim = remember { Animatable(0f) }
    LaunchedEffect(message.id) {
        bubbleEnterAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(bubbleEnterAnim.value)
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        // AI Avatar for assistant messages
        if (!isUser) {
            Surface(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Bottom),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Surface(
            modifier = Modifier.widthIn(max = 290.dp),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 6.dp,
                bottomEnd = if (isUser) 6.dp else 18.dp
            ),
            color = if (isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            },
            contentColor = if (isUser) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            border = if (!isUser) {
                BorderStroke(
                    0.5.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )
            } else null
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
            ) {
                if (!isUser && message.text.isNotBlank()) {
                    FormattedMessageText(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = message.text.ifBlank { "..." },
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }

                if (message.isStreaming) {
                    Spacer(modifier = Modifier.height(4.dp))
                    StreamingCursor()
                }
            }
        }
    }
}

/**
 * Simple markdown-like formatting:
 * - **bold** text
 * - Bullet points (• and -)
 * - Line breaks
 */
@Composable
private fun FormattedMessageText(
    text: String,
    color: Color
) {
    // Simple rendering — full markdown not needed for chat context
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        lineHeight = 21.sp
    )
}

// ─── Streaming Cursor ───────────────────────────────────────────────────────────

@Composable
private fun StreamingCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Box(
        modifier = Modifier
            .width(8.dp)
            .height(14.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}

// ─── Typing Indicator ───────────────────────────────────────────────────────────

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
        modifier = Modifier.padding(start = 36.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val delay = index * 180
            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 450,
                        delayMillis = delay,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "typingDot$index"
            )

            Box(
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

// ─── Suggestion Chips ───────────────────────────────────────────────────────────

@Composable
private fun SuggestionChipsRow(
    onChipClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp, top = 6.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Coba tanya:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 12.dp)
        ) {
            items(defaultSuggestions) { chip ->
                SuggestionChipItem(
                    chip = chip,
                    onClick = { onChipClick(chip.query) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionChipItem(
    chip: SuggestionChip,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Text(
            text = chip.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── Input Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun ChatInputBar(
    isLoading: Boolean,
    onSendMessage: (String) -> Unit
) {
    var inputText by rememberSaveable { mutableStateOf("") }

    val sendEnabled = inputText.isNotBlank() && !isLoading

    val sendButtonScale by animateFloatAsState(
        targetValue = if (sendEnabled) 1f else 0.8f,
        animationSpec = tween(200),
        label = "sendScale"
    )

    val sendButtonRotation by animateFloatAsState(
        targetValue = if (sendEnabled) 0f else 15f,
        animationSpec = tween(200),
        label = "sendRotation"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Text field
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = if (isLoading) "Menunggu jawaban..." else "Ketik pertanyaan...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                enabled = !isLoading,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (sendEnabled) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                maxLines = 4
            )

            // Send button
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .scale(sendButtonScale),
                shape = CircleShape,
                color = if (sendEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                contentColor = if (sendEnabled) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                },
                onClick = {
                    if (sendEnabled) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(sendButtonRotation)
                    )
                }
            }
        }
    }
}
