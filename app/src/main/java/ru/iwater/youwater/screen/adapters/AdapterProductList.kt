package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.databinding.ItemCardProductBinding
import ru.iwater.youwater.domain.Product

class AdapterProductList : ListAdapter<Product, AdapterProductList.AdapterProductHolder>(ProductDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProductHolder {
        return AdapterProductHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AdapterProductHolder, position: Int) {
        val product = getItem(position)
        holder.bindProductCard(product)
    }

    class AdapterProductHolder(val binding: ItemCardProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindProductCard(product: Product) {
            binding.product = product
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup) : AdapterProductHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCardProductBinding.inflate(layoutInflater, parent, false)
                return AdapterProductHolder(binding)
            }
        }
    }

    companion object ProductDiffCallback: DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}