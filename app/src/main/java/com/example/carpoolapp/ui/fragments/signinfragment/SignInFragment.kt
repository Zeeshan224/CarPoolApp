package com.example.carpoolapp.ui.fragments.signinfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.carpoolapp.databinding.FragmentSignInBinding
import com.example.carpoolapp.model.Intents.UserIntent
import com.example.carpoolapp.model.States.ViewState
import com.example.carpoolapp.ui.fragments.signupfragment.SignUpViewModel
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.btnSignIn.setOnClickListener {
            val email = binding.email.editText?.text.toString().trim()
            val password = binding.password.editText?.text.toString().trim()
            lifecycleScope.launch {
                viewModel.userIntent.send(UserIntent.LogIn(email, password))
            }
        }

        binding.tvSignUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        ViewState.Idle -> {
                            binding.pbLoading.visibility = View.GONE
                        }

                        ViewState.IsLoading -> {
                            binding.pbLoading.visibility = View.VISIBLE
                        }

                        is ViewState.Success -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()

                            val action =
                                SignInFragmentDirections.actionSignInFragmentToDashboardFragment()
                            findNavController().navigate(action)
                        }

                        is ViewState.Error -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        }
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
