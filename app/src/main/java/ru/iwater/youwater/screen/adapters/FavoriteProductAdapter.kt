package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.FavoriteProduct
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.ItemFavoriteProductBinding

class FavoriteProductAdapter(private val onFavoriteProductClickListener: OnFavoriteProductClickListener) :
    ListAdapter<FavoriteProduct, FavoriteProductAdapter.FavoriteProductHolder>(FavoriteDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteProductHolder {
        return FavoriteProductHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FavoriteProductHolder, position: Int) {
        val favorite = getItem(position)
        holder.bindingFavoriteCard(favorite, onFavoriteProductClickListener)
    }

    class FavoriteProductHolder(val binding: ItemFavoriteProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindingFavoriteCard(favoriteProduct: FavoriteProduct, onFavoriteProductClickListener: OnFavoriteProductClickListener) {
            binding.favorite = favoriteProduct
            binding.onFavoriteItemClick = onFavoriteProductClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FavoriteProductHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFavoriteProductBinding.inflate(layoutInflater, parent, false)
                return FavoriteProductHolder(binding)
            }
        }
    }

    companion object FavoriteDiffCallback: DiffUtil.ItemCallback<FavoriteProduct>() {
        override fun areItemsTheSame(oldItem: FavoriteProduct, newItem: FavoriteProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FavoriteProduct,
            newItem: FavoriteProduct
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface OnFavoriteProductClickListener {
        fun onLikeItemClick(favoriteProduct: FavoriteProduct)
        fun addItemInBasked(favoriteProduct: FavoriteProduct)
        fun aboutFavoriteClick(favoriteProduct: FavoriteProduct)
    }
}