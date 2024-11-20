package com.example.carpoolapp.model.data

import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Timestamp

data class ChatMessage(
    val content: String,
    val senderId: String,
    val timestamp: Timestamp
)
