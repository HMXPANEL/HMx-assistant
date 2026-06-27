package dev.krinry.jarvis.ai

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * OpenRouter Provider — 300+ models, many free, OpenAI-compatible.
 * API: openrouter.ai
 */
class OpenRouterProvider : LlmProvider {
    override val id = "openrouter"
    override val displayName = "OpenRouter"
    override val baseUrl = "https://openrouter.ai/api/v1"
    override val defaultModel = "google/gemini-2.0-flash-exp:free"
    override val defaultFallbackModel = "meta-llama/llama-3.3-70b-instruct:free"

    override fun extraHeaders(): Map<String, String> = mapOf(
        "HTTP-Referer" to "https://jarvis.krinry.dev",
        "X-Title" to "Jarvis AI Agent"
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        suspend fun chatMessage(
            context: Context,
            model: String,
            systemPrompt: String,
            userMessage: String
        ): String? {
            val apiKey = dev.krinry.jarvis.security.SecureKeyStore.getProviderApiKey(context, "openrouter")
                ?: return null
            
            val requestBody = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userMessage)
                    })
                })
                put("max_tokens", 1024)
                put("temperature", 0.7)
            }.toString()
            
            return try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
                
                val request = Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://jarvis.krinry.dev")
                    .addHeader("X-Title", "Jarvis AI Agent")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()
                
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }
                
                val responseBody = response.body?.string() ?: return null
                val json = JSONObject(responseBody)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun fetchModels(apiKey: String): List<ModelInfo> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/models")
                .get().build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return emptyList()

            val body = response.body?.string() ?: return emptyList()
            val data = JSONObject(body).optJSONArray("data") ?: return emptyList()

            val models = mutableListOf<ModelInfo>()
            for (i in 0 until data.length()) {
                val model = data.getJSONObject(i)
                val modelId = model.optString("id", "")
                val name = model.optString("name", modelId)
                val pricing = model.optJSONObject("pricing")
                val promptPrice = pricing?.optString("prompt", "0")?.toDoubleOrNull() ?: 0.0
                val isFree = promptPrice == 0.0 || modelId.contains(":free")
                val contextLength = model.optInt("context_length", 0)

                if (modelId.isNotEmpty()) {
                    models.add(ModelInfo(
                        id = modelId, name = name,
                        isFree = isFree, contextLength = contextLength
                    ))
                }
            }
            models.sortedWith(compareByDescending<ModelInfo> { it.isFree }.thenBy { it.name })
        } catch (e: Exception) {
            Log.e("OpenRouterProvider", "Fetch models failed", e)
            emptyList()
        }
    }
}
