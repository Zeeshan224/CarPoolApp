package com.example.carpoolapp.model.States

sealed class ViewState {
    data object Idle : ViewState()
    data object IsLoading : ViewState()
    data class Success(val message: String) : ViewState()
    data class Error(val error: String) : ViewState()
}