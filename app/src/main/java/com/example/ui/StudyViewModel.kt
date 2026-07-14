package com.example.ui

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdManager
import com.example.data.AppDatabase
import com.example.data.StudyRepository
import com.example.data.StudyScan
import com.example.data.TtsManager
import com.example.data.GeminiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Locale

sealed interface StudyUiState {
    object Idle : StudyUiState
    object Loading : StudyUiState
    data class Success(val response: String) : StudyUiState
    data class Error(val message: String) : StudyUiState
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "HalAi_StudyViewModel"
    private val repository: StudyRepository
    private var ttsManager: TtsManager? = null

    // State flows
    private val _preferredLanguage = MutableStateFlow<String?>(null)
    val preferredLanguage: StateFlow<String?> = _preferredLanguage.asStateFlow()

    private val _focusAllowed = MutableStateFlow<Boolean?>(null)
    val focusAllowed: StateFlow<Boolean?> = _focusAllowed.asStateFlow()

    private val _isFocusModeActive = MutableStateFlow(false)
    val isFocusModeActive: StateFlow<Boolean> = _isFocusModeActive.asStateFlow()

    private val _allScans = MutableStateFlow<List<StudyScan>>(emptyList())
    val allScans: StateFlow<List<StudyScan>> = _allScans.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterFavoritesOnly = MutableStateFlow(false)
    val filterFavoritesOnly: StateFlow<Boolean> = _filterFavoritesOnly.asStateFlow()

    private val _uiState = MutableStateFlow<StudyUiState>(StudyUiState.Idle)
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    // Scanned homework visual attachments
    private val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap.asStateFlow()

    private val _selectedImageBase64 = MutableStateFlow<String?>(null)
    val selectedImageBase64: StateFlow<String?> = _selectedImageBase64.asStateFlow()

    // Timer state for AdMob Interstitial breaks (5, 10, 20, 30 min of usage)
    private val _secondsElapsed = MutableStateFlow(0)
    val secondsElapsed: StateFlow<Int> = _secondsElapsed.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    // Event flow to trigger showing Interstitial ad in Activity context
    private val _showAdEvent = MutableSharedFlow<Unit>()
    val showAdEvent: SharedFlow<Unit> = _showAdEvent.asSharedFlow()

    private var timerJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StudyRepository(database)
        ttsManager = TtsManager(application)

        // Load settings from Room Database
        viewModelScope.launch {
            val lang = repository.getPreference("pref_language")
            _preferredLanguage.value = lang

            val focus = repository.getPreference("pref_focus_allowed")
            _focusAllowed.value = focus?.toBoolean()

            // Automatically turn focus mode on if allowed and we have policy access
            if (_focusAllowed.value == true) {
                checkAndEnableDnd(application)
            }

            // Observe scans history from database
            repository.allScans.collect { scans ->
                _allScans.value = scans
            }
        }

        // Start usage timer
        startUsageTimer()

        // Initialize AdMob
        AdManager.initialize(application)
    }

    private fun startUsageTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _secondsElapsed.value += 1
                val elapsed = _secondsElapsed.value

                // Milestones: 5 minutes (300s), 10 minutes (600s), 20 minutes (1200s), 30 minutes (1800s)
                val targetTimes = listOf(300, 600, 1200, 1800)
                // Pre-load 30 seconds before each milestone
                val loadTimes = targetTimes.map { it - 30 }

                if (loadTimes.contains(elapsed)) {
                    Log.d(TAG, "Ad Optimization: Pre-loading Interstitial Ad 30 seconds before milestone at $elapsed seconds...")
                    AdManager.loadAd(getApplication())
                }

                if (targetTimes.contains(elapsed)) {
                    Log.d(TAG, "Study Milestone Hit! Triggering Interstitial Ad break at $elapsed seconds...")
                    _showAdEvent.emit(Unit)
                }
            }
        }
    }

    // Next study break calculation helper
    fun getSecondsToNextBreak(): Int {
        val elapsed = _secondsElapsed.value
        val targets = listOf(300, 600, 1200, 1800)
        val nextTarget = targets.firstOrNull { it > elapsed } ?: 1800
        return (nextTarget - elapsed).coerceAtLeast(0)
    }

    fun selectLanguage(lang: String) {
        viewModelScope.launch {
            repository.savePreference("pref_language", lang)
            _preferredLanguage.value = lang
        }
    }

    fun setFocusPermission(allowed: Boolean) {
        viewModelScope.launch {
            repository.savePreference("pref_focus_allowed", allowed.toString())
            _focusAllowed.value = allowed
            if (allowed) {
                checkAndEnableDnd(getApplication())
            } else {
                disableDnd(getApplication())
            }
        }
    }

    fun checkAndEnableDnd(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                try {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                    _isFocusModeActive.value = true
                    Log.d(TAG, "Focus Mode active: Notification Policy DND activated.")
                } catch (e: Exception) {
                    Log.e(TAG, "Error activating interruption filter", e)
                }
            } else {
                _isFocusModeActive.value = false
            }
        } else {
            _isFocusModeActive.value = true
        }
    }

    fun disableDnd(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                try {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                    _isFocusModeActive.value = false
                    Log.d(TAG, "Focus Mode deactivated: Restored standard notifications.")
                } catch (e: Exception) {
                    Log.e(TAG, "Error resetting interruption filter", e)
                }
            }
        }
        _isFocusModeActive.value = false
    }

    fun onAppResume() {
        if (_focusAllowed.value == true) {
            checkAndEnableDnd(getApplication())
        }
    }

    fun onAppPause() {
        // Restore notifications immediately when app is active focus is lost/paused
        if (_focusAllowed.value == true) {
            disableDnd(getApplication())
        }
    }

    fun selectImage(bitmap: Bitmap?) {
        _selectedImageBitmap.value = bitmap
        if (bitmap != null) {
            // Store as base64 for local database history caching
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()
            _selectedImageBase64.value = Base64.encodeToString(byteArray, Base64.DEFAULT)
        } else {
            _selectedImageBase64.value = null
        }
    }

    fun askStudyQuestion(questionText: String) {
        val query = questionText.trim()
        if (query.isEmpty() && _selectedImageBitmap.value == null) return

        _uiState.value = StudyUiState.Loading
        stopTts()

        viewModelScope.launch {
            val language = _preferredLanguage.value ?: "en"
            
            // Build system instruction enforcing language, safety, and specialized Study-expert boundaries
            val systemInstruction = """
                You are HalAi, a dedicated, distraction-free study helper.
                Your absolute highest priority is to answer ONLY study-related questions (Math, Physics, Chemistry, Biology, Science, History, Geography, Literature, Economics, Computer Science, general school homework).
                
                If the user asks non-study questions (such as product prices, general gossiping, pop music, celebrity gossip, buying items, jokes, weather, general chitchat unrelated to learning/education, etc.), you MUST politely reply exactly in their chosen language:
                - For Hindi: 'कृपया मुझसे अपनी पढ़ाई के बारे में पूछें; मैं इस प्रश्न का उत्तर नहीं दे सकता।'
                - For English: 'Please ask me about your studies; I cannot answer this question.'
                
                Do NOT answer any non-study questions under any circumstances.
                
                Always respond in the user's preferred language: ${if (language == "hi") "Hindi" else "English"}.
                Explain complex educational concepts simply, step-by-step, with neat bullet points, so they are instantly memorized.
                Every response you give must end with the exact disclaimer:
                - For English: "This is an AI; errors may occur."
                - For Hindi: "यह एक एआई है; त्रुटियां हो सकती हैं।"
            """.trimIndent()

            val response = GeminiService.generateStudyResponse(
                prompt = query.ifEmpty { "Solve this study-related question step by step." },
                bitmap = _selectedImageBitmap.value,
                systemInstruction = systemInstruction
            )

            // Save image attachment base64 for storage
            val imgBase64 = _selectedImageBase64.value

            // Insert into local history
            val promptDisplay = if (query.isNotEmpty()) query else {
                if (language == "hi") "[स्कैन की गई छवि]" else "[Scanned Image]"
            }
            repository.insertScan(promptDisplay, response, imgBase64)

            _uiState.value = StudyUiState.Success(response)

            // Clear selected image after submitting
            _selectedImageBitmap.value = null
            _selectedImageBase64.value = null
        }
    }

    fun speak(text: String) {
        val language = _preferredLanguage.value ?: "en"
        val locale = if (language == "hi") Locale("hi", "IN") else Locale.ENGLISH
        _isSpeaking.value = true
        ttsManager?.speak(text, locale)
    }

    fun stopTts() {
        _isSpeaking.value = false
        ttsManager?.stop()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterFavoritesOnly(favoritesOnly: Boolean) {
        _filterFavoritesOnly.value = favoritesOnly
    }

    fun toggleBookmark(scan: StudyScan) {
        viewModelScope.launch {
            repository.updateBookmark(scan.id, !scan.isBookmarked)
        }
    }

    fun deleteScan(id: Long) {
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun decodeBase64ToBitmap(base64Str: String?): Bitmap? {
        if (base64Str == null) return null
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun triggerManualAdDisplay(activity: Activity) {
        if (AdManager.isAdLoaded()) {
            AdManager.showAd(activity) {
                Toast.makeText(activity, "Break complete. Return to your studies!", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Ad not loaded yet, fetch it now
            AdManager.loadAd(activity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        ttsManager?.shutdown()
        ttsManager = null
    }
}
