package com.cg.chatapp.main.chats

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cg.chatapp.R
import com.cg.chatapp.SharedPrefsRepository
import com.cg.chatapp.databinding.FragmentChatsBinding
import com.cg.chatapp.login.LoginActivity
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {
    private var fragmentBinding: FragmentChatsBinding? = null
    private val binding get() = fragmentBinding!!

    val viewModel: ChatsViewModel by lazy {
        ChatsViewModel(activity?.application!!)
    }

    private val prefs: SharedPrefsRepository by lazy {
        SharedPrefsRepository(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fragmentBinding == null) {
            fragmentBinding = FragmentChatsBinding.inflate(layoutInflater, container, false)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_action, menu)
        menu.findItem(R.id.filter).isVisible = false
        menu.findItem(R.id.profile).isVisible = false

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.log_out) {
            logOutUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOutUser() {
        prefs.logOutUser()

        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun setupRecyclerView() {
        binding.chatsRv.apply {
            adapter = ChatsAdapter(viewModel, viewLifecycleOwner)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun startCollectingStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.chats.collect {
                        (binding.chatsRv.adapter as ChatsAdapter).submitList(it)
                    }
                }
                launch {
                    viewModel.navigateToChats.collect {
                        findNavController().navigate(
                            ChatsFragmentDirections.actionChatsFragmentToChatFragment(it)
                        )
                    }
                }
            }
        }
    }
}