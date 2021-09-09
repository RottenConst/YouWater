package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.iwater.youwater.R
import ru.iwater.youwater.databinding.ItemCardProductBinding
import ru.iwater.youwater.domain.Product

class CatalogWaterAdapter :
    ListAdapter<Product, CatalogWaterAdapter.CatalogWaterHolder>(ProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogWaterHolder {
        return CatalogWaterHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CatalogWaterHolder, position: Int) {
        val item = getItem(position)
        holder.bindCardProduct(item)
    }

    class CatalogWaterHolder(val binding: ItemCardProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindCardProduct(product: Product) {
            binding.product = product
            binding.executePendingBindings()

            Glide.with(itemView.context)
                .load(R.mipmap.product_image)
                .into(binding.ivProduct)
        }

        companion object {
            fun from(parent: ViewGroup): CatalogWaterHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCardProductBinding.inflate(layoutInflater, parent, false)
                return CatalogWaterHolder(binding)
            }
        }

    }

    companion object ProductDiffCallback : DiffUtil.ItemCallback <Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}