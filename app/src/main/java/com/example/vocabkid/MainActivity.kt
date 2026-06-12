package com.example.vocabkid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.vocabkid.presentation.navigation.VocabKidNavHost
import com.example.vocabkid.ui.theme.VocabKidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as VocabKidApplication).repository
        setContent {
            VocabKidTheme {
                VocabKidNavHost(repository = repository)
            }
        }
    }
}
