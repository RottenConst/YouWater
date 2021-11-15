package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.databinding.ItemCardProductBinding
import ru.iwater.youwater.data.Product

/**
 * адаптер для списка продуктов товаров
 */
class AdapterProductList(
    private val onClickListener: OnClickListener,
    private val onProductItemClickListener: OnProductItemClickListener
                         ) : ListAdapter<Product, AdapterProductList.AdapterProductHolder>(ProductDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProductHolder {
        return AdapterProductHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AdapterProductHolder, position: Int) {
        val product = getItem(position)
        holder.binding.cvProduct.setOnClickListener {
            onClickListener.onClick(product)
        }
        holder.bindProductCard(product, onProductItemClickListener)
    }

    class AdapterProductHolder(val binding: ItemCardProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindProductCard(product: Product, onProductItemClickListener: OnProductItemClickListener) {
            binding.product = product
            binding.productItemClick = onProductItemClickListener
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

    class OnClickListener(val clickListener: (product: Product) -> Unit) {
        fun onClick(product: Product) = clickListener(product)
    }

    interface OnProductItemClickListener {
        fun onProductItemClicked(product: Product)
    }
}