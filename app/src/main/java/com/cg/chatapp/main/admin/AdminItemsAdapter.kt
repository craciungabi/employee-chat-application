package com.cg.chatapp.main.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.cg.chatapp.R
import com.cg.chatapp.databinding.AdminItemBinding

internal class AdminItemsAdapter(context: Context, items: List<AdminItemViewModel>) :
    ArrayAdapter<AdminItemViewModel>(context, R.layout.admin_item, items) {
    private val layoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.tag as? AdminItemBinding
            ?: DataBindingUtil.inflate(layoutInflater, R.layout.admin_item, parent, false)

        binding.root.tag = binding
        binding.viewModel = getItem(position)

        return binding.root
    }
}
