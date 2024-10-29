package com.example.carpoolapp.model.Intents

sealed class UserIntent {
    data class SignUp(val email: String, val password: String) : UserIntent()
    data class LogIn(val email: String, val password: String) : UserIntent()
}
