package com.example.carpoolapp.model

data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val createdBy: String = "",
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val members: Map<String, String> = mapOf()
)
