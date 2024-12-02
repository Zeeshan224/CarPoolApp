package com.example.carpoolapp.ui.fragments.signinfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.intents.AuthIntent
import com.example.carpoolapp.model.states.AuthViewState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow<AuthViewState>(AuthViewState.Idle)
    val state: StateFlow<AuthViewState> get() = _state
    val authIntent = Channel<AuthIntent>(Channel.UNLIMITED)
    private var _verificationId: String? = null

    private val auth = FirebaseAuth.getInstance()

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            authIntent.consumeAsFlow().collect{ intent ->
                when (intent) {
                    is AuthIntent.LogIn -> logIn(intent.email, intent.password)
                    is AuthIntent.ForgotPassword -> forgotPassword(intent.email)
                    is AuthIntent.GoogleSignIn -> googleSignIn(intent.idToken)
                    is AuthIntent.PhoneSignIn -> senderVerificationCode(intent.phoneNumber)
                    is AuthIntent.VerifyOTP -> verifyOTP(intent.otp,intent.verificationCode)
                    is AuthIntent.SignUp -> {}
                }
            }
        }
    }

    fun senderVerificationCode(phoneNumber: String){
        _state.value = AuthViewState.IsLoading

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _state.value = AuthViewState.Error("Verification failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    _state.value = AuthViewState.PhoneVerificationPhoneSent("OTP sent to $phoneNumber")
                    _verificationId = verificationId
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOTP(otp: String, verificationId: String) {
        _state.value = AuthViewState.IsLoading
        val credential = PhoneAuthProvider.getCredential(verificationId ?: "", otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = AuthViewState.Success("Phone number verification successful!")

                } else {
                    _state.value = AuthViewState.Error("Phone sign-in failed: ${task.exception?.message}")
                }
            }
        }

    private fun googleSignIn(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        _state.value = AuthViewState.IsLoading
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = AuthViewState.Success("Google Sign-In Successful!")
                } else {
                    _state.value =
                        AuthViewState.Error(task.exception?.message ?: "Google Sign-In Failed.")
                }
            }
        }

    fun getVerificationId(): String? {
        return _verificationId
    }

    private suspend fun logIn(email: String, password: String) {
        _state.value = AuthViewState.IsLoading
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser

            if (user != null && user.isEmailVerified) {
                _state.value = AuthViewState.Success("Login Successful!")
            } else {
                auth.signOut() // Sign out unverified users
                _state.value = AuthViewState.Error("Please verify your email before logging in.")
            }
        } catch (e: Exception) {
            _state.value = AuthViewState.Error("Login Failed: ${e.message}")
        }
    }

    private fun forgotPassword(email: String) {
        _state.value = AuthViewState.IsLoading
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    _state.value = AuthViewState.ForgotPasswordSuccess("Password reset email  sent")
                }
                else{
                    _state.value = AuthViewState.Error(task.exception?.message ?: "Failed to send reset email")
                }
            }
    }
}
