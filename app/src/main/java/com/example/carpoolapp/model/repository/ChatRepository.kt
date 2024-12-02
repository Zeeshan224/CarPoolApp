package com.example.carpoolapp.model.repository

import com.example.carpoolapp.model.data.Chat
import com.example.carpoolapp.model.data.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun initiateChat(userId: String, recipientId: String, onComplete: (String) -> Unit) {
        val chatId =
            if (userId < recipientId) "${userId}_$recipientId" else "${recipientId}_$userId"

        val chatData = mapOf(
            "participants" to listOf(userId, recipientId),
            "messages" to emptyList<Message>()
        )

        firestore.collection("chats").document(chatId)
            .set(chatData)
            .addOnSuccessListener { onComplete(chatId) }
            .addOnFailureListener { onComplete("") }
    }

    fun generateChatId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) {
            "${user1Id}_${user2Id}"
        } else {
            "${user2Id}_${user1Id}"
        }
    }

//    suspend fun getChatMessages(chatId: String): List<Message> {
//        return try {
//            val snapshot = firestore.collection("chats")
//                .document(chatId)
//                .get()
//                .await()
//
//            val messages = snapshot.get("messages") as? List<Message> ?: emptyList()
//            messages
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

//    suspend fun getChat(chatId: String): Chat? {
//        return try {
//            val snapshot = firestore.collection("chats")
//                .document(chatId)
//                .get()
//                .await()
//
//            val participants = snapshot.get("participants") as? List<String>
//            val messages = snapshot.get("messages") as? List<Message> ?: emptyList()
//
//            if (participants != null) {
//                Chat(chatId, participants, messages)
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
}

