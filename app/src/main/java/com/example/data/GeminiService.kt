package com.example.data

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "HalAi_GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    suspend fun generateStudyResponse(
        prompt: String,
        bitmap: Bitmap?,
        systemInstruction: String,
        apiKey: String = BuildConfig.GEMINI_API_KEY
    ): String = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured.")
            return@withContext "Error: API key is not configured. Please add your GEMINI_API_KEY to user secrets in the AI Studio platform."
        }

        try {
            // Build the JSON body
            val jsonBody = JSONObject()

            // 1. Contents Array
            val contentsArray = JSONArray()
            val contentObject = JSONObject()
            val partsArray = JSONArray()

            // Text part
            val textPart = JSONObject().put("text", prompt)
            partsArray.put(textPart)

            // Image part (if bitmap is provided)
            if (bitmap != null) {
                val imageBase64 = bitmap.toBase64()
                val inlineDataObject = JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", imageBase64)
                val imagePart = JSONObject().put("inlineData", inlineDataObject)
                partsArray.put(imagePart)
            }

            contentObject.put("parts", partsArray)
            contentsArray.put(contentObject)
            jsonBody.put("contents", contentsArray)

            // 2. System Instruction
            if (systemInstruction.isNotEmpty()) {
                val systemInstructionObject = JSONObject().put(
                    "parts",
                    JSONArray().put(JSONObject().put("text", systemInstruction))
                )
                jsonBody.put("systemInstruction", systemInstructionObject)
            }

            // 3. Generation Config
            val generationConfig = JSONObject()
                .put("temperature", 0.4)
            jsonBody.put("generationConfig", generationConfig)

            val requestBodyString = jsonBody.toString()
            Log.d(TAG, "Request payload size: ${requestBodyString.length}")

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestBodyString.toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val responseString = response.body?.string() ?: ""
                Log.d(TAG, "Received raw response from Gemini API.")

                if (!response.isSuccessful) {
                    Log.e(TAG, "Gemini Request failed: ${response.code} - $responseString")
                    return@withContext "Error: Request failed with status code ${response.code}. Please ensure your API key is valid."
                }

                val responseJson = JSONObject(responseString)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    if (contentObj != null) {
                        val partsArr = contentObj.optJSONArray("parts")
                        if (partsArr != null && partsArr.length() > 0) {
                            return@withContext partsArr.getJSONObject(0).optString("text", "No response text found.")
                        }
                    }
                }
                return@withContext "Error: Received empty response from the Study Expert AI. Please try again."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call", e)
            return@withContext "Error: ${e.localizedMessage ?: "Connection timed out. Please check your network."}"
        }
    }
}
