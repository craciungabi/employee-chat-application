package com.cg.chatapp.main.employees

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cg.chatapp.R
import com.cg.chatapp.SharedPrefsRepository
import com.cg.chatapp.databinding.FilterModalBinding
import com.cg.chatapp.databinding.FragmentEmployeesBinding
import com.cg.chatapp.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmployeesFragment : Fragment() {
    private var fragmentBinding: FragmentEmployeesBinding? = null
    private val binding get() = fragmentBinding!!

    private val prefs: SharedPrefsRepository by lazy {
        SharedPrefsRepository(requireContext())
    }

    private val dialog: BottomSheetDialog by lazy {
        BottomSheetDialog(requireContext())
    }

    private val viewModel: EmployeesViewModel by viewModels()

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
            fragmentBinding = FragmentEmployeesBinding.inflate(layoutInflater, container, false)

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

    override fun onResume() {
        super.onResume()

        viewModel.populate()
    }

    private fun setupRecyclerView() {
        binding.employeesRv.apply {
            adapter = EmployeesAdapter(lifecycleScope, viewModel, viewLifecycleOwner)
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_action, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.log_out) logOutUser()
        if (item.itemId == R.id.profile) {
            findNavController().navigate(
                EmployeesFragmentDirections.actionEmployeesFragmentToEditEmployeeFragment(
                    prefs.getUser()
                )
            )
        }
        if (item.itemId == R.id.filter) {
            viewModel.getFilterItems()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logOutUser() {
        prefs.logOutUser()

        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun showFilterModal(list: List<String>) {
        val modal = setupFilterModal(list) {
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.setContentView(modal)
        dialog.show()
    }

    private fun setupFilterModal(
        list: List<String>,
        onClicked: () -> Unit,
    ): View {
        val inflater = LayoutInflater.from(requireContext())
        val bottomSheetBinding = FilterModalBinding.inflate(inflater, null, false)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            list
        )
        bottomSheetBinding.listView.adapter = adapter
        bottomSheetBinding.listView.setOnItemClickListener { _, _, position, _ ->
            viewModel.repopulate(bottomSheetBinding.listView.adapter.getItem(position) as String)
            onClicked.invoke()
        }
        return bottomSheetBinding.root
    }

    private fun startCollectingStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.employees.collect {
                        (binding.employeesRv.adapter as EmployeesAdapter).submitList(it)
                    }
                }
                launch {
                    viewModel.filteredEmployees.collect {
                        (binding.employeesRv.adapter as EmployeesAdapter).submitList(it)
                    }
                }
                launch {
                    viewModel.navigateToChat.collectLatest {
                        if (it.isNotEmpty()) {
                            findNavController().navigate(
                                EmployeesFragmentDirections.actionEmployeesFragmentToChatDest(
                                    it
                                )
                            )
                        }
                    }
                }
                launch {
                    viewModel.navigateToDetails.collectLatest {
                        if (it.isNotEmpty()) {
                            findNavController().navigate(
                                EmployeesFragmentDirections.actionEmployeesFragmentToEmployeeDetailsFragment(
                                    it
                                )
                            )
                        }
                    }
                }
                launch {
                    viewModel.filters.collectLatest {
                        showFilterModal(it)
                    }
                }
            }
        }
    }
}