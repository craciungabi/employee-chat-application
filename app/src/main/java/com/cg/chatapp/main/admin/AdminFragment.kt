package com.cg.chatapp.main.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.cg.chatapp.databinding.FragmentAdminBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminFragment : Fragment() {
    private var fragmentBinding: FragmentAdminBinding? = null
    private val binding get() = fragmentBinding!!

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fragmentBinding == null) {
            fragmentBinding = FragmentAdminBinding.inflate(layoutInflater, container, false)

            binding.lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCollectingStates()
    }

    private fun navigateTo(screen: AdminScreen) {
        when (screen) {
            AdminScreen.ADD_USER ->
                findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAddUserFragment())
            AdminScreen.ADD_EMPLOYEE ->
                findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAddEmployeeFragment())
            AdminScreen.EDIT_EMPLOYEE ->
                findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToManageEmployeesFragment())
        }
    }

    private fun startCollectingStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.items.collectLatest {
                        binding.adminItems.adapter =
                            AdminItemsAdapter(context as Context, it)
                    }
                }
                launch {
                    viewModel.navigateToScreen.collect {
                        navigateTo(it)
                    }
                }
            }
        }
    }
}
