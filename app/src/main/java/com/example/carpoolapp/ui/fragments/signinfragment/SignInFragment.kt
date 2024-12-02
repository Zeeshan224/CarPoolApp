package com.example.carpoolapp.ui.fragments.signinfragment

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.carpoolapp.R
import com.example.carpoolapp.databinding.FragmentSignInBinding
import com.example.carpoolapp.model.intents.AuthIntent
import com.example.carpoolapp.model.states.AuthViewState
import com.example.carpoolapp.ui.fragments.PhoneAuthBottomSheet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

class SignInFragment : Fragment(R.layout.fragment_phone_auth) {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignInViewModel by viewModels()

    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(Exception::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    lifecycleScope.launch {
                        viewModel.authIntent.send(AuthIntent.GoogleSignIn(idToken))
                    }
                } else {
                    Toast.makeText(requireContext(), "Google sign-in failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Google sign-in failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyGradientToTextView()
        observeViewModel()

        binding.btnSignIn.setOnClickListener {
            val email = binding.email.editText?.text.toString().trim()
            val password = binding.password.editText?.text.toString().trim()
            lifecycleScope.launch {
                viewModel.authIntent.send(AuthIntent.LogIn(email, password))
            }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnNumberSignIn.setOnClickListener {
            val bottomSheetFragment = PhoneAuthBottomSheet()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        binding.tvSignUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.forgotPassword.setOnClickListener {
            val email = binding.email.editText?.text.toString().trim()
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.authIntent.send(AuthIntent.ForgotPassword(email))
                }
            } else {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun signInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun applyGradientToTextView() {
        val textView = binding.logo
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())

        val startColor = ContextCompat.getColor(requireContext(), R.color.secondary)
        val endColor = ContextCompat.getColor(requireContext(), R.color.smoke_gray)

        val textShader = LinearGradient(
            0f, 0f, width, textView.textSize,
            intArrayOf(startColor, endColor),
            null,
            Shader.TileMode.MIRROR
        )
        textView.paint.shader = textShader
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        AuthViewState.Idle -> {
                            binding.pbLoading.visibility = View.GONE
                        }

                        AuthViewState.IsLoading -> {
                            binding.pbLoading.visibility = View.VISIBLE
                        }

                        is AuthViewState.Success -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()

                            val action =
                                SignInFragmentDirections.actionSignInFragmentToChatRoomFragment()
                            findNavController().navigate(action)
                        }

                        is AuthViewState.Error -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        }

                        is AuthViewState.ForgotPasswordSuccess -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        is AuthViewState.PhoneVerificationPhoneSent -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


