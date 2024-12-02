package com.example.carpoolapp.model.data

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

