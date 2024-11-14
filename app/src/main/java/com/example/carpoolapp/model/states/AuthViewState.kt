package com.example.carpoolapp.model.states

sealed class AuthViewState {
    data object Idle : AuthViewState()
    data object IsLoading : AuthViewState()
    data class Success(val message: String) : AuthViewState()
    data class Error(val error: String) : AuthViewState()
    data class ForgotPasswordSuccess(val message: String) : AuthViewState()
}
