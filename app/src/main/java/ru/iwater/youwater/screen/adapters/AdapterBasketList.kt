package ru.iwater.youwater.screen.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.ItemBasketProductBinding

class AdapterBasketList(
//    private val onClickListener: OnClickItemProduct,
    private val onProductItemListener: OnProductItemListener
) :
    ListAdapter<Product, AdapterBasketList.BasketHolder>(BasketProductDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketHolder {
        return BasketHolder.from(parent, onProductItemListener)
    }

    override fun onBindViewHolder(holder: BasketHolder, position: Int) {
        val product = getItem(position)
        holder.bindingProduct(product)
    }


    class BasketHolder(val binding: ItemBasketProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bindingProduct(product: Product) {
                binding.product = product
//                binding.productItemClick = onProductItemListener
                binding.tvSumProduct.text = product.count.toString()
                binding.tvSumDiscount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.executePendingBindings()
                if (product.category == 20) {
                    binding.btnPlusProduct.isEnabled = false
                    binding.btnMinus.isEnabled = false
                } else {
                    binding.btnPlusProduct.isEnabled = true
                    binding.btnMinus.isEnabled = true
                }
            }

            companion object {
                fun from(parent: ViewGroup, onProductItemListener: OnProductItemListener): BasketHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ItemBasketProductBinding.inflate(layoutInflater, parent, false)
                    binding.productItemClick = onProductItemListener
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
//
//    class OnClickItemProduct(val clickListener: (product: Product) -> Unit) {
//        fun onClick(product: Product) = clickListener(product)
//    }

    interface OnProductItemListener {
        fun deleteProductClick(product: Product)
        fun addProduct(product: Product)
        fun minusProduct(product: Product)
    }
}