package com.cg.chatapp.main.admin.manage_employees

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cg.chatapp.FirebaseStorage
import com.cg.chatapp.databinding.EditEmployeeItemBinding
import com.cg.chatapp.main.admin.edit_employee.EditEmployeeItemViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ManageEmployeesAdapter(
    private val scope: LifecycleCoroutineScope,
    private val manageEmployeesViewModel: ManageEmployeesViewModel,
    private val employeesLifeCycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val employees = mutableListOf<EditEmployeeItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = EditEmployeeItemBinding.inflate(layoutInflater, parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as EmployeeViewHolder).setEmployee(employees[position])
    }

    override fun getItemCount(): Int = employees.size

    fun submitList(employees: List<EditEmployeeItemViewModel>) {
        val diffCallback = EmployeesDiffCallback(this.employees, employees)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.employees.apply {
            clear()
            addAll(employees)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    private inner class EmployeeViewHolder(
        private val binding: EditEmployeeItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setEmployee(editEmployeeItemViewModel: EditEmployeeItemViewModel) {
            binding.apply {
                employee = editEmployeeItemViewModel
                viewModel = manageEmployeesViewModel
                lifecycleOwner = employeesLifeCycleOwner

                scope.launch {
                    val path = FirebaseStorage().getPath(editEmployeeItemViewModel.username)
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
        private val oldItems: List<EditEmployeeItemViewModel>,
        private val newItems: List<EditEmployeeItemViewModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].username == newItems[newItemPosition].username

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].employeeName == newItems[newItemPosition].employeeName
    }
}