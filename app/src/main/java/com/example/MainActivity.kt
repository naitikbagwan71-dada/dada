package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.AdManager
import com.example.ui.StudyScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Safe drawings inets & Edge to edge layout
        enableEdgeToEdge()

        // Observe ViewModel's AdMob interstitial milestone triggers
        lifecycleScope.launch {
            viewModel.showAdEvent.collect {
                Log.d("HalAi_MainActivity", "Milestone hit! Showing study break Interstitial ad...")
                AdManager.showAd(this@MainActivity) {
                    Log.d("HalAi_MainActivity", "Interstitial study break finished.")
                }
            }
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudyScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onTriggerAd = {
                            AdManager.showAd(this@MainActivity) {}
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppPause()
    }
}
