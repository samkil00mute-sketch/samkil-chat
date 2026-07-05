package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey val id: String,
    val contactName: String,
    val avatarDescription: String,
    val lastMessageText: String, // Encrypted payload format
    val lastMessageTimestamp: Long,
    val opponentPublicKey: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false
) : Serializable

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: String,
    val senderName: String,
    val isMe: Boolean,
    val encryptedPayload: String, // The E2E AES Encrypted ciphertext representation
    val decryptedText: String,    // The clear text displayed upon client-side E2E decryption
    val timestamp: Long = System.currentTimeMillis(),
    val mediaType: String = "TEXT", // TEXT, IMAGE, AUDIO, DOCUMENT
    val mediaUrl: String? = null,
    val mediaCaption: String? = null,
    val isEncrypted: Boolean = true
) : Serializable

@Entity(tableName = "calls")
data class CallLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contactName: String,
    val avatarDescription: String,
    val timestamp: Long = System.currentTimeMillis(),
    val callType: String = "VIDEO", // AUDIO, VIDEO
    val status: String = "CONNECTED", // OUTGOING, INCOMING, MISSED, CONNECTED
    val durationSeconds: Int = 0
) : Serializable

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val encryptedMediaUrl: String, // E2EE encrypted Unsplash URL or placeholder
    val encryptedCaption: String,  // E2EE encrypted caption text
    val timestamp: Long = System.currentTimeMillis(),
    val mediaType: String = "IMAGE", // IMAGE, VIDEO
    val durationSeconds: Int = 5
) : Serializable

