package com.example.carpoolapp.ui.fragments.signupfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.carpoolapp.databinding.FragmentSignUpBinding
import com.example.carpoolapp.model.Intents.UserIntent
import com.example.carpoolapp.model.States.ViewState
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.btnSignUp.setOnClickListener {
            val email = binding.email.editText?.text.toString().trim()
            val password = binding.password.editText?.text.toString().trim()
            lifecycleScope.launch {
                viewModel.userIntent.send(UserIntent.SignUp(email, password))
            }
        }
        binding.tvSignIn.setOnClickListener{
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when(state){
                    ViewState.Idle -> {
                        binding.pbLoading.visibility = View.GONE
                    }
                    ViewState.IsLoading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    is ViewState.Success -> {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()

                        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
