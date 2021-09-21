package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.databinding.ItemCategoryProductBinding
import ru.iwater.youwater.domain.Product
import ru.iwater.youwater.domain.TypeProduct
import timber.log.Timber

class CatalogWaterAdapter :
    ListAdapter<Pair<TypeProduct, List<Product>>, CatalogWaterAdapter.CatalogWaterHolder>(TypeProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogWaterHolder {
        return CatalogWaterHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CatalogWaterHolder, position: Int) {
        val item = getItem(position)
        holder.bindCardProduct(item)
    }

    class CatalogWaterHolder(val binding: ItemCategoryProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindCardProduct(catalogItem: Pair<TypeProduct, List<Product>>) {
            binding.category = catalogItem.first
            binding.product = catalogItem.second
            binding.rvProduct.adapter = AdapterProductList()
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CatalogWaterHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCategoryProductBinding.inflate(layoutInflater, parent, false)
                return CatalogWaterHolder(binding)
            }
        }

    }

    companion object TypeProductDiffCallback : DiffUtil.ItemCallback <Pair<TypeProduct, List<Product>>>() {
        override fun areItemsTheSame(oldItem: Pair<TypeProduct, List<Product>>, newItem: Pair<TypeProduct, List<Product>>): Boolean {
            return  oldItem.first.id == newItem.first.id
        }

        override fun areContentsTheSame(oldItem: Pair<TypeProduct, List<Product>>, newItem: Pair<TypeProduct, List<Product>>): Boolean {
            return oldItem == newItem
        }
    }
}