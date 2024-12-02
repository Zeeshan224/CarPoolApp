package com.example.carpoolapp.model.intents

sealed class AuthIntent {
    data class SignUp(val userName: String,val email: String, val password: String) : AuthIntent()
    data class LogIn(val email: String, val password: String) : AuthIntent()
    data class ForgotPassword(val email: String) : AuthIntent()
    data class GoogleSignIn(val idToken: String) : AuthIntent()
    data class PhoneSignIn(val phoneNumber: String): AuthIntent()
    data class VerifyOTP(val otp: String, val verificationCode: String) : AuthIntent()
}
