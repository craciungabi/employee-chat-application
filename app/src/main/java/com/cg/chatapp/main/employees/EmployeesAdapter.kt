package com.cg.chatapp.main.employees

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cg.chatapp.FirebaseStorage
import com.cg.chatapp.databinding.EmployeeItemBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EmployeesAdapter(
    private val scope: LifecycleCoroutineScope,
    private val employeesViewModel: EmployeesViewModel,
    private val employeesLifeCycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val employees = mutableListOf<EmployeeDetailsItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = EmployeeItemBinding.inflate(layoutInflater, parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as EmployeeViewHolder).setEmployee(employees[position])
    }

    override fun getItemCount(): Int = employees.size

    fun submitList(employees: List<EmployeeDetailsItemViewModel>) {
        val diffCallback = EmployeesDiffCallback(this.employees, employees)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.employees.apply {
            clear()
            addAll(employees)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    private inner class EmployeeViewHolder(
        private val binding: EmployeeItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setEmployee(employeeDetailsItemViewModel: EmployeeDetailsItemViewModel) {
            binding.apply {
                employee = employeeDetailsItemViewModel
                viewModel = employeesViewModel
                lifecycleOwner = employeesLifeCycleOwner

                scope.launch {
                    val path = FirebaseStorage().getPath(employeeDetailsItemViewModel.username)
                    if (path.isNotEmpty()) {
                        Picasso.get()
                            .load(path)
                            .into(employeeImage)
                    }
                }
            }
        }
    }

    private inner class EmployeesDiffCallback(
        private val oldItems: List<EmployeeDetailsItemViewModel>,
        private val newItems: List<EmployeeDetailsItemViewModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].username == newItems[newItemPosition].username

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].employeeName == newItems[newItemPosition].employeeName
    }
}