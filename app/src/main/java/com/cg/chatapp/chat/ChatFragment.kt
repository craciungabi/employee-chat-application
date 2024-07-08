package com.cg.chatapp.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cg.chatapp.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private val args: ChatFragmentArgs by navArgs()

    private var fragmentBinding: FragmentChatBinding? = null
    private val binding get() = fragmentBinding!!

    private val viewModel: ChatViewModel by lazy {
        ChatViewModel(activity?.application!!, args.username)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fragmentBinding == null) {
            fragmentBinding = FragmentChatBinding.inflate(layoutInflater, container, false)

            activity?.title = args.username

            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        startCollectingStates()
    }

    private fun setupRecyclerView() {
        val linerLayoutManager = LinearLayoutManager(context).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        binding.chatRv.apply {
            adapter = ChatAdapter(viewLifecycleOwner)
            layoutManager = linerLayoutManager
        }
        binding.chatRv.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            binding.chatRv.scrollToPosition(0)
        }
    }

    private fun startCollectingStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.messages.collect {
                        (binding.chatRv.adapter as ChatAdapter).submitList(it)
                        binding.chatRv.scrollToPosition(0)
                    }
                }
            }
        }
    }
}