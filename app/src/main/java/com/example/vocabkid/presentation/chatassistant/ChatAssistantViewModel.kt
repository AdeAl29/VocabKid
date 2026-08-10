package com.example.vocabkid.presentation.chatassistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.remote.GeminiChatService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─── Data Models ────────────────────────────────────────────────────────────────

enum class ChatRole {
    USER,
    ASSISTANT
}

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

data class ChatAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isOverlayVisible: Boolean = false,
    val errorMessage: String? = null
)

// ─── Suggestion Chips ───────────────────────────────────────────────────────────

data class SuggestionChip(
    val label: String,
    val query: String
)

val defaultSuggestions = listOf(
    SuggestionChip("🍎 Arti 'apple'?", "Apa arti kata 'apple'? Berikan contoh kalimatnya juga."),
    SuggestionChip("📚 Cara pakai Flashcard", "Bagaimana cara menggunakan fitur Study / Flashcard di VocabKid?"),
    SuggestionChip("💡 Tips belajar vocab", "Berikan tips efektif untuk menghafal kosakata bahasa Inggris"),
    SuggestionChip("❓ Fitur Quiz", "Jelaskan cara menggunakan fitur Quiz di VocabKid"),
    SuggestionChip("🗣️ Latihan pronunciation", "Bagaimana cara menggunakan fitur Pronunciation di VocabKid?"),
    SuggestionChip("💬 Latihan conversation", "Jelaskan tentang fitur Conversation Practice di VocabKid")
)

// ─── ViewModel ──────────────────────────────────────────────────────────────────

class ChatAssistantViewModel : ViewModel() {

    private val chatService = GeminiChatService()

    private val _uiState = MutableStateFlow(ChatAssistantUiState())
    val uiState: StateFlow<ChatAssistantUiState> = _uiState.asStateFlow()

    private var messageCounter = 0
    private var streamingJob: Job? = null

    val isApiKeyConfigured: Boolean
        get() = chatService.isApiKeyConfigured

    fun toggleOverlay() {
        _uiState.update { it.copy(isOverlayVisible = !it.isOverlayVisible) }
        // Add welcome message on first open
        if (_uiState.value.isOverlayVisible && _uiState.value.messages.isEmpty()) {
            addWelcomeMessage()
        }
    }

    fun showOverlay() {
        _uiState.update { it.copy(isOverlayVisible = true) }
        if (_uiState.value.messages.isEmpty()) {
            addWelcomeMessage()
        }
    }

    fun hideOverlay() {
        _uiState.update { it.copy(isOverlayVisible = false) }
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _uiState.value.isLoading) return

        // Add user message
        val userMessage = ChatMessage(
            id = nextMessageId(),
            role = ChatRole.USER,
            text = trimmed
        )
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                isLoading = true,
                errorMessage = null
            )
        }

        // Create placeholder for assistant response
        val assistantMessageId = nextMessageId()
        val placeholderMessage = ChatMessage(
            id = assistantMessageId,
            role = ChatRole.ASSISTANT,
            text = "",
            isStreaming = true
        )
        _uiState.update {
            it.copy(messages = it.messages + placeholderMessage)
        }

        // Stream response from Gemini
        streamingJob?.cancel()
        streamingJob = viewModelScope.launch {
            chatService.sendMessage(trimmed).collect { partialText ->
                _uiState.update { state ->
                    val updatedMessages = state.messages.map { msg ->
                        if (msg.id == assistantMessageId) {
                            msg.copy(text = partialText, isStreaming = true)
                        } else msg
                    }
                    state.copy(messages = updatedMessages)
                }
            }

            // Mark streaming as complete
            _uiState.update { state ->
                val updatedMessages = state.messages.map { msg ->
                    if (msg.id == assistantMessageId) {
                        msg.copy(isStreaming = false)
                    } else msg
                }
                state.copy(
                    messages = updatedMessages,
                    isLoading = false
                )
            }
        }
    }

    fun clearChat() {
        streamingJob?.cancel()
        chatService.resetChat()
        messageCounter = 0
        _uiState.update {
            it.copy(
                messages = emptyList(),
                isLoading = false,
                errorMessage = null
            )
        }
        addWelcomeMessage()
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        streamingJob?.cancel()
        super.onCleared()
    }

    private fun addWelcomeMessage() {
        val welcome = ChatMessage(
            id = nextMessageId(),
            role = ChatRole.ASSISTANT,
            text = "Halo! 👋 Aku **VocabKid Assistant**.\n\n" +
                "Aku bisa membantu kamu:\n" +
                "• 📖 Menjelaskan arti kata bahasa Inggris\n" +
                "• 💡 Memberikan contoh kalimat\n" +
                "• 🎯 Panduan menggunakan fitur VocabKid\n" +
                "• 📝 Tips belajar kosakata\n\n" +
                "Mau tanya apa? 😊"
        )
        _uiState.update {
            it.copy(messages = it.messages + welcome)
        }
    }

    private fun nextMessageId(): String {
        messageCounter++
        return "chat_msg_$messageCounter"
    }
}
