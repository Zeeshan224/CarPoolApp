package com.example.carpoolapp.model.data

import android.adservices.topics.EncryptedTopic
import com.google.firebase.Timestamp
import java.util.Date

data class Message(
    val id: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
)

