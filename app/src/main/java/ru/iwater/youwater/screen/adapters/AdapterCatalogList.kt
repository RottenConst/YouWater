package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.databinding.ItemCatalogBinding
import ru.iwater.youwater.domain.TypeProduct
import ru.iwater.youwater.screen.adapters.AdapterCatalogList.HolderCatalogList

class AdapterCatalogList : ListAdapter<TypeProduct, HolderCatalogList>(TypeProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCatalogList {
        return HolderCatalogList.from(parent)
    }

    override fun onBindViewHolder(holder: HolderCatalogList, position: Int) {
        val item = getItem(position)
        holder.bindCatalog(item)
    }

    class HolderCatalogList(val binding: ItemCatalogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindCatalog(typeProduct: TypeProduct) {
            binding.typeProduct = typeProduct
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HolderCatalogList {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCatalogBinding.inflate(layoutInflater, parent, false)
                return HolderCatalogList(binding)
            }
        }
    }

    companion object TypeProductDiffCallback : DiffUtil.ItemCallback<TypeProduct>() {
        override fun areItemsTheSame(oldItem: TypeProduct, newItem: TypeProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TypeProduct, newItem: TypeProduct): Boolean {
            return oldItem == newItem
        }
    }
}