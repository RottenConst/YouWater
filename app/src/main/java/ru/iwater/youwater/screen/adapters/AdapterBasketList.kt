package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.ItemBasketProductBinding

class AdapterBasketList(
    private val onProductItemListener: OnProductItemListener
) :
    ListAdapter<Product, AdapterBasketList.BasketHolder>(BasketProductDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketHolder {
        return BasketHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BasketHolder, position: Int) {
        val product = getItem(position)
        holder.bindingProduct(product, onProductItemListener)
    }


    class BasketHolder(val binding: ItemBasketProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bindingProduct(product: Product, onProductItemListener: OnProductItemListener) {
                binding.product = product
                binding.productItemClick = onProductItemListener
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup): BasketHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ItemBasketProductBinding.inflate(layoutInflater, parent, false)
                    return BasketHolder(binding)
                }
            }
    }

    companion object BasketProductDiffUtilCallback : DiffUtil.ItemCallback <Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    interface OnProductItemListener {
        fun deleteProductClick(product: Product)
        fun addProduct(product: Product)
        fun minusProduct(product: Product)
    }
}