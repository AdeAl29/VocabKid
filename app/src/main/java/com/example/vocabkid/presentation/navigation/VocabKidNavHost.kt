package com.example.vocabkid.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.presentation.auth.AuthScreen
import com.example.vocabkid.presentation.auth.AuthViewModel
import com.example.vocabkid.presentation.conversation.ConversationScreen
import com.example.vocabkid.presentation.conversation.ConversationViewModel
import com.example.vocabkid.presentation.pronunciation.PronunciationScreen
import com.example.vocabkid.presentation.pronunciation.PronunciationViewModel
import com.example.vocabkid.presentation.home.HomeScreen
import com.example.vocabkid.presentation.home.HomeViewModel
import com.example.vocabkid.presentation.onboarding.OnboardingScreen
import com.example.vocabkid.presentation.onboarding.OnboardingViewModel
import com.example.vocabkid.presentation.components.SkyThemeBackground
import com.example.vocabkid.presentation.profile.ProfileScreen
import com.example.vocabkid.presentation.profile.ProfileViewModel
import com.example.vocabkid.presentation.progress.ProgressScreen
import com.example.vocabkid.presentation.progress.ProgressViewModel
import com.example.vocabkid.presentation.quiz.QuizScreen
import com.example.vocabkid.presentation.quiz.QuizViewModel
import com.example.vocabkid.presentation.splash.SplashScreen
import com.example.vocabkid.presentation.study.StudyScreen
import com.example.vocabkid.presentation.study.StudyViewModel
import com.example.vocabkid.presentation.vocabulary.VocabularyDetailScreen
import com.example.vocabkid.presentation.vocabulary.VocabularyScreen
import com.example.vocabkid.presentation.vocabulary.VocabularyViewModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun VocabKidNavHost(
    repository: VocabKidRepository,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    isMusicEnabled: Boolean,
    onToggleMusic: () -> Unit,
    isLogoutConfirmationEnabled: Boolean,
    onDisableLogoutConfirmation: () -> Unit
) {
    val navController = rememberNavController()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val factory = remember(repository, firebaseAuth) {
        VocabKidViewModelFactory(repository, firebaseAuth)
    }
    val coroutineScope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    var selectedMainRoute by rememberSaveable { mutableStateOf(Routes.HOME) }
    var startDestination by remember { mutableStateOf<String?>(null) }
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var isLogoutDialogOpen by rememberSaveable { mutableStateOf(false) }
    var shouldSkipNextLogoutConfirmation by rememberSaveable { mutableStateOf(false) }
    val showBottomBar = startDestination != null && currentRoute == Routes.MAIN_TABS && !showSplash
    val bottomContentPadding = if (showBottomBar) 104.dp else 0.dp
    val performLogout: () -> Unit = {
        firebaseAuth.signOut()
        coroutineScope.launch {
            repository.clearStudent()
            navController.navigate(Routes.AUTH) {
                popUpTo(Routes.MAIN_TABS) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    val requestLogout: () -> Unit = {
        if (isLogoutConfirmationEnabled) {
            shouldSkipNextLogoutConfirmation = false
            isLogoutDialogOpen = true
        } else {
            performLogout()
        }
    }
    val navigateToConversation: () -> Unit = {
        navController.navigate(Routes.CONVERSATION) {
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        repository.seedInitialVocabularyIfNeeded()
        val currentUser = firebaseAuth.currentUser
        startDestination = if (currentUser != null) {
            ensureLocalStudent(repository, currentUser)
            Routes.MAIN_TABS
        } else {
            Routes.AUTH
        }
    }

    SkyThemeBackground(isDarkTheme = isDarkTheme) {
        val initialRoute = startDestination

        if (initialRoute == null && !showSplash) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (initialRoute != null && !showSplash) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLogoutDialogOpen) {
                    LogoutConfirmationDialog(
                        doNotShowAgain = shouldSkipNextLogoutConfirmation,
                        onDoNotShowAgainChange = { shouldSkipNextLogoutConfirmation = it },
                        onDismiss = { isLogoutDialogOpen = false },
                        onConfirm = {
                            if (shouldSkipNextLogoutConfirmation) {
                                onDisableLogoutConfirmation()
                            }
                            isLogoutDialogOpen = false
                            performLogout()
                        }
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = initialRoute,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Routes.AUTH) {
                        val viewModel: AuthViewModel = viewModel(factory = factory)
                        AuthScreen(
                            isLoading = viewModel.isLoading,
                            errorMessage = viewModel.errorMessage,
                            successMessage = viewModel.successMessage,
                            onLoginSubmit = { email, password ->
                                viewModel.login(email, password) {
                                    selectedMainRoute = Routes.HOME
                                    navController.navigate(Routes.MAIN_TABS) {
                                        popUpTo(Routes.AUTH) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onRegisterSubmit = { name, email, password, grade ->
                                viewModel.register(name, email, password, grade) {
                                    selectedMainRoute = Routes.HOME
                                    navController.navigate(Routes.MAIN_TABS) {
                                        popUpTo(Routes.AUTH) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onResetPasswordSubmit = viewModel::resetPassword
                        )
                    }

                    composable(Routes.ONBOARDING) {
                        val viewModel: OnboardingViewModel = viewModel(factory = factory)
                        OnboardingScreen(
                            viewModel = viewModel,
                            onFinished = {
                                selectedMainRoute = Routes.HOME
                                navController.navigate(Routes.MAIN_TABS) {
                                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Routes.MAIN_TABS) {
                        MainTabsContent(
                            factory = factory,
                            selectedRoute = selectedMainRoute,
                            onSelectedRouteChange = { route -> selectedMainRoute = route },
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onPronunciationClick = {
                                navController.navigate(Routes.PRONUNCIATION) {
                                    launchSingleTop = true
                                }
                            },
                            bottomContentPadding = bottomContentPadding,
                            onProfileClick = { navController.navigate(Routes.PROFILE) },
                            onStoryClick = {
                                navController.navigate(Routes.STORY) {
                                    launchSingleTop = true
                                }
                            },
                            onVocabularyDetailClick = { wordId ->
                                navController.navigate(Routes.vocabularyDetail(wordId))
                            },
                            onConversationClick = navigateToConversation
                        )
                    }

                    composable(Routes.CONVERSATION) {
                        val viewModel: ConversationViewModel = viewModel(factory = factory)
                        ConversationScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.PRONUNCIATION) {
                        val viewModel: PronunciationViewModel = viewModel(factory = factory)
                        PronunciationScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.STORY) {
                        val viewModel: com.example.vocabkid.presentation.story.StoryViewModel = viewModel(factory = factory)
                        com.example.vocabkid.presentation.story.StoryScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
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

                    composable(Routes.PROFILE) {
                        val viewModel: ProfileViewModel = viewModel(factory = factory)
                        ProfileScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onLogoutClick = requestLogout
                        )
                    }
                }

                if (showBottomBar) {
                    GlassBottomNavigationBar(
                        currentRoute = selectedMainRoute,
                        onNavigate = { route ->
                            selectedMainRoute = route
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }


            }
        }

        if (showSplash) {
            SplashScreen(onFinished = { showSplash = false })
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    doNotShowAgain: Boolean,
    onDoNotShowAgainChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "logoutDialogMotion")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoutDialogBob"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LogoutDialogIllustration(
                    bob = bob,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp)
                )
                Text(
                    text = "Keluar akun?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Kamu akan kembali ke halaman login. Progres latihan tetap tersimpan di perangkat ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                        .clickable {
                            onDoNotShowAgainChange(!doNotShowAgain)
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = doNotShowAgain,
                        onCheckedChange = onDoNotShowAgainChange
                    )
                    Text(
                        text = "Jangan tampilkan lagi",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Keluar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Batal")
            }
        },
        shape = RoundedCornerShape(8.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun LogoutDialogIllustration(
    bob: Float,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
    val tertiary = MaterialTheme.colorScheme.tertiary
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline

    Canvas(modifier = modifier) {
        drawLogoutIllustration(
            bob = bob,
            primary = primary,
            primaryContainer = primaryContainer,
            secondaryContainer = secondaryContainer,
            tertiary = tertiary,
            tertiaryContainer = tertiaryContainer,
            surface = surface,
            outline = outline
        )
    }
}

private fun DrawScope.drawLogoutIllustration(
    bob: Float,
    primary: Color,
    primaryContainer: Color,
    secondaryContainer: Color,
    tertiary: Color,
    tertiaryContainer: Color,
    surface: Color,
    outline: Color
) {
    val w = size.width
    val h = size.height
    val radius = 8.dp.toPx()
    val bobPx = bob * density

    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                primaryContainer.copy(alpha = 0.92f),
                tertiaryContainer.copy(alpha = 0.86f),
                secondaryContainer.copy(alpha = 0.78f)
            ),
            start = Offset.Zero,
            end = Offset(w, h)
        ),
        cornerRadius = CornerRadius(radius, radius)
    )

    drawRoundRect(
        color = surface.copy(alpha = 0.88f),
        topLeft = Offset(w * 0.18f, h * 0.24f + bobPx),
        size = Size(w * 0.26f, h * 0.44f),
        cornerRadius = CornerRadius(radius, radius)
    )
    drawRoundRect(
        color = primary.copy(alpha = 0.84f),
        topLeft = Offset(w * 0.21f, h * 0.28f + bobPx),
        size = Size(w * 0.20f, h * 0.09f),
        cornerRadius = CornerRadius(radius, radius)
    )
    repeat(3) { index ->
        drawRoundRect(
            color = outline.copy(alpha = 0.34f),
            topLeft = Offset(w * 0.23f, h * (0.43f + index * 0.075f) + bobPx),
            size = Size(w * 0.16f, 3.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
    }

    drawRoundRect(
        color = tertiary.copy(alpha = 0.94f),
        topLeft = Offset(w * 0.56f, h * 0.18f - bobPx * 0.62f),
        size = Size(w * 0.22f, h * 0.54f),
        cornerRadius = CornerRadius(radius, radius)
    )
    drawRoundRect(
        color = surface.copy(alpha = 0.9f),
        topLeft = Offset(w * 0.61f, h * 0.24f - bobPx * 0.62f),
        size = Size(w * 0.12f, h * 0.42f),
        cornerRadius = CornerRadius(radius * 0.72f, radius * 0.72f)
    )
    drawCircle(
        color = primary,
        radius = 4.dp.toPx(),
        center = Offset(w * 0.715f, h * 0.46f - bobPx * 0.62f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.72f),
        radius = 4.dp.toPx(),
        center = Offset(w * 0.83f, h * 0.26f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.56f),
        radius = 3.dp.toPx(),
        center = Offset(w * 0.12f, h * 0.22f)
    )
}

private suspend fun ensureLocalStudent(
    repository: VocabKidRepository,
    user: FirebaseUser
) {
    if (repository.observeStudent().first() != null) return

    repository.saveStudent(
        name = defaultStudentName(user),
        grade = 3
    )
}

private fun defaultStudentName(user: FirebaseUser): String {
    return user.displayName
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: user.email
            ?.substringBefore("@")
            ?.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
            ?.takeIf { it.isNotBlank() }
        ?: "Siswa VocabKid"
}
