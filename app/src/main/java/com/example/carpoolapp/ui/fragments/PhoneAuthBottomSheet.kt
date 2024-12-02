package com.example.carpoolapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.carpoolapp.databinding.FragmentPhoneAuthBinding
import com.example.carpoolapp.model.states.AuthViewState
import com.example.carpoolapp.ui.fragments.signinfragment.SignInFragmentDirections
import com.example.carpoolapp.ui.fragments.signinfragment.SignInViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class PhoneAuthBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentPhoneAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.etOtp.visibility = View.GONE
        binding.btnVerifyOtp.visibility = View.GONE

        binding.btnPhoneSignIn.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                viewModel.senderVerificationCode(phoneNumber)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Handle OTP verification
        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()
            if (otp.isNotEmpty()) {
                val verificationId = viewModel.getVerificationId()
                if (verificationId != null) {
                    viewModel.verifyOTP(otp, verificationId)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Verification ID not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
                else {
                Toast.makeText(requireContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AuthViewState.Idle -> Unit
                        is AuthViewState.IsLoading -> {}
                        is AuthViewState.PhoneVerificationPhoneSent -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                            binding.etOtp.visibility = View.VISIBLE
                            binding.btnVerifyOtp.visibility = View.VISIBLE
                        }

                        is AuthViewState.Success -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                            val action =
                                SignInFragmentDirections.actionSignInFragmentToChatRoomFragment()
                            findNavController().navigate(action)
                            dismiss()
                        }

                        is AuthViewState.Error -> {
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        }

                        is AuthViewState.ForgotPasswordSuccess -> {}
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


