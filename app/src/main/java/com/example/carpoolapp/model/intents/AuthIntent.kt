package com.example.carpoolapp.model.intents

sealed class AuthIntent {
    data class SignUp(val userName: String,val email: String, val password: String) : AuthIntent()
    data class LogIn(val email: String, val password: String) : AuthIntent()
    data class ForgotPassword(val email: String) : AuthIntent()
}
