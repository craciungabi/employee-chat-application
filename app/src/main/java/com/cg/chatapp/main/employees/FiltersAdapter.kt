//package com.cg.chatapp.main.employees
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import com.cg.chatapp.R
//import com.epix.presentationtv.R
//import com.epix.presentationtv.databinding.IconHeaderItemBinding
//
//class FiltersAdapter(
////    private val lifecycleOwner: LifecycleOwner,
//    context: Context,
//    menuItems: List<String>
//) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, menuItems) {
//    private val layoutInflater: LayoutInflater =
//        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//    @SuppressLint("ViewHolder")
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val binding = IconHeaderItemBinding.inflate(layoutInflater)
////        val binding = convertView?.tag as? IconHeaderItemBinding
////            ?: DataBindingUtil.inflate(layoutInflater, R.layout.icon_header_item, parent, false)
//
////        binding.lifecycleOwner = lifecycleOwner
//
//
////        binding.layout.setOnClickListener {
////            binding.layout.visibility = View.GONE
////        }
//
//        return binding.root
//    }
//}
