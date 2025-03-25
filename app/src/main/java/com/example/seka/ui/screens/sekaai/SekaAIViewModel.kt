package com.example.seka.ui.screens.sekaai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.ChatMessageEntity
import com.example.seka.data.repository.ChatRepository
import com.example.seka.util.api.OpenAIService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date = Date()
)

data class SekaAIUiState(
    val messages: List<Message> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSpeaking: Boolean = false,
    val isListening: Boolean = false,
    val languageMode: LanguageMode = LanguageMode.INDONESIAN
)

enum class LanguageMode {
    INDONESIAN, ENGLISH
}

@HiltViewModel
class SekaAIViewModel @Inject constructor(
    private val openAIService: OpenAIService,
    private val chatRepository: ChatRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SekaAIUiState())
    val uiState: StateFlow<SekaAIUiState> = _uiState.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    init {
        loadMessages()
        initTextToSpeech()
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                updateTTSLanguage()

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        _uiState.update { it.copy(isSpeaking = false) }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _uiState.update { it.copy(isSpeaking = false) }
                    }
                })
            }
        }
    }

    private fun updateTTSLanguage() {
        val locale = when (_uiState.value.languageMode) {
            LanguageMode.INDONESIAN -> Locale("id", "ID")
            LanguageMode.ENGLISH -> Locale.US
        }

        val result = textToSpeech?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Jika bahasa tidak tersedia, gunakan default
            textToSpeech?.setLanguage(Locale.getDefault())
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

            speechRecognizer?.setRecognitionListener(object : android.speech.RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _uiState.update { it.copy(isListening = false) }
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        _uiState.update { it.copy(currentInput = text) }

                        // Auto-send setelah mengenali suara
                        sendMessage()
                    }
                }

                override fun onError(error: Int) {
                    _uiState.update {
                        it.copy(
                            isListening = false,
                            error = "Kesalahan dalam pengenalan suara: $error"
                        )
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } else {
            _uiState.update {
                it.copy(
                    error = "Pengenalan suara tidak tersedia di perangkat ini"
                )
            }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getAllMessages()
                .collect { entityList ->
                    val messages = entityList.map { entity ->
                        Message(
                            id = entity.id,
                            content = entity.content,
                            isFromUser = entity.isFromUser,
                            timestamp = entity.timestamp
                        )
                    }
                    _uiState.update { it.copy(messages = messages) }
                }
        }
    }

    fun updateCurrentInput(input: String) {
        _uiState.update { it.copy(currentInput = input) }
    }

    fun toggleLanguageMode() {
        val newMode = when (_uiState.value.languageMode) {
            LanguageMode.INDONESIAN -> LanguageMode.ENGLISH
            LanguageMode.ENGLISH -> LanguageMode.INDONESIAN
        }

        _uiState.update { it.copy(languageMode = newMode) }
        updateTTSLanguage()
    }

    fun sendMessage() {
        val message = _uiState.value.currentInput.trim()

        if (message.isBlank()) return

        val userMessage = Message(
            content = message,
            isFromUser = true
        )

        _uiState.update {
            it.copy(
                currentInput = "",
                isLoading = true,
                error = null
            )
        }

        // Simpan pesan user ke database
        saveMessageToDatabase(userMessage)

        getAIResponse(message)
    }

    private fun getAIResponse(userMessage: String) {
        viewModelScope.launch {
            try {
                val response = openAIService.chatWithAI(userMessage)

                val aiMessage = Message(
                    content = response,
                    isFromUser = false
                )

                // Simpan respon AI ke database
                saveMessageToDatabase(aiMessage)

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Terjadi kesalahan dalam berkomunikasi dengan AI",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatRepository.clearAllMessages()
            _uiState.update {
                it.copy(
                    currentInput = "",
                    error = null
                )
            }
        }
    }

    private fun saveMessageToDatabase(message: Message) {
        viewModelScope.launch {
            val entity = ChatMessageEntity(
                id = message.id,
                content = message.content,
                isFromUser = message.isFromUser,
                timestamp = message.timestamp
            )
            chatRepository.saveMessage(entity)
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }

        _uiState.update { it.copy(isListening = true) }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        // Set bahasa berdasarkan language mode
        val languageCode = when (_uiState.value.languageMode) {
            LanguageMode.INDONESIAN -> "id"
            LanguageMode.ENGLISH -> "en-US"
        }
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isListening = false,
                    error = "Tidak dapat memulai pengenalan suara: ${e.message}"
                )
            }
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

    fun speakMessage(message: Message) {
        if (message.isFromUser) return

        _uiState.update { it.copy(isSpeaking = true) }

        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "messageId"

        textToSpeech?.speak(
            message.content,
            TextToSpeech.QUEUE_FLUSH,
            params
        )
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
    }
}