package com.example.vocabkid.presentation.conversation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.local.entity.ConversationScenarioProgressEntity
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.ConversationPracticeStats
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ConversationScreenMode {
    SCENE_LIST,
    SCENARIO_LIST,
    CHAT
}

enum class CharacterExpression {
    IDLE,
    TALKING,
    THINKING,
    HAPPY
}

data class ConversationMessage(
    val id: String,
    val character: ConversationCharacter,
    val english: String,
    val indonesian: String,
    val isUser: Boolean
)

data class ConversationUiState(
    val mode: ConversationScreenMode = ConversationScreenMode.SCENE_LIST,
    val scenes: List<ConversationScene> = ConversationScenarioLibrary.scenes,
    val scenarioCount: Int = ConversationScenarioLibrary.scenarios.size,
    val selectedScene: ConversationScene? = null,
    val selectedScenario: ConversationScenario? = null,
    val messages: List<ConversationMessage> = emptyList(),
    val activeChoices: List<ConversationChoice> = emptyList(),
    val stepIndex: Int = 0,
    val isComplete: Boolean = false,
    val isPartnerTyping: Boolean = false,
    val typingCharacter: ConversationCharacter? = null,
    val currentExpression: CharacterExpression = CharacterExpression.IDLE,
    val displayedCharacter: ConversationCharacter? = null,
    val currentDialogue: ConversationMessage? = null,
    val showUserMoment: Boolean = false
)

class ConversationViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {
    val progressRecords: StateFlow<List<ConversationScenarioProgressEntity>> =
        repository.observeConversationScenarioProgress()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val practiceStats: StateFlow<ConversationPracticeStats> =
        repository.observeConversationPracticeStats()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConversationPracticeStats())

    var uiState by mutableStateOf(ConversationUiState())
        private set

    private var messageCounter = 0
    private var revealJob: Job? = null

    fun selectScene(scene: ConversationScene) {
        revealJob?.cancel()
        uiState = uiState.copy(
            mode = ConversationScreenMode.SCENARIO_LIST,
            selectedScene = scene,
            selectedScenario = null,
            messages = emptyList(),
            activeChoices = emptyList(),
            stepIndex = 0,
            isComplete = false,
            isPartnerTyping = false,
            typingCharacter = null
        )
    }

    fun startScenario(scenario: ConversationScenario) {
        revealJob?.cancel()
        val scene = ConversationScenarioLibrary.sceneForId(scenario.sceneId)
        val firstStep = scenario.steps.firstOrNull()
        messageCounter = 0

        uiState = uiState.copy(
            mode = ConversationScreenMode.CHAT,
            selectedScene = scene ?: uiState.selectedScene,
            selectedScenario = scenario,
            messages = emptyList(),
            activeChoices = emptyList(),
            stepIndex = 0,
            isComplete = false,
            isPartnerTyping = false,
            typingCharacter = null,
            currentExpression = CharacterExpression.IDLE,
            displayedCharacter = scenario.partner,
            currentDialogue = null,
            showUserMoment = false
        )

        recordScenarioStarted(scenario, scene)

        revealPartnerLines(
            lines = scenario.openingLines + listOfNotNull(firstStep?.prompt),
            choicesAfter = firstStep?.choices.orEmpty(),
            completeAfter = firstStep == null
        )
    }

    fun chooseReply(choice: ConversationChoice) {
        val currentState = uiState
        val scenario = currentState.selectedScenario ?: return
        if (currentState.isComplete || currentState.isPartnerTyping) return
        if (choice !in currentState.activeChoices) return

        val choiceIndex = currentState.activeChoices.indexOf(choice).coerceAtLeast(0)
        val selectedStepIndex = currentState.stepIndex
        val nextStepIndex = currentState.stepIndex + 1
        val nextStep = scenario.steps.getOrNull(nextStepIndex)
        val scene = currentState.selectedScene

        val userMessage = choice.toUserMessage()
        uiState = currentState.copy(
            messages = currentState.messages + userMessage,
            activeChoices = emptyList(),
            stepIndex = nextStepIndex,
            isComplete = false,
            isPartnerTyping = false,
            typingCharacter = null,
            currentExpression = CharacterExpression.HAPPY,
            displayedCharacter = ConversationScenarioLibrary.userCharacter,
            currentDialogue = userMessage,
            showUserMoment = true
        )

        val partnerLines = if (nextStep != null) {
            listOf(choice.response, nextStep.prompt)
        } else {
            listOf(choice.response, scenario.closingLine)
        }

        viewModelScope.launch {
            repository.recordConversationChoice(
                scenarioId = scenario.id,
                sceneId = scenario.sceneId,
                sceneTitle = scene?.title ?: scenario.sceneId,
                scenarioTitle = scenario.title,
                partnerName = scenario.partner.name,
                totalSteps = scenario.steps.size,
                stepIndex = selectedStepIndex,
                choiceIndex = choiceIndex,
                choiceEnglish = choice.english,
                choiceIndonesian = choice.indonesian,
                responseEnglish = choice.response.english
            )

            if (nextStep == null) {
                repository.recordConversationScenarioCompleted(
                    scenarioId = scenario.id,
                    sceneId = scenario.sceneId,
                    sceneTitle = scene?.title ?: scenario.sceneId,
                    scenarioTitle = scenario.title,
                    partnerName = scenario.partner.name,
                    totalSteps = scenario.steps.size
                )
            }
        }

        revealPartnerLines(
            lines = partnerLines,
            choicesAfter = nextStep?.choices.orEmpty(),
            completeAfter = nextStep == null
        )
    }

    fun startRandomScenario(scene: ConversationScene? = uiState.selectedScene) {
        val scenarioPool = if (scene != null) {
            ConversationScenarioLibrary.scenariosForScene(scene.id)
        } else {
            ConversationScenarioLibrary.scenarios
        }

        scenarioPool.randomOrNull()?.let { scenario ->
            startScenario(scenario)
        }
    }

    fun restartCurrentScenario() {
        revealJob?.cancel()
        uiState.selectedScenario?.let { scenario ->
            startScenario(scenario)
        }
    }

    fun backToScenes() {
        revealJob?.cancel()
        uiState = uiState.copy(
            mode = ConversationScreenMode.SCENE_LIST,
            selectedScene = null,
            selectedScenario = null,
            messages = emptyList(),
            activeChoices = emptyList(),
            stepIndex = 0,
            isComplete = false,
            isPartnerTyping = false,
            typingCharacter = null
        )
    }

    fun backToScenarioList() {
        revealJob?.cancel()
        uiState = uiState.copy(
            mode = ConversationScreenMode.SCENARIO_LIST,
            selectedScenario = null,
            messages = emptyList(),
            activeChoices = emptyList(),
            stepIndex = 0,
            isComplete = false,
            isPartnerTyping = false,
            typingCharacter = null
        )
    }

    fun scenariosForSelectedScene(): List<ConversationScenario> {
        val scene = uiState.selectedScene ?: return emptyList()
        return ConversationScenarioLibrary.scenariosForScene(scene.id)
    }

    private fun recordScenarioStarted(
        scenario: ConversationScenario,
        scene: ConversationScene?
    ) {
        viewModelScope.launch {
            repository.recordConversationScenarioStarted(
                scenarioId = scenario.id,
                sceneId = scenario.sceneId,
                sceneTitle = scene?.title ?: scenario.sceneId,
                scenarioTitle = scenario.title,
                partnerName = scenario.partner.name,
                totalSteps = scenario.steps.size
            )
        }
    }

    override fun onCleared() {
        revealJob?.cancel()
        super.onCleared()
    }

    private fun revealPartnerLines(
        lines: List<ConversationLine>,
        choicesAfter: List<ConversationChoice>,
        completeAfter: Boolean
    ) {
        revealJob?.cancel()
        revealJob = viewModelScope.launch {
            // Brief user moment before switching back to partner
            if (uiState.showUserMoment) {
                delay(800)
                uiState = uiState.copy(showUserMoment = false)
            }

            lines.forEachIndexed { index, line ->
                uiState = uiState.copy(
                    isPartnerTyping = true,
                    typingCharacter = line.character,
                    currentExpression = CharacterExpression.TALKING,
                    displayedCharacter = line.character
                )
                delay(line.typingPauseMillis())
                val newMessage = line.toMessage(isUser = false)
                uiState = uiState.copy(
                    messages = uiState.messages + newMessage,
                    isPartnerTyping = false,
                    typingCharacter = null,
                    currentExpression = CharacterExpression.IDLE,
                    currentDialogue = newMessage
                )

                if (index != lines.lastIndex) {
                    delay(520)
                }
            }

            uiState = uiState.copy(
                activeChoices = choicesAfter,
                isComplete = completeAfter,
                isPartnerTyping = false,
                typingCharacter = null,
                currentExpression = if (choicesAfter.isNotEmpty()) {
                    CharacterExpression.THINKING
                } else if (completeAfter) {
                    CharacterExpression.HAPPY
                } else {
                    CharacterExpression.IDLE
                }
            )
        }
    }

    private fun ConversationLine.typingPauseMillis(): Long {
        val estimated = 980L + (english.length * 24L)
        return estimated.coerceIn(1_350L, 2_900L)
    }

    private fun ConversationLine.toMessage(isUser: Boolean): ConversationMessage {
        messageCounter += 1
        return ConversationMessage(
            id = "message_$messageCounter",
            character = character,
            english = english,
            indonesian = indonesian,
            isUser = isUser
        )
    }

    private fun ConversationChoice.toUserMessage(): ConversationMessage {
        messageCounter += 1
        val character = ConversationScenarioLibrary.userCharacter
        return ConversationMessage(
            id = "message_$messageCounter",
            character = character,
            english = english,
            indonesian = indonesian,
            isUser = true
        )
    }
}
