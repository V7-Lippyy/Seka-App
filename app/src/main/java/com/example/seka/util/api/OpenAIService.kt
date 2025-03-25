package com.example.seka.util.api

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIService @Inject constructor(
    private val openAI: OpenAI
) {
    suspend fun generateSummary(text: String): String {
        val prompt = "Ringkaskan teks berikut dengan tetap mempertahankan poin-poin penting: $text"
        return getChatCompletion(prompt)
    }

    suspend fun generateParaphrase(text: String): String {
        val prompt = "Parafrasalah teks berikut dengan menggunakan kalimat dan struktur yang berbeda namun tetap mempertahankan makna yang sama: $text"
        return getChatCompletion(prompt)
    }

    suspend fun translate(text: String, sourceLanguage: String, targetLanguage: String): String {
        val prompt = "Terjemahkan teks berikut dari $sourceLanguage ke $targetLanguage: $text"
        return getChatCompletion(prompt)
    }

    suspend fun chatWithAI(message: String): String {
        // Check if the message is asking about the app creator
        if (isAskingAboutCreator(message)) {
            return "Aplikasi ini diciptakan oleh developer indie bernama Muhammad Alif Qadri."
        }

        // Check if the message is asking about Alif or Muhammad Alif Qadri
        if (isAskingAboutAlif(message)) {
            return "Dialah yang memprogram aplikasi ini dan dia juga merupakan seorang mahasiswa biasa di Universitas Negeri Makassar."
        }

        return getChatCompletion(message)
    }

    // Helper function to check if the message is asking about the app creator
    private fun isAskingAboutCreator(message: String): Boolean {
        val lowercaseMessage = message.lowercase()
        val creatorKeywords = listOf(
            "siapa yang buat aplikasi ini",
            "siapa yang menciptakan aplikasi ini",
            "siapa pembuat aplikasi ini",
            "siapa developer aplikasi ini",
            "aplikasi ini dibuat oleh siapa",
            "aplikasi ini dikembangkan oleh siapa"
        )

        return creatorKeywords.any { keyword -> lowercaseMessage.contains(keyword) }
    }

    // Helper function to check if the message is asking about Alif
    private fun isAskingAboutAlif(message: String): Boolean {
        val lowercaseMessage = message.lowercase()
        val alifKeywords = listOf(
            "siapa alif",
            "siapa muhammad alif qadri",
            "siapa muhammad alif",
            "alif qadri siapa",
            "ceritakan tentang alif",
            "ceritakan tentang muhammad alif qadri"
        )

        return alifKeywords.any { keyword -> lowercaseMessage.contains(keyword) }
    }

    // Implementasi placeholder untuk text-to-speech, fungsi ini akan dikembangkan nanti
    suspend fun textToSpeech(text: String): ByteArray {
        // Placeholder sederhana, ini akan disiapkan dengan implementasi asli nanti
        return ByteArray(0)
    }

    // Implementasi placeholder untuk speech-to-text, fungsi ini akan dikembangkan nanti
    suspend fun speechToText(audioData: ByteArray): String {
        // Placeholder sederhana, ini akan disiapkan dengan implementasi asli nanti
        return "Transkripsi audio akan ditampilkan di sini"
    }

    private suspend fun getChatCompletion(prompt: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "Kamu adalah asisten AI yang membantu menganalisis dan meringkas teks."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        return completion.choices.first().message.content ?: "Maaf, tidak ada respons yang dihasilkan."
    }

    fun streamChatCompletion(prompt: String): Flow<String> = flow {
        // Check if the prompt is asking about the app creator or about Alif
        if (isAskingAboutCreator(prompt)) {
            emit("Aplikasi ini diciptakan oleh developer indie bernama Muhammad Alif Qadri.")
            return@flow
        }

        if (isAskingAboutAlif(prompt)) {
            emit("Dialah yang memprogram aplikasi ini dan dia juga merupakan seorang mahasiswa biasa di Universitas Negeri Makassar.")
            return@flow
        }

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "Kamu adalah asisten AI yang membantu menganalisis dan meringkas teks."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )

        openAI.chatCompletions(chatCompletionRequest).collect { chunk ->
            val content = chunk.choices.firstOrNull()?.delta?.content
            if (content != null) {
                emit(content)
            }
        }
    }.flowOn(Dispatchers.IO)
}