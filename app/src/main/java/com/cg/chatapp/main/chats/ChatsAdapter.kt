package com.cg.chatapp.main.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cg.chatapp.databinding.ChatsItemBinding

class ChatsAdapter(
    private val chatsViewModel: ChatsViewModel,
    private val chatsLifeCycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val chats = mutableListOf<ChatsItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = ChatsItemBinding.inflate(layoutInflater, parent, false)
        return ChatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatsViewHolder).setChats(chats[position])
    }

    override fun getItemCount(): Int = chats.size

    fun submitList(chats: List<ChatsItemViewModel>) {
        val diffCallback = ChatsDiffCallback(this.chats, chats)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.chats.apply {
            clear()
            addAll(chats)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    private inner class ChatsViewHolder(
        private val binding: ChatsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setChats(chatsItemViewModel: ChatsItemViewModel) {
            binding.apply {
                viewModel = chatsItemViewModel
                chats = chatsViewModel
                lifecycleOwner = chatsLifeCycleOwner
            }
        }
    }

    private inner class ChatsDiffCallback(
        private val oldItems: List<ChatsItemViewModel>,
        private val newItems: List<ChatsItemViewModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].employeeName == newItems[newItemPosition].employeeName

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].message == newItems[newItemPosition].message
    }
}