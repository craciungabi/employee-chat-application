package com.cg.chatapp.main.admin.add_user

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
import com.cg.chatapp.databinding.FragmentAddUserBinding
import com.cg.chatapp.hideKeyboard
import kotlinx.coroutines.launch


class AddUserFragment : Fragment() {
    private var fragmentBinding: FragmentAddUserBinding? = null
    private val binding get() = fragmentBinding!!

    private val viewModel: AddUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fragmentBinding == null) {
            fragmentBinding = FragmentAddUserBinding.inflate(layoutInflater, container, false)

            activity?.title = "Add user"

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
            "Are you sure you want to add user?",
            {
                viewModel.addUser()
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
                    viewModel.addUser.collect {
                        showAlertDialog()
                    }
                }
            }
        }
    }
}