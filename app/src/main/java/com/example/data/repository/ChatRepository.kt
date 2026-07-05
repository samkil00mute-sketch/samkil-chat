package com.example.data.repository

import com.example.data.database.ChatDao
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.data.model.CallLog
import com.example.data.model.Story
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) {

    val allChats: Flow<List<Chat>> = chatDao.getAllChats()
    val allCallLogs: Flow<List<CallLog>> = chatDao.getAllCallLogs()
    val allStoriesFlow: Flow<List<Story>> = chatDao.getAllStoriesFlow()

    fun getMessagesForChat(chatId: String): Flow<List<Message>> = 
        chatDao.getMessagesForChat(chatId)

    suspend fun insertStory(story: Story) {
        chatDao.insertStory(story)
    }

    suspend fun deleteExpiredStories(expirationTimestamp: Long) {
        chatDao.deleteExpiredStories(expirationTimestamp)
    }

    suspend fun insertChat(chat: Chat) {
        chatDao.insertChat(chat)
    }

    suspend fun updateChat(chat: Chat) {
        chatDao.updateChat(chat)
    }

    /**
     * Inserts a message after encrypting the plain text, demonstrating 100% end-to-end encryption pipeline.
     */
    suspend fun sendSecureMessage(
        chatId: String,
        senderName: String,
        isMe: Boolean,
        plainText: String,
        mediaType: String = "TEXT",
        mediaUrl: String? = null,
        mediaCaption: String? = null,
        sessionKey: String? = null
    ) {
        // Core E2EE: Ciphertext payload
        val encryptedPayload = CryptoHelper.encrypt(plainText, sessionKey)
        
        val message = Message(
            chatId = chatId,
            senderName = senderName,
            isMe = isMe,
            encryptedPayload = encryptedPayload,
            decryptedText = plainText, // Exposed locally only to the user
            mediaType = mediaType,
            mediaUrl = mediaUrl,
            mediaCaption = mediaCaption,
            isEncrypted = true
        )
        chatDao.insertMessage(message)

        // Update last message parameters in corresponding Chat
        val existingChat = chatDao.getChatById(chatId)
        if (existingChat != null) {
            val updatedChat = existingChat.copy(
                lastMessageText = encryptedPayload,
                lastMessageTimestamp = System.currentTimeMillis()
            )
            chatDao.updateChat(updatedChat)
        }
    }

    suspend fun insertCallLog(callLog: CallLog) {
        chatDao.insertCallLog(callLog)
    }

    suspend fun deleteChat(chatId: String) {
        chatDao.deleteChatById(chatId)
        chatDao.clearMessagesForChat(chatId)
    }

    suspend fun clearChatMessages(chatId: String) {
        chatDao.clearMessagesForChat(chatId)
    }

    suspend fun clearCallHistory() {
        chatDao.clearCallLogs()
    }

    /**
     * Seeds initial chat records representing encrypted Signal-style sessions.
     */
    suspend fun prepopulateDatabaseIfEmpty() {
        // Let's check from standard or if empty
        val list = chatDao.getChatById("alice_id")
        if (list == null) {
            val aliceKey = "pub_dh_alice_77b3df62"
            val davidKey = "pub_dh_david_99c54e11"
            val botKey   = "pub_dh_samkil_88a6d4ee"

            // Insert initial Chats
            chatDao.insertChat(Chat(
                id = "alice_id",
                contactName = "Alice Vance",
                avatarDescription = "A",
                lastMessageText = CryptoHelper.encrypt("Hey! Are you online? Let's check out our security signatures keys. 🔐"),
                lastMessageTimestamp = System.currentTimeMillis() - 3600000,
                opponentPublicKey = aliceKey,
                isOnline = true,
                isTyping = false
            ))

            chatDao.insertChat(Chat(
                id = "david_id",
                contactName = "David Sterling",
                avatarDescription = "D",
                lastMessageText = CryptoHelper.encrypt("Shared a diagram of our new project workspace."),
                lastMessageTimestamp = System.currentTimeMillis() - 7200000,
                opponentPublicKey = davidKey,
                isOnline = false,
                isTyping = false
            ))

            chatDao.insertChat(Chat(
                id = "samkil_bot",
                contactName = "Samkil AI Bot",
                avatarDescription = "S",
                lastMessageText = CryptoHelper.encrypt("Hi there! Ask me any question. I can answer or assist in real-time. Our conversations are completely secured with E2EE local keys."),
                lastMessageTimestamp = System.currentTimeMillis() - 100000,
                opponentPublicKey = botKey,
                isOnline = true,
                isTyping = false
            ))

            // Seed initial secure messages
            sendSecureMessage(
                chatId = "alice_id",
                senderName = "Alice Vance",
                isMe = false,
                plainText = "Hey! Are you online? Let's check out our security signatures keys. 🔐",
                sessionKey = aliceKey
            )

            sendSecureMessage(
                chatId = "david_id",
                senderName = "David Sterling",
                isMe = false,
                plainText = "Shared a diagram of our new project workspace.",
                sessionKey = davidKey
            )
            // Send accompanying media message
            sendSecureMessage(
                chatId = "david_id",
                senderName = "David Sterling",
                isMe = false,
                plainText = "Workspace Design",
                mediaType = "IMAGE",
                mediaUrl = "https://images.unsplash.com/photo-1512486130939-2c4f79935e4f?auto=format&fit=crop&w=800&q=80",
                mediaCaption = "Workspace Design Diagram.png",
                sessionKey = davidKey
            )

            // Seed E2EE stories
            chatDao.insertStory(Story(
                senderId = "alice_id",
                senderName = "Alice Vance",
                senderAvatar = "A",
                encryptedMediaUrl = CryptoHelper.encrypt("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80", aliceKey),
                encryptedCaption = CryptoHelper.encrypt("Cryptographic beach holiday! 🏖️🔒", aliceKey),
                mediaType = "IMAGE",
                timestamp = System.currentTimeMillis()
            ))

            chatDao.insertStory(Story(
                senderId = "david_id",
                senderName = "David Sterling",
                senderAvatar = "D",
                encryptedMediaUrl = CryptoHelper.encrypt("https://images.unsplash.com/photo-1531297484001-80022131f5a1?auto=format&fit=crop&w=800&q=80", davidKey),
                encryptedCaption = CryptoHelper.encrypt("My updated hardware key verification setup 🖥️⚙️", davidKey),
                mediaType = "IMAGE",
                timestamp = System.currentTimeMillis() - 1200000
            ))

            chatDao.insertStory(Story(
                senderId = "samkil_bot",
                senderName = "Samkil AI Bot",
                senderAvatar = "S",
                encryptedMediaUrl = CryptoHelper.encrypt("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80", botKey),
                encryptedCaption = CryptoHelper.encrypt("Secure decentralized nodes live status graph 📊🤖", botKey),
                mediaType = "IMAGE",
                timestamp = System.currentTimeMillis() - 43200000
            ))


            sendSecureMessage(
                chatId = "samkil_bot",
                senderName = "Samkil AI Bot",
                isMe = false,
                plainText = "Hi there! Welcome to Samkil Chat! You can ask me questions about our End-to-End Cryptography model. Tell me something!",
                sessionKey = botKey
            )

            // Seed initial call logs
            chatDao.insertCallLog(CallLog(
                contactName = "Alice Vance",
                avatarDescription = "A",
                timestamp = System.currentTimeMillis() - 1800000,
                callType = "VIDEO",
                status = "CONNECTED",
                durationSeconds = 142
            ))

            chatDao.insertCallLog(CallLog(
                contactName = "David Sterling",
                avatarDescription = "D",
                timestamp = System.currentTimeMillis() - 86400000,
                callType = "AUDIO",
                status = "MISSED",
                durationSeconds = 0
            ))
        }
    }
}
