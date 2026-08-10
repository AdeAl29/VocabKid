package com.example.vocabkid.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.vocabkid.R

class SoundEffectPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val loadedSoundIds = mutableSetOf<Int>()
    private var isReleased = false

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val flipCardSoundId: Int
    private val correctAnswerSoundId: Int
    private val wrongAnswerSoundId: Int
    private val tapSoundId: Int
    private val selectSoundId: Int

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == LOAD_SUCCESS) {
                synchronized(loadedSoundIds) {
                    if (!isReleased) {
                        loadedSoundIds.add(sampleId)
                    }
                }
            }
        }

        flipCardSoundId = load(R.raw.sfx_flip_card)
        correctAnswerSoundId = load(R.raw.sfx_correct)
        wrongAnswerSoundId = load(R.raw.sfx_wrong)
        tapSoundId = load(R.raw.sfx_tap)
        selectSoundId = load(R.raw.sfx_select)
    }

    fun playTap() {
        play(tapSoundId, volume = 0.18f)
    }

    fun playSelect() {
        play(selectSoundId, volume = 0.38f)
    }

    fun playFlipCard() {
        play(flipCardSoundId, volume = 0.45f)
    }

    fun playCorrectAnswer() {
        play(correctAnswerSoundId, volume = 0.62f)
    }

    fun playWrongAnswer() {
        play(wrongAnswerSoundId, volume = 0.52f)
    }

    fun release() {
        synchronized(loadedSoundIds) {
            if (isReleased) return
            isReleased = true
            loadedSoundIds.clear()
        }
        soundPool.setOnLoadCompleteListener(null)
        soundPool.release()
    }

    private fun load(@RawRes soundResourceId: Int): Int {
        return soundPool.load(appContext, soundResourceId, LOAD_PRIORITY)
    }

    private fun play(soundId: Int, volume: Float) {
        val canPlay = synchronized(loadedSoundIds) {
            !isReleased && loadedSoundIds.contains(soundId)
        }
        if (!canPlay) return

        soundPool.play(
            soundId,
            volume,
            volume,
            PLAY_PRIORITY,
            NO_LOOP,
            NORMAL_PLAYBACK_RATE
        )
    }

    private companion object {
        const val MAX_STREAMS = 4
        const val LOAD_SUCCESS = 0
        const val LOAD_PRIORITY = 1
        const val PLAY_PRIORITY = 1
        const val NO_LOOP = 0
        const val NORMAL_PLAYBACK_RATE = 1f
    }
}

private val LocalSoundEffectPlayer = staticCompositionLocalOf<SoundEffectPlayer?> {
    null
}

@Composable
fun SoundEffectProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val player = remember(context) {
        SoundEffectPlayer(context)
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    CompositionLocalProvider(LocalSoundEffectPlayer provides player) {
        content()
    }
}

@Composable
fun PressSoundEffectLayer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val player = rememberSoundEffectPlayer()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(player) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.changes.any { it.changedToDownIgnoreConsumed() }) {
                            player.playTap()
                        }
                    }
                }
            }
    ) {
        content()
    }
}

@Composable
fun rememberSoundEffectPlayer(): SoundEffectPlayer {
    val providedPlayer = LocalSoundEffectPlayer.current
    if (providedPlayer != null) {
        return providedPlayer
    }

    val context = LocalContext.current
    val player = remember(context) {
        SoundEffectPlayer(context)
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    return player
}
