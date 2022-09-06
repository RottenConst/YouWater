package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.databinding.ItemPriceBinding
import timber.log.Timber

class PriceProductAdapter() : ListAdapter<String, PriceProductAdapter.PriceProductHolder> (PriceProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceProductHolder {
        return PriceProductHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PriceProductHolder, position: Int) {
        val prices = getItem(position)
        holder.bindingPrice(prices)
    }


    class PriceProductHolder(val binding: ItemPriceBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindingPrice(prices: String) {
            val price = prices.split(":")
            if (price[0] == "1") {
                binding.tvCountProducts.text = "От одной шт."
            } else {
                binding.tvCountProducts.text = "От ${price[0]} шт."
            }
            binding.tvPriceProduct.text = "${price[1]}pyб./шт."
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): PriceProductHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPriceBinding.inflate(layoutInflater, parent, false)
                return PriceProductHolder(binding)
            }
        }
    }

    companion object PriceProductDiffCallback: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}