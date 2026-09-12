package com.example.vocabkid.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vocabkid.presentation.home.HomeScreen
import com.example.vocabkid.presentation.home.HomeViewModel
import com.example.vocabkid.presentation.progress.ProgressScreen
import com.example.vocabkid.presentation.progress.ProgressViewModel
import com.example.vocabkid.presentation.quiz.QuizScreen
import com.example.vocabkid.presentation.quiz.QuizViewModel
import com.example.vocabkid.presentation.study.StudyScreen
import com.example.vocabkid.presentation.study.StudyViewModel
import com.example.vocabkid.presentation.vocabulary.VocabularyScreen
import com.example.vocabkid.presentation.vocabulary.VocabularyViewModel

@Composable
fun MainTabsContent(
    factory: VocabKidViewModelFactory,
    selectedRoute: String,
    onSelectedRouteChange: (String) -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onPronunciationClick: () -> Unit,
    bottomContentPadding: Dp,
    onProfileClick: () -> Unit,
    onStoryClick: () -> Unit,
    onVocabularyDetailClick: (Long) -> Unit,
    onConversationClick: () -> Unit,
    onArcadeClick: () -> Unit = {}
) {
    BackHandler(enabled = selectedRoute != Routes.HOME) {
        onSelectedRouteChange(Routes.HOME)
    }

    Crossfade(
        targetState = selectedRoute,
        animationSpec = tween(durationMillis = 180),
        label = "mainTabContent"
    ) { route ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (route) {
                Routes.STUDY -> {
                    val viewModel: StudyViewModel = viewModel(factory = factory)
                    StudyScreen(
                        viewModel = viewModel,
                        bottomContentPadding = bottomContentPadding
                    )
                }

                Routes.QUIZ -> {
                    val viewModel: QuizViewModel = viewModel(factory = factory)
                    QuizScreen(
                        viewModel = viewModel,
                        bottomContentPadding = bottomContentPadding
                    )
                }

                Routes.VOCABULARY -> {
                    val viewModel: VocabularyViewModel = viewModel(factory = factory)
                    VocabularyScreen(
                        viewModel = viewModel,
                        bottomContentPadding = bottomContentPadding,
                        onDetailClick = onVocabularyDetailClick
                    )
                }

                Routes.PROGRESS -> {
                    val viewModel: ProgressViewModel = viewModel(factory = factory)
                    ProgressScreen(
                        viewModel = viewModel,
                        bottomContentPadding = bottomContentPadding,
                        onStudyClick = { onSelectedRouteChange(Routes.STUDY) }
                    )
                }

                else -> {
                    val viewModel: HomeViewModel = viewModel(factory = factory)
                    HomeScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = onToggleDarkTheme,
                        onPronunciationClick = onPronunciationClick,
                        bottomContentPadding = bottomContentPadding,
                        onProfileClick = onProfileClick,
                        onStoryClick = onStoryClick,
                        onStudyClick = { onSelectedRouteChange(Routes.STUDY) },
                        onConversationClick = onConversationClick,
                        onArcadeClick = onArcadeClick
                    )
                }
            }
        }
    }
}
