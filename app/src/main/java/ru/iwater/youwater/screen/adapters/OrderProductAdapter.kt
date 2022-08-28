package ru.iwater.youwater.screen.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.ItemOrderProductBinding

class OrderProductAdapter : ListAdapter<Product, OrderProductAdapter.OrderProductHolder>(OrderProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductHolder {
        return OrderProductHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderProductHolder, position: Int) {
        val product = getItem(position)
        holder.bindingProduct(product)
    }


    class OrderProductHolder(val binding: ItemOrderProductBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindingProduct(product: Product) {
            binding.product = product
            "${product.count}шт.".also { binding.tvCountOfProductOrder.text = it }
            binding.tvCostOrderNoDiscount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): OrderProductHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderProductBinding.inflate(layoutInflater, parent, false)
                return OrderProductHolder(binding)
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