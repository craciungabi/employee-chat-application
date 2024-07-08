package com.cg.chatapp.main.admin.edit_employee

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.cg.chatapp.FirebaseStorage
import com.cg.chatapp.createAlertDialog
import com.cg.chatapp.databinding.FragmentEditEmployeeBinding
import com.cg.chatapp.hideKeyboard
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditEmployeeFragment : Fragment() {
    private val args: EditEmployeeFragmentArgs by navArgs()

    private lateinit var binding: FragmentEditEmployeeBinding

    private val viewModel: EditEmployeeViewModel by viewModels()

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                lifecycleScope.launch {
                    FirebaseStorage().uploadImage(args.employeeUsername, data?.data)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditEmployeeBinding.inflate(layoutInflater, container, false)

        activity?.title = args.employeeUsername

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        lifecycleScope.launch {
            val path = FirebaseStorage().getPath(args.employeeUsername)
            if (path.isNotEmpty()) {
                Picasso.get()
                    .load(path)
                    .into(binding.imageModify)
            }
        }

        binding.imageModify.setOnClickListener {
            val intent = Intent()
                .setType("*/.jpg*")
                .setAction(Intent.ACTION_GET_CONTENT)

            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCollectingStates()
    }

    override fun onResume() {
        super.onResume()

        viewModel.populate(args.employeeUsername)
    }

    private fun showAlertDialog() {
        createAlertDialog(
            requireContext(),
            "Confirm action",
            "Are you sure you want to save changes?",
            {
                viewModel.saveEditEmployee()
                activity?.supportFragmentManager?.popBackStackImmediate()
                this.hideKeyboard()
            },
            { this.hideKeyboard() }
        )
    }

    private fun startCollectingStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.showToast.collectLatest {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    viewModel.saveEditEmployee.collect {
                        showAlertDialog()
                    }
                }
            }
        }
    }
}