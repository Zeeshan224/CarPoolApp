package com.example.carpoolapp.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carpoolapp.R
import com.example.carpoolapp.databinding.ActivityOtpactivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpactivityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Button to send OTP
        binding.btnSendOtp.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(phoneNumber)
            } else {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
            }
        }

        // Button to verify OTP
        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()
            if (otp.isNotEmpty()) {
                verifyCode(otp)
            } else {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically verify OTP
                    val code = credential.smsCode
                    if (code != null) {
                        binding.etOtp.setText(code)
                        verifyCode(code)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OTPActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Save verification ID and resending token
                    storedVerificationId = verificationId
                    resendToken = token
                    Toast.makeText(this@OTPActivity, "Code sent", Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // OTP verified successfully
                    Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}