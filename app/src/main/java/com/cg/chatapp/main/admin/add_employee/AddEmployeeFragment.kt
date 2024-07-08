package com.cg.chatapp.main.admin.add_employee

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
import com.cg.chatapp.createAlertDialog
import com.cg.chatapp.databinding.FragmentAddEmployeeBinding
import com.cg.chatapp.hideKeyboard
import kotlinx.coroutines.launch

class AddEmployeeFragment : Fragment() {
    private var fragmentBinding: FragmentAddEmployeeBinding? = null
    private val binding get() = fragmentBinding!!

    private val viewModel: AddEmployeeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fragmentBinding == null) {
            fragmentBinding = FragmentAddEmployeeBinding.inflate(layoutInflater, container, false)

            activity?.title = "Add employee"

            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCollectingStates()
    }

    private fun showAlertDialog() {
        createAlertDialog(
            requireContext(),
            "Confirm action",
            "Are you sure you want to add employee?",
            {
                viewModel.addEmployee()
//                activity?.supportFragmentManager?.popBackStackImmediate()
                this.hideKeyboard()
            },
            { this.hideKeyboard() }
        )
    }

    private fun startCollectingStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.showToast.collect {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    viewModel.addEmployee.collect {
                        showAlertDialog()
                    }
                }
            }
        }
    }
}