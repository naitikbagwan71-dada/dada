package com.example.data

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private val TAG = "HalAi_TtsManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var pendingLocale: Locale? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            Log.d(TAG, "TTS Initialized Successfully")
            pendingText?.let { text ->
                speak(text, pendingLocale ?: Locale.getDefault())
                pendingText = null
                pendingLocale = null
            }
        } else {
            Log.e(TAG, "Failed to initialize TTS")
        }
    }

    fun speak(text: String, locale: Locale) {
        val utteranceText = cleanMarkdown(text)
        if (!isInitialized) {
            pendingText = utteranceText
            pendingLocale = locale
            return
        }

        tts?.let {
            val result = it.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language is not supported or missing data: $locale. Falling back...")
                it.language = Locale.ENGLISH
            }
            it.speak(utteranceText, TextToSpeech.QUEUE_FLUSH, null, "HalAi_Speak")
        }
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    fun shutdown() {
        if (isInitialized) {
            tts?.shutdown()
        }
        tts = null
    }

    private fun cleanMarkdown(input: String): String {
        // Remove markdown elements like stars, hashes, and footers for high-quality audio pronunciation
        return input
            .replace(Regex("\\*\\*|\\*|_|`"), "")
            .replace(Regex("#+\\s+"), "")
            .replace("This is an AI; errors may occur.", "")
            .replace("यह एक एआई है; त्रुटियां हो सकती हैं।", "")
            .trim()
    }
}
