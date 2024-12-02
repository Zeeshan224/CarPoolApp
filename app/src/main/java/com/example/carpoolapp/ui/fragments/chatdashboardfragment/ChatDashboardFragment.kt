package com.example.carpoolapp.ui.fragments.chatdashboardfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.carpoolapp.R
import com.example.carpoolapp.databinding.FragmentDashboardBinding
import com.example.carpoolapp.model.Group
import com.example.carpoolapp.model.intents.GroupChatIntent
import com.example.carpoolapp.model.states.GroupChatViewState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatDashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        // Observe ViewState
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewState.collectLatest { state ->
                when (state) {
                    is GroupChatViewState.Idle -> { /* Display idle state */ }
                    is GroupChatViewState.Loading -> { /* Show loading spinner */ }
                    is GroupChatViewState.Success -> showSuccess(state.message)
                    is GroupChatViewState.Error -> showError(state.error)
                    is GroupChatViewState.GroupChatGroupLoaded -> displayGroup(state.group)
                }
            }
        }

        binding.btnCreateGroup.setOnClickListener {
            val groupName = binding.etGroupName.text.toString().trim()
            lifecycleScope.launch {
                viewModel.userIntent.send(GroupChatIntent.CreateGroupChatGroup(groupName))
            }
        }

        binding.btnAddMember.setOnClickListener {
            val groupId = getString(R.string.group_id)
            val userId = getString(R.string.user_id)
            lifecycleScope.launch {
                viewModel.userIntent.send(GroupChatIntent.AddMember(groupId, userId))
            }
        }
    }

    private fun showSuccess(message: String) {
        // Show success message
        Toast.makeText(requireContext(), "Group Created Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showError(error: String) {
        // Show error message
        Toast.makeText(requireContext(), "Group Creation Failed", Toast.LENGTH_SHORT).show()
    }

    private fun displayGroup(group: Group) {
        // Display group details
    }
}
