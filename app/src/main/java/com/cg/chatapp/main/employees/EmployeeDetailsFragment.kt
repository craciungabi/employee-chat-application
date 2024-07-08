package com.cg.chatapp.main.employees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.cg.chatapp.FirebaseStorage
import com.cg.chatapp.databinding.FragmentEmployeeDetailsBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EmployeeDetailsFragment : Fragment() {
    private val args: EmployeeDetailsFragmentArgs by navArgs()

    private lateinit var binding: FragmentEmployeeDetailsBinding

    private val viewModel: EmployeeDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeDetailsBinding.inflate(layoutInflater, container, false)

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.populate(args.employeeUsername)
    }
}