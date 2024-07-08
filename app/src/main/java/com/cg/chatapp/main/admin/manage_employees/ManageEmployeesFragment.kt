package com.cg.chatapp.main.admin.manage_employees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cg.chatapp.createAlertDialog
import com.cg.chatapp.databinding.FragmentManageEmployeesBinding
import com.cg.chatapp.hideKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManageEmployeesFragment : Fragment() {
    private var fragmentBinding: FragmentManageEmployeesBinding? = null
    private val binding get() = fragmentBinding!!

    private val viewModel: ManageEmployeesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fragmentBinding == null) {
            fragmentBinding =
                FragmentManageEmployeesBinding.inflate(layoutInflater, container, false)

            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }

        activity?.title = "Edit Employees"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        startCollectingStates()
    }

    override fun onResume() {
        super.onResume()

        viewModel.populate()
    }

    private fun setupRecyclerView() {
        binding.employeesRv.apply {
            adapter = ManageEmployeesAdapter(lifecycleScope, viewModel, viewLifecycleOwner)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showAlertDialog(username: String) {
        createAlertDialog(
            requireContext(),
            "Confirm action",
            "Are you sure you want to delete employee?",
            {
                viewModel.deleteEmployee(username)
                this.hideKeyboard()
            },
            { this.hideKeyboard() }
        )
    }

    private fun startCollectingStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.employees.collect {
                        (binding.employeesRv.adapter as ManageEmployeesAdapter).submitList(it)
                    }
                }
                launch {
                    viewModel.navigateToDetails.collectLatest {
                        if (it.isNotEmpty()) {
                            findNavController().navigate(
                                ManageEmployeesFragmentDirections.actionManageEmployeesFragmentToEditEmployeeFragment(
                                    it
                                )
                            )
                        }
                    }
                }
                launch {
                    viewModel.deleteEmployee.collect {
                        showAlertDialog(it)
                    }
                }
                launch {
                    viewModel.showToast.collect {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}