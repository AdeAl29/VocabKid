package com.example.vocabkid.presentation.components

import android.speech.tts.TextToSpeech
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class EnglishTextSpeaker internal constructor(
    val isReady: Boolean,
    private val speakAction: (String) -> Unit
) {
    fun speak(text: String) {
        speakAction(text)
    }
}

@Composable
fun rememberEnglishTextSpeaker(): EnglishTextSpeaker {
    val context = LocalContext.current
    var textToSpeech by remember(context) { mutableStateOf<TextToSpeech?>(null) }
    var isReady by remember(context) { mutableStateOf(false) }

    DisposableEffect(context) {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = engine?.setLanguage(Locale.US)
                isReady = languageResult != TextToSpeech.LANG_MISSING_DATA &&
                    languageResult != TextToSpeech.LANG_NOT_SUPPORTED
                engine?.setSpeechRate(0.9f)
            } else {
                isReady = false
            }
        }
        textToSpeech = engine

        onDispose {
            engine.stop()
            engine.shutdown()
            textToSpeech = null
            isReady = false
        }
    }

    return remember(textToSpeech, isReady) {
        EnglishTextSpeaker(isReady) { text ->
            val phrase = text.trim()
            if (phrase.isNotEmpty() && isReady) {
                textToSpeech?.speak(
                    phrase,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "vocabkid_${System.currentTimeMillis()}"
                )
            }
        }
    }
}

@Composable
fun SpeakerIconButton(
    text: String,
    contentDescription: String,
    speaker: EnglishTextSpeaker,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { speaker.speak(text) },
        enabled = speaker.isReady && text.isNotBlank(),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = contentDescription
        )
    }
}
