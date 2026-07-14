package com.example.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private const val TAG = "HalAi_AdManager"
    private const val AD_UNIT_ID = "ca-app-pub-1651942733947167/2945531990"
    
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    fun initialize(context: Context) {
        MobileAds.initialize(context) { status ->
            Log.d(TAG, "AdMob Mobile Ads SDK Initialized: $status")
        }
    }

    fun loadAd(context: Context, onLoaded: (() -> Unit)? = null) {
        if (isAdLoading || interstitialAd != null) {
            Log.d(TAG, "Ad is already loading or already loaded.")
            return
        }
        isAdLoading = true
        Log.d(TAG, "Starting to load Interstitial Ad...")
        
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context.applicationContext,
            AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    Log.d(TAG, "Interstitial Ad loaded successfully!")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    Log.w(TAG, "Failed to load Interstitial Ad: ${error.message} (code: ${error.code})")
                }
            }
        )
    }

    fun isAdLoaded(): Boolean {
        return interstitialAd != null
    }

    fun showAd(activity: Activity, onAdClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            Log.d(TAG, "Showing Interstitial Ad...")
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad dismissed. Preparing next load...")
                    interstitialAd = null
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    Log.w(TAG, "Ad failed to show: ${error.message}")
                    interstitialAd = null
                    onAdClosed()
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "Ad not ready yet. Continuing flow.")
            onAdClosed()
        }
    }
}
