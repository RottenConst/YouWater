package ru.iwater.youwater.screen.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.ItemOrderProductNoEditBinding

class OrderProductNoEditAdapter : ListAdapter<Product, OrderProductNoEditAdapter.OrderProductNoEditHolder>(OrderProductNoEditAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductNoEditHolder {
        return OrderProductNoEditHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderProductNoEditHolder, position: Int) {
        val product = getItem(position)
        holder.bindingProduct(product)
    }


    class OrderProductNoEditHolder(val binding: ItemOrderProductNoEditBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindingProduct(product: Product) {
            binding.product = product
            "${product.count}шт.".also { binding.tvCountProductOrder.text = it }
            binding.tvCostOrderNoDiscount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): OrderProductNoEditHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderProductNoEditBinding.inflate(layoutInflater, parent, false)
                return OrderProductNoEditHolder(binding)
            }
        }
    }

    companion object OrderProductDiffCallback: DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}