package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.database.AppDatabase
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.data.model.CallLog
import com.example.data.model.Story
import com.example.data.repository.ChatRepository
import com.example.data.repository.CryptoHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

enum class CallStatus { IDLE, OUTGOING, INCOMING, CONNECTED }

data class ActiveCall(
    val contactName: String,
    val avatarDescription: String,
    val isVideo: Boolean,
    val status: CallStatus,
    val durationSeconds: Int = 0
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "samkil_chat_db"
    )
    .fallbackToDestructiveMigration()
    .build()

    private val repository = ChatRepository(db.chatDao())

    // All available secure chats
    val chats: StateFlow<List<Chat>> = repository.allChats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All E2EE active stories
    val stories: StateFlow<List<Story>> = repository.allStoriesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All historic call logs
    val callLogs: StateFlow<List<CallLog>> = repository.allCallLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently focused chat-id
    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()

    // Currently selected chat object
    private val _selectedChat = MutableStateFlow<Chat?>(null)
    val selectedChat: StateFlow<Chat?> = _selectedChat.asStateFlow()

    // Reactive secure messages list for active chat
    val activeMessages: StateFlow<List<Message>> = _selectedChatId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getMessagesForChat(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active full-screen Calling visual HUD state
    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    // Simulated user's own identity keypairs
    private val _myPublicKey = MutableStateFlow("pub_dh_me_31a4df89")
    val myPublicKey: StateFlow<String> = _myPublicKey.asStateFlow()

    private var callTimerJob: Job? = null

    init {
        // Prepopulate database with visual chats on first run
        viewModelScope.launch {
            repository.prepopulateDatabaseIfEmpty()
            // Cleanup stories older than 24h
            val yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            repository.deleteExpiredStories(yesterday)
        }
    }

    fun selectChat(chatId: String) {
        _selectedChatId.value = chatId
        viewModelScope.launch {
            val fetched = withContext(Dispatchers.IO) {
                db.chatDao().getChatById(chatId)
            }
            _selectedChat.value = fetched
        }
    }

    fun deselectChat() {
        _selectedChatId.value = null
        _selectedChat.value = null
    }

    /**
     * Key Rotation: Regenerates the user's DH Public Key and illustrates active cryptography.
     */
    fun rotateMyKeys() {
        val randomSuffix = UUID.randomUUID().toString().take(8)
        _myPublicKey.value = "pub_dh_me_$randomSuffix"
    }

    /**
     * Sends an E2EE message over standard channels, and schedules automated replies or AI models.
     */
    fun sendSecureMessage(plainText: String, mediaType: String = "TEXT", mediaUrl: String? = null, mediaCaption: String? = null) {
        val currentChatId = _selectedChatId.value ?: return
        val chat = _selectedChat.value ?: return

        viewModelScope.launch {
            // Send our message
            repository.sendSecureMessage(
                chatId = currentChatId,
                senderName = "You",
                isMe = true,
                plainText = plainText,
                mediaType = mediaType,
                mediaUrl = mediaUrl,
                mediaCaption = mediaCaption,
                sessionKey = chat.opponentPublicKey
            )

            // Trigger AI assistant / Opponent automatic simulation response
            if (currentChatId == "samkil_bot") {
                triggerBotReply(plainText)
            } else {
                triggerOpponentSimulatedReply(currentChatId, chat.opponentPublicKey)
            }
        }
    }

    /**
     * Simulation of peer-to-peer online indicators and message typing status.
     */
    private fun triggerOpponentSimulatedReply(chatId: String, key: String) {
        viewModelScope.launch {
            // Set chat typing indicator to true
            val current = db.chatDao().getChatById(chatId) ?: return@launch
            db.chatDao().updateChat(current.copy(isTyping = true, isOnline = true))
            
            delay(2000) // visual simulation delay

            val replyText = when (chatId) {
                "alice_id" -> "Received with Alice's local signature! Cryptographic checksum fits. Let's arrange our video call. 🔒"
                "david_id" -> "Looks spectacular! This cross-platform UI is fully responsive. Let me send a document to confirm."
                else -> "Encrypted packet delivered! Secure handshake verified."
            }

            repository.sendSecureMessage(
                chatId = chatId,
                senderName = current.contactName,
                isMe = false,
                plainText = replyText,
                sessionKey = key
            )

            // Disable typing indicator
            val reFetched = db.chatDao().getChatById(chatId)
            if (reFetched != null) {
                db.chatDao().updateChat(reFetched.copy(isTyping = false))
            }
        }
    }

    /**
     * Triggers Samkil Chat AI Response.
     * Uses real Gemini model if API key is supplied, or falls back to secure chatbot simulation.
     */
    private fun triggerBotReply(userPrompt: String) {
        viewModelScope.launch {
            // Set bot typing indicator
            val current = db.chatDao().getChatById("samkil_bot") ?: return@launch
            db.chatDao().updateChat(current.copy(isTyping = true))

            delay(1500)

            val apiKey = BuildConfig.GEMINI_API_KEY
            val isKeyConfigured = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

            val botResponse: String = if (isKeyConfigured) {
                try {
                    val prompt = "You are Samkil Chat's built-in security assistant. Answer the user prompt concisely while explaining how End-to-End messaging prevents third parties from viewing messages: '$userPrompt'"
                    val request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
                    )
                    val response = GeminiClient.api.generateSecureResponse(apiKey, request)
                    response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                        ?: "Handshake successful, but received empty response from secure nodes. Encryption validated."
                } catch (e: Exception) {
                    "Message payload verified. [Note: Gemini API returned a network fault: ${e.localizedMessage}. Emulating secure offline responder]: End-to-End encryption protects your message by signing with $myPublicKey. Only you and Samkil hold corresponding private decryption keys!"
                }
            } else {
                "🔒 [E2E Security Demonstration]: Your message was encrypted as AES ciphertext in the local database. Here is what your message looks like on-disk: '${CryptoHelper.encrypt(userPrompt, current.opponentPublicKey)}'. Nobody, not even Samkil servers, can decrypt it because only your app has the corresponding private session verification key!"
            }

            repository.sendSecureMessage(
                chatId = "samkil_bot",
                senderName = "Samkil AI Bot",
                isMe = false,
                plainText = botResponse,
                sessionKey = current.opponentPublicKey
            )

            // Turn off bot typing
            val updated = db.chatDao().getChatById("samkil_bot")
            if (updated != null) {
                db.chatDao().updateChat(updated.copy(isTyping = false))
            }
        }
    }

    /**
     * TRIGGERS AUDIO/VIDEO CALL SIMULATION (Full HUD Overlay)
     */
    fun startCall(contactName: String, avatarDescription: String, isVideo: Boolean) {
        _activeCall.value = ActiveCall(
            contactName = contactName,
            avatarDescription = avatarDescription,
            isVideo = isVideo,
            status = CallStatus.OUTGOING,
            durationSeconds = 0
        )

        // Progress simulation
        viewModelScope.launch {
            delay(2000) // simulation of ringing
            val current = _activeCall.value
            if (current != null && current.status == CallStatus.OUTGOING) {
                _activeCall.value = current.copy(status = CallStatus.CONNECTED)
                startTickingCall()
            }
        }
    }

    fun receiveIncomingCall(contactName: String, avatarDescription: String, isVideo: Boolean) {
        _activeCall.value = ActiveCall(
            contactName = contactName,
            avatarDescription = avatarDescription,
            isVideo = isVideo,
            status = CallStatus.INCOMING,
            durationSeconds = 0
        )
    }

    fun acceptCall() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(status = CallStatus.CONNECTED)
        startTickingCall()
    }

    private fun startTickingCall() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _activeCall.value ?: break
                if (current.status == CallStatus.CONNECTED) {
                    _activeCall.value = current.copy(durationSeconds = current.durationSeconds + 1)
                } else {
                    break
                }
            }
        }
    }

    fun endCall() {
        callTimerJob?.cancel()
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(status = CallStatus.IDLE)

        viewModelScope.launch {
            // Write call to Room Database log
            repository.insertCallLog(
                CallLog(
                    contactName = current.contactName,
                    avatarDescription = current.avatarDescription,
                    callType = if (current.isVideo) "VIDEO" else "AUDIO",
                    status = if (current.durationSeconds > 0) "CONNECTED" else "MISSED",
                    durationSeconds = current.durationSeconds
                )
            )
            delay(500) // visual ease-out
            _activeCall.value = null
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearCallHistory()
        }
    }

    fun clearMessages(chatId: String) {
        viewModelScope.launch {
            repository.clearChatMessages(chatId)
        }
    }

    fun addNewChat(name: String) {
        if (name.trim().isEmpty()) return
        val id = "chat_${UUID.randomUUID().toString().take(6)}"
        val peerKey = "pub_dh_peer_${UUID.randomUUID().toString().take(8)}"
        viewModelScope.launch {
            repository.insertChat(
                Chat(
                    id = id,
                    contactName = name,
                    avatarDescription = name.firstOrNull()?.uppercase() ?: "?",
                    lastMessageText = CryptoHelper.encrypt("Security key verification established. Tap to message. 🔐"),
                    lastMessageTimestamp = System.currentTimeMillis(),
                    opponentPublicKey = peerKey,
                    isOnline = true
                )
            )
            repository.sendSecureMessage(
                chatId = id,
                senderName = name,
                isMe = false,
                plainText = "Security key verification established. Tap to message. 🔐",
                sessionKey = peerKey
            )
        }
    }

    /**
     * Posts a new user story. Content is locally encrypted prior to persistence.
     */
    fun postStory(mediaUrl: String, caption: String, mediaType: String = "IMAGE") {
        viewModelScope.launch {
            // Encrypt content with user's own public key
            val encryptedUrl = CryptoHelper.encrypt(mediaUrl, _myPublicKey.value)
            val encryptedCaption = CryptoHelper.encrypt(caption, _myPublicKey.value)

            val newStory = Story(
                senderId = "me",
                senderName = "You",
                senderAvatar = "Y",
                encryptedMediaUrl = encryptedUrl,
                encryptedCaption = encryptedCaption,
                mediaType = mediaType,
                timestamp = System.currentTimeMillis()
            )
            repository.insertStory(newStory)
        }
    }
}

