package com.example.carpoolapp.ui.fragments.chatroomfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.carpoolapp.R
import com.example.carpoolapp.databinding.ItemChatRoomBinding
import com.example.carpoolapp.model.states.ChatRoom
import com.example.carpoolapp.ui.fragments.chatfragment.ChatAdapter
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class ChatRoomAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<ChatRoom, ChatRoomAdapter.ChatRoomViewHolder>(ChatRoomDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatRoomAdapter.ChatRoomViewHolder {
        val binding = ItemChatRoomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatRoom: ChatRoom) {
            binding.apply {
                tvUserName.text = chatRoom.user.name

                if (chatRoom.lastMessage != null) {
                    tvLastMessage.text = chatRoom.lastMessage.content
                    tvTimestamp.text = formatTimeStamp(chatRoom.lastMessage.timeStamp)
                    tvTimestamp.visibility = View.VISIBLE
                    messageStatus.text =
                        if (chatRoom.lastMessage.isSent) "Sent" else "Received"
                    messageStatus.visibility = View.VISIBLE
                }
                else{
                    tvLastMessage.text = itemView.context.getString(R.string.no_message)
                    tvTimestamp.visibility = View.GONE
                    messageStatus.visibility = View.GONE
                }
                root.setOnClickListener{
                    onItemClick(chatRoom.user.id)
                }
            }
        }

        private fun formatTimeStamp(timestamp: Timestamp): String {
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return dateFormat.format(timestamp.toDate())
        }
    }

    class ChatRoomDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.user.id == newItem.user.id
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }
    }
}