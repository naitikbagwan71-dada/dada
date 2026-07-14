package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.BorderStroke
import com.example.data.AdManager
import com.example.data.StudyScan
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.DarkGrayText
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceCard
import com.example.ui.theme.LightGrayText
import com.example.ui.theme.MutedBlueGray
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBackground
import kotlinx.coroutines.launch

object Locales {
    val en = mapOf(
        "welcome" to "HalAi Study Expert",
        "welcome_desc" to "Your dedicated distraction-free AI assistant. Get explanations for Math, Physics, and other STEM homework instantly.",
        "select_lang" to "Select Preferred Language",
        "focus_permission_title" to "Notification Blocker",
        "focus_permission_desc" to "HalAi needs permission to hide notifications. Social media notifications can distract you while studying. This feature will hide notifications until you close the app. Do you allow this?",
        "allow" to "Allow",
        "dont_allow" to "Don't Allow",
        "focus_active" to "FOCUS MODE: ACTIVE 🛡️",
        "focus_inactive" to "FOCUS MODE: OFF ⚠️",
        "scan" to "Scan",
        "listen" to "Listen",
        "stop" to "Stop",
        "hint" to "Ask about Math, Physics, Chemistry, Science...",
        "footer" to "This is an AI; errors may occur.",
        "empty_history" to "Scan a photo of your homework or type a study question to get started!",
        "break_info" to "Break",
        "clear_history" to "Clear History",
        "settings" to "Settings",
        "dnd_instruction_title" to "Do Not Disturb Permission Required",
        "dnd_instruction_desc" to "To block distracting notifications, HalAi needs 'Do Not Disturb' Access.\n\nPlease find 'HalAi' in the settings list and toggle it ON, then return to the app.",
        "open_settings" to "Open Settings",
        "search_placeholder" to "Search past explanations...",
        "filter_all" to "All",
        "filter_saved" to "Saved ⭐",
        "no_saved_results" to "No bookmarked explanations found.",
        "no_search_results" to "No past solutions match your search."
    )

    val hi = mapOf(
        "welcome" to "HalAi स्टडी एक्सपर्ट",
        "welcome_desc" to "आपका ध्यान-मुक्त एआई अध्ययन सहायक। गणित, भौतिकी और अन्य STEM होमवर्क के समाधान तुरंत प्राप्त करें।",
        "select_lang" to "अपनी पसंदीदा भाषा चुनें",
        "focus_permission_title" to "सूचना अवरोधक (फोकस मोड)",
        "focus_permission_desc" to "HalAi को नोटिफिकेशन छिपाने की अनुमति चाहिए। पढ़ाई के दौरान सोशल मीडिया के नोटिफिकेशन आपको विचलित कर सकते हैं। यह फीचर ऐप बंद होने तक नोटिफिकेशन छिपा देगा। क्या आप इसकी अनुमति देते हैं?",
        "allow" to "अनुमति दें",
        "dont_allow" to "अनुमति न दें",
        "focus_active" to "फोकस मोड: सक्रिय 🛡️",
        "focus_inactive" to "फोकस मोड: बंद ⚠️",
        "scan" to "स्कैन करें",
        "listen" to "सुनें",
        "stop" to "रोकें",
        "hint" to "गणित, भौतिकी, रसायन, विज्ञान आदि के बारे में पूछें...",
        "footer" to "यह एक एआई है; त्रुटियां हो सकती हैं।",
        "empty_history" to "होमवर्क की फोटो स्कैन करें या पढ़ाई का प्रश्न टाइप करके हल प्राप्त करें!",
        "break_info" to "ब्रेक",
        "clear_history" to "इतिहास साफ़ करें",
        "settings" to "सेटिंग्स",
        "dnd_instruction_title" to "परेशान न करें अनुमति आवश्यक है",
        "dnd_instruction_desc" to "ध्यान भटकाने वाले नोटिफिकेशन को छिपाने के लिए, HalAi को 'परेशान न करें' अनुमति की आवश्यकता है।\n\nकृपया सूची में 'HalAi' ढूंढें और इसे ON करें, फिर ऐप पर लौटें।",
        "open_settings" to "सेटिंग्स खोलें",
        "search_placeholder" to "पुराने उत्तर खोजें...",
        "filter_all" to "सभी",
        "filter_saved" to "सहेजे गए ⭐",
        "no_saved_results" to "कोई सहेजा हुआ उत्तर नहीं मिला।",
        "no_search_results" to "आपकी खोज से मेल खाता कोई पुराना हल नहीं मिला।"
    )

    fun get(lang: String?, key: String): String {
        return if (lang == "hi") hi[key] ?: (en[key] ?: "") else en[key] ?: ""
    }
}

@Composable
fun StudyScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier,
    onTriggerAd: () -> Unit
) {
    val preferredLanguage by viewModel.preferredLanguage.collectAsState()
    val focusAllowed by viewModel.focusAllowed.collectAsState()
    val isFocusModeActive by viewModel.isFocusModeActive.collectAsState()
    val allScans by viewModel.allScans.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterFavoritesOnly by viewModel.filterFavoritesOnly.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val selectedBitmap by viewModel.selectedImageBitmap.collectAsState()
    val secondsElapsed by viewModel.secondsElapsed.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    
    // Check if system DND permission is granted if DND is enabled
    var showDndPermissionGuidance by remember { mutableStateOf(false) }

    LaunchedEffect(focusAllowed) {
        if (focusAllowed == true) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
                showDndPermissionGuidance = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Linear Background Radial Glow mimicking Gemini interface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.12f),
                            NeonCyan.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        when {
            // STEP 1: Onboarding preferred language selection
            preferredLanguage == null -> {
                LanguageSelectionScreen(
                    onSelect = { lang ->
                        viewModel.selectLanguage(lang)
                    }
                )
            }

            // STEP 2: Onboarding notification blocker (Focus Mode) prompt
            focusAllowed == null -> {
                FocusPermissionPrompt(
                    lang = preferredLanguage,
                    onAccept = { allowed ->
                        viewModel.setFocusPermission(allowed)
                    }
                )
            }

            // STEP 3: Main distraction-free study dashboard
            else -> {
                // If user allowed Focus Mode but the system permission isn't granted yet, show a guidance overlay
                if (showDndPermissionGuidance) {
                    DndPermissionGuidanceDialog(
                        lang = preferredLanguage,
                        onDismiss = { showDndPermissionGuidance = false },
                        onOpenSettings = {
                            showDndPermissionGuidance = false
                            try {
                                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open settings: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                MainDashboardContent(
                    viewModel = viewModel,
                    preferredLanguage = preferredLanguage,
                    isFocusModeActive = isFocusModeActive,
                    allScans = allScans,
                    searchQuery = searchQuery,
                    filterFavoritesOnly = filterFavoritesOnly,
                    uiState = uiState,
                    selectedBitmap = selectedBitmap,
                    inputText = inputText,
                    onInputTextChange = { inputText = it },
                    secondsElapsed = secondsElapsed,
                    isSpeaking = isSpeaking,
                    onClearHistory = { viewModel.clearHistory() },
                    onToggleFocus = {
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
                            showDndPermissionGuidance = true
                        } else {
                            val newAllowed = !(focusAllowed ?: false)
                            viewModel.setFocusPermission(newAllowed)
                        }
                    },
                    onSubmitQuestion = {
                        viewModel.askStudyQuestion(inputText)
                        inputText = ""
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageSelectionScreen(onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("language_selection_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App branding logo
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonPink, NeonCyan)),
                    CircleShape
                )
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryBooks,
                    contentDescription = "HalAi Logo",
                    tint = NeonCyan,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "HalAi",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.5.sp,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = "Your Dedicated Study Assistant",
            fontSize = 16.sp,
            color = LightGrayText,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Language Option Cards
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable { onSelect("en") }
                .testTag("select_english_button"),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "English",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Learn in English language",
                        fontSize = 14.sp,
                        color = DarkGrayText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "English",
                    tint = NeonCyan,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable { onSelect("hi") }
                .testTag("select_hindi_button"),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "हिंदी (Hindi)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "हिंदी भाषा में अध्ययन करें",
                        fontSize = 14.sp,
                        color = DarkGrayText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Hindi",
                    tint = NeonPurple,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun FocusPermissionPrompt(lang: String?, onAccept: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("focus_permission_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Warning focus icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(NeonPink.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, NeonPink.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = "Focus Mode Alert",
                tint = NeonPink,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = Locales.get(lang, "focus_permission_title"),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = NeonPink,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MutedBlueGray, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
        ) {
            Text(
                text = Locales.get(lang, "focus_permission_desc"),
                fontSize = 16.sp,
                color = LightGrayText,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Button(
            onClick = { onAccept(true) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("allow_focus_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = Locales.get(lang, "allow"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onAccept(false) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("deny_focus_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGrayText),
            border = BorderStroke(1.dp, MutedBlueGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = Locales.get(lang, "dont_allow"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DndPermissionGuidanceDialog(
    lang: String?,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable(enabled = false) {},
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "DND Permission Icon",
                    tint = NeonCyan,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = Locales.get(lang, "dnd_instruction_title"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = Locales.get(lang, "dnd_instruction_desc"),
                    fontSize = 14.sp,
                    color = LightGrayText,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, MutedBlueGray),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGrayText),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onOpenSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(
                            text = Locales.get(lang, "open_settings"),
                            color = Color(0xFF020617),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainDashboardContent(
    viewModel: StudyViewModel,
    preferredLanguage: String?,
    isFocusModeActive: Boolean,
    allScans: List<StudyScan>,
    searchQuery: String,
    filterFavoritesOnly: Boolean,
    uiState: StudyUiState,
    selectedBitmap: Bitmap?,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    secondsElapsed: Int,
    isSpeaking: Boolean,
    onClearHistory: () -> Unit,
    onToggleFocus: () -> Unit,
    onSubmitQuestion: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val filteredScans = remember(allScans, searchQuery, filterFavoritesOnly) {
        allScans.filter { scan ->
            val matchesQuery = searchQuery.isEmpty() ||
                    scan.prompt.contains(searchQuery, ignoreCase = true) ||
                    scan.response.contains(searchQuery, ignoreCase = true)
            val matchesFavorite = !filterFavoritesOnly || scan.isBookmarked
            matchesQuery && matchesFavorite
        }
    }

    // Scroll to top automatically when new chat scans are added
    LaunchedEffect(allScans.size, uiState) {
        if (allScans.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    // Media picking launcher
    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.selectImage(bitmap)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load scan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_dashboard")
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "HalAi",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = 0.5.sp
                )
                // Speaking indicator
                AnimatedVisibility(visible = isSpeaking) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = if (preferredLanguage == "hi") "एआई बोल रहा है..." else "AI is speaking...",
                            fontSize = 11.sp,
                            color = NeonPurple,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        CircularProgressIndicator(
                            color = NeonPurple,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            // Quick Toggle Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Focus Mode Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isFocusModeActive) NeonGreen.copy(alpha = 0.1f) else NeonPink.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            if (isFocusModeActive) NeonGreen.copy(alpha = 0.6f) else NeonPink.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onToggleFocus() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("focus_mode_status_badge")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pulse circle for active mode
                        if (isFocusModeActive) {
                            val infiniteTransition = rememberInfiniteTransition()
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.2f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .alpha(alpha)
                                    .background(NeonGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = if (isFocusModeActive) {
                                if (preferredLanguage == "hi") "फोकस: चालू 🛡️" else "FOCUS: ON 🛡️"
                            } else {
                                if (preferredLanguage == "hi") "फोकस: बंद ⚠️" else "FOCUS: OFF ⚠️"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFocusModeActive) NeonGreen else NeonPink
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Language quick toggle
                IconButton(
                    onClick = {
                        val nextLang = if (preferredLanguage == "hi") "en" else "hi"
                        viewModel.selectLanguage(nextLang)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSlateSurfaceCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Toggle Language",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Clear history
                IconButton(
                    onClick = { onClearHistory() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSlateSurfaceCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear History",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // STUDY TIMER & STUDY BREAK BANNER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = NeonPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        val secondsToNext = viewModel.getSecondsToNextBreak()
                        val minutesLeft = secondsToNext / 60
                        val secsLeft = secondsToNext % 60
                        val timerString = String.format("%02d:%02d", minutesLeft, secsLeft)

                        Text(
                            text = if (preferredLanguage == "hi") "अगला अध्ययन ब्रेक" else "Next Study Break",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkGrayText
                        )
                        Text(
                            text = timerString,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonPurple,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Show visual alert when Ad pre-loads and is ready
                if (AdManager.isAdLoaded()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonPurple)
                            .clickable {
                                val activity = context as? Activity
                                if (activity != null) {
                                    viewModel.triggerManualAdDisplay(activity)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (preferredLanguage == "hi") "ब्रेक लें ☕" else "Take Break ☕",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    Text(
                        text = "Pomodoro mode",
                        fontSize = 11.sp,
                        color = DarkGrayText.copy(alpha = 0.5f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        // SEARCH & FILTER ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = Locales.get(preferredLanguage, "search_placeholder"),
                        fontSize = 13.sp,
                        color = DarkGrayText.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DarkGrayText.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setSearchQuery("") },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search",
                                tint = DarkGrayText.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSlateSurfaceCard,
                    unfocusedContainerColor = DarkSlateSurface,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = MutedBlueGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonCyan
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp)
                    .testTag("search_history_input")
            )

            // Bookmark/Favorites filter button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (filterFavoritesOnly) NeonPurple.copy(alpha = 0.15f) else DarkSlateSurfaceCard.copy(alpha = 0.6f)
                    )
                    .border(
                        1.dp,
                        if (filterFavoritesOnly) NeonPurple else MutedBlueGray,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.setFilterFavoritesOnly(!filterFavoritesOnly) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("filter_saved_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Saved Filter",
                        tint = if (filterFavoritesOnly) NeonPurple else DarkGrayText.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Locales.get(preferredLanguage, "filter_saved"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (filterFavoritesOnly) NeonPurple else LightGrayText
                    )
                }
            }
        }

        // CHAT CONTENT CONTAINER OR LOADING SPINNER
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (allScans.isEmpty() && uiState is StudyUiState.Idle) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LibraryBooks,
                        contentDescription = "No Homework Solver History",
                        tint = DarkGrayText.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = Locales.get(preferredLanguage, "welcome"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Locales.get(preferredLanguage, "welcome_desc"),
                        fontSize = 13.sp,
                        color = DarkGrayText,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            } else if (filteredScans.isEmpty() && uiState is StudyUiState.Idle) {
                // Empty Search / Filter state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (filterFavoritesOnly) Icons.Default.Star else Icons.Default.Search,
                        contentDescription = "No results found",
                        tint = DarkGrayText.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (filterFavoritesOnly) {
                            Locales.get(preferredLanguage, "no_saved_results")
                        } else {
                            Locales.get(preferredLanguage, "no_search_results")
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // History List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    reverseLayout = false
                ) {
                    // Current loading indicator on top or bottom
                    if (uiState is StudyUiState.Loading) {
                        item {
                            StudyLoadingBubble()
                        }
                    }

                    // Display current temporary UI output state if success
                    if (uiState is StudyUiState.Success) {
                        val succ = uiState as StudyUiState.Success
                        item {
                            StudyResponseBubble(
                                isNewest = true,
                                queryText = if (selectedBitmap != null) {
                                    if (preferredLanguage == "hi") "[होमवर्क स्कैन]" else "[Homework Scan]"
                                } else "",
                                scan = StudyScan(prompt = "", response = succ.response),
                                lang = preferredLanguage,
                                isSpeaking = isSpeaking,
                                onSpeak = { viewModel.speak(succ.response) },
                                onStopSpeak = { viewModel.stopTts() }
                            )
                        }
                    }

                    items(filteredScans, key = { it.id }) { scan ->
                        StudyResponseBubble(
                            isNewest = false,
                            queryText = scan.prompt,
                            scan = scan,
                            lang = preferredLanguage,
                            isSpeaking = isSpeaking,
                            decodedBitmap = viewModel.decodeBase64ToBitmap(scan.imageBase64),
                            onSpeak = { viewModel.speak(scan.response) },
                            onStopSpeak = { viewModel.stopTts() },
                            onToggleBookmark = { viewModel.toggleBookmark(scan) },
                            onDelete = { viewModel.deleteScan(scan.id) }
                        )
                    }
                }
            }
        }

        // BOTTOM CONTROLS & ATTACHMENTS PANEL
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ObsidianBackground,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Upload Image Thumbnail Preview row (if selected)
                AnimatedVisibility(visible = selectedBitmap != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                        ) {
                            if (selectedBitmap != null) {
                                Image(
                                    bitmap = selectedBitmap.asImageBitmap(),
                                    contentDescription = "Selected scan",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            IconButton(
                                onClick = { viewModel.selectImage(null) },
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                    .align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = NeonPink,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (preferredLanguage == "hi") "स्कैन करने के लिए तैयार" else "Scan ready to solve",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan
                        )
                    }
                }

                // Input field + action buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // CAMERA SCAN BUTTON
                    IconButton(
                        onClick = { mediaLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(48.dp)
                            .background(DarkSlateSurfaceCard, CircleShape)
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), CircleShape)
                            .testTag("scan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = Locales.get(preferredLanguage, "scan"),
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // INPUT FIELD
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = onInputTextChange,
                        placeholder = {
                            Text(
                                text = Locales.get(preferredLanguage, "hint"),
                                fontSize = 13.sp,
                                color = DarkGrayText
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSlateSurfaceCard,
                            unfocusedContainerColor = DarkSlateSurface,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = MutedBlueGray,
                            focusedLabelColor = NeonCyan,
                            cursorColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = false,
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("question_input_field"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (inputText.isNotEmpty() || selectedBitmap != null) {
                                onSubmitQuestion()
                            }
                        })
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // SOLVE/ASK BUTTON
                    val sendEnabled = inputText.trim().isNotEmpty() || selectedBitmap != null
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (sendEnabled) {
                                    Brush.linearGradient(listOf(NeonCyan, NeonPurple))
                                } else {
                                    Brush.linearGradient(listOf(MutedBlueGray, MutedBlueGray))
                                }
                            )
                            .clickable(enabled = sendEnabled) {
                                onSubmitQuestion()
                            }
                            .testTag("submit_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Solve Question",
                            tint = if (sendEnabled) Color.Black else DarkGrayText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudyLoadingBubble() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                color = NeonCyan,
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "HalAi solving...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "Analyzing homework scan & drafting study guide.",
                    fontSize = 12.sp,
                    color = DarkGrayText,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun StudyResponseBubble(
    isNewest: Boolean,
    queryText: String,
    scan: StudyScan,
    lang: String?,
    isSpeaking: Boolean,
    decodedBitmap: Bitmap? = null,
    onSpeak: () -> Unit,
    onStopSpeak: () -> Unit,
    onToggleBookmark: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // User's Prompt Query bubble (Only show if prompt text exists or we have a custom decoded image)
        if (queryText.isNotEmpty() || decodedBitmap != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Card(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Display decoded homework image if present
                        if (decodedBitmap != null) {
                            Image(
                                bitmap = decodedBitmap.asImageBitmap(),
                                contentDescription = "Scanned math homework",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(bottom = 8.dp)
                            )
                        }

                        if (queryText.isNotEmpty()) {
                            Text(
                                text = queryText,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // HalAi's Assistant Explanation bubble
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (isNewest) NeonPurple.copy(alpha = 0.6f) else NeonPurple.copy(alpha = 0.15f),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Expert response text
                Text(
                    text = scan.response,
                    fontSize = 14.sp,
                    color = LightGrayText,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom actions for speaking, and the required small footer text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left actions group: Speak, Bookmark, Delete
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // TTS Listen Action
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MutedBlueGray.copy(alpha = 0.4f))
                                .clickable {
                                    if (isSpeaking) onStopSpeak() else onSpeak()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isSpeaking) "Stop speaking" else "Read solution aloud",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSpeaking) {
                                    Locales.get(lang, "stop")
                                } else {
                                    Locales.get(lang, "listen")
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        if (!isNewest) {
                            // Toggle Bookmark Button
                            IconButton(
                                onClick = onToggleBookmark,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MutedBlueGray.copy(alpha = 0.4f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Bookmark math explanation",
                                    tint = if (scan.isBookmarked) NeonPurple else DarkGrayText.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Individual Delete Button
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MutedBlueGray.copy(alpha = 0.4f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete individual item",
                                    tint = NeonPink.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // MANDATORY Footer Disclaimer
                    Text(
                        text = Locales.get(lang, "footer"),
                        fontSize = 10.sp,
                        color = DarkGrayText.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

// Standard androidx.compose.foundation.BorderStroke is used directly
