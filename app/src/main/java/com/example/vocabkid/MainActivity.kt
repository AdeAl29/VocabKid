package com.example.vocabkid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import com.example.vocabkid.audio.BackgroundMusicPlayer
import com.example.vocabkid.audio.PressSoundEffectLayer
import com.example.vocabkid.audio.SoundEffectProvider
import com.example.vocabkid.presentation.navigation.VocabKidNavHost
import com.example.vocabkid.ui.theme.VocabKidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as VocabKidApplication).repository
        val appPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        setContent {
            var isDarkTheme by rememberSaveable {
                mutableStateOf(appPreferences.getBoolean(KEY_DARK_THEME, false))
            }
            var isMusicEnabled by rememberSaveable {
                mutableStateOf(appPreferences.getBoolean(KEY_BACKGROUND_MUSIC, false))
            }
            var isLogoutConfirmationEnabled by rememberSaveable {
                mutableStateOf(appPreferences.getBoolean(KEY_LOGOUT_CONFIRMATION, true))
            }

            LaunchedEffect(isDarkTheme) {
                appPreferences.edit()
                    .putBoolean(KEY_DARK_THEME, isDarkTheme)
                    .apply()
            }

            LaunchedEffect(isMusicEnabled) {
                appPreferences.edit()
                    .putBoolean(KEY_BACKGROUND_MUSIC, isMusicEnabled)
                    .apply()
            }

            LaunchedEffect(isLogoutConfirmationEnabled) {
                appPreferences.edit()
                    .putBoolean(KEY_LOGOUT_CONFIRMATION, isLogoutConfirmationEnabled)
                    .apply()
            }

            BackgroundMusicEffect(isEnabled = isMusicEnabled)

            VocabKidTheme(darkTheme = isDarkTheme) {
                SideEffect {
                    window.statusBarColor = Color.Transparent.toArgb()
                    window.navigationBarColor = if (isDarkTheme) {
                        Color(0xFF082D38).toArgb()
                    } else {
                        Color(0xFFAEE8A7).toArgb()
                    }
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !isDarkTheme
                        isAppearanceLightNavigationBars = !isDarkTheme
                    }
                }

                SoundEffectProvider {
                    PressSoundEffectLayer {
                        VocabKidNavHost(
                            repository = repository,
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
                            isMusicEnabled = isMusicEnabled,
                            onToggleMusic = { isMusicEnabled = !isMusicEnabled },
                            isLogoutConfirmationEnabled = isLogoutConfirmationEnabled,
                            onDisableLogoutConfirmation = { isLogoutConfirmationEnabled = false }
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "vocabkid_preferences"
        const val KEY_DARK_THEME = "dark_theme"
        const val KEY_BACKGROUND_MUSIC = "background_music"
        const val KEY_LOGOUT_CONFIRMATION = "logout_confirmation"
    }
}

@Composable
private fun BackgroundMusicEffect(isEnabled: Boolean) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val player = remember { BackgroundMusicPlayer() }

    DisposableEffect(lifecycleOwner, isEnabled) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (isEnabled) player.start()
                Lifecycle.Event.ON_STOP -> player.stop()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        if (isEnabled && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            player.start()
        } else {
            player.stop()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
}
