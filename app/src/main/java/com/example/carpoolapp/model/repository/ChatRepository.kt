package com.example.carpoolapp.model.repository

import com.example.carpoolapp.model.data.Message
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository(private val firestore: FirebaseFirestore) {

    fun initiateChat(userId: String, recipientId: String, onComplete: (String) -> Unit) {
        val chatId = if (userId < recipientId) "${userId}_$recipientId" else "${recipientId}_$userId"

        val chatData = mapOf(
            "participants" to listOf(userId, recipientId),
            "messages" to emptyList<Message>()
        )

        firestore.collection("chats").document(chatId)
            .set(chatData)
            .addOnSuccessListener { onComplete(chatId) }
            .addOnFailureListener { onComplete("") }

    }
}
