package com.example.vocabkid.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.presentation.home.HomeScreen
import com.example.vocabkid.presentation.home.HomeViewModel
import com.example.vocabkid.presentation.onboarding.OnboardingScreen
import com.example.vocabkid.presentation.onboarding.OnboardingViewModel
import com.example.vocabkid.presentation.progress.ProgressScreen
import com.example.vocabkid.presentation.progress.ProgressViewModel
import com.example.vocabkid.presentation.quiz.QuizScreen
import com.example.vocabkid.presentation.quiz.QuizViewModel
import com.example.vocabkid.presentation.study.StudyScreen
import com.example.vocabkid.presentation.study.StudyViewModel
import com.example.vocabkid.presentation.vocabulary.VocabularyDetailScreen
import com.example.vocabkid.presentation.vocabulary.VocabularyScreen
import com.example.vocabkid.presentation.vocabulary.VocabularyViewModel

@Composable
fun VocabKidNavHost(repository: VocabKidRepository) {
    val navController = rememberNavController()
    val factory = remember(repository) { VocabKidViewModelFactory(repository) }
    val student by repository.observeStudent().collectAsStateWithLifecycle(initialValue = null)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var isSeedReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.seedInitialVocabularyIfNeeded()
        isSeedReady = true
    }

    LaunchedEffect(student?.id, isSeedReady, currentBackStackEntry?.destination?.route) {
        val currentRoute = currentBackStackEntry?.destination?.route
        if (isSeedReady && student != null && currentRoute == Routes.ONBOARDING) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.ONBOARDING) { inclusive = true }
            }
        }
    }

    if (!isSeedReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARDING
    ) {
        composable(Routes.ONBOARDING) {
            val viewModel: OnboardingViewModel = viewModel(factory = factory)
            OnboardingScreen(
                viewModel = viewModel,
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = viewModel,
                onStudyClick = { navController.navigate(Routes.STUDY) },
                onQuizClick = { navController.navigate(Routes.QUIZ) },
                onVocabularyClick = { navController.navigate(Routes.VOCABULARY) },
                onProgressClick = { navController.navigate(Routes.PROGRESS) }
            )
        }

        composable(Routes.STUDY) {
            val viewModel: StudyViewModel = viewModel(factory = factory)
            StudyScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.QUIZ) {
            val viewModel: QuizViewModel = viewModel(factory = factory)
            QuizScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.VOCABULARY) {
            val viewModel: VocabularyViewModel = viewModel(factory = factory)
            VocabularyScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onDetailClick = { wordId ->
                    navController.navigate(Routes.vocabularyDetail(wordId))
                }
            )
        }

        composable(
            route = Routes.VOCABULARY_DETAIL,
            arguments = listOf(
                navArgument("wordId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getLong("wordId") ?: 0L
            val viewModel: VocabularyViewModel = viewModel(factory = factory)
            VocabularyDetailScreen(
                wordId = wordId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.PROGRESS) {
            val viewModel: ProgressViewModel = viewModel(factory = factory)
            ProgressScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
