package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.iwater.youwater.R
import ru.iwater.youwater.data.PromoBanner
import ru.iwater.youwater.databinding.ItemPromoBinding
import ru.iwater.youwater.network.ImageUrl

class PromoBannerAdapter(
    private val onClickListener: OnBannerItemClickListener
) : ListAdapter<PromoBanner, PromoBannerAdapter.PromoBannerHolder>(PromoBannerDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoBannerHolder {
        return PromoBannerHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PromoBannerHolder, position: Int) {
        val banner = getItem(position)
        holder.bindingBanner(banner, onClickListener)
    }

    class PromoBannerHolder(val binding: ItemPromoBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindingBanner(banner: PromoBanner, onClickListener: OnBannerItemClickListener) {
            val imgUrl = "$ImageUrl/${banner.picture}"//prod
            Glide.with(binding.ivPromo)
                .load(imgUrl)
                .apply(RequestOptions()
                    .placeholder(R.drawable.ic_your_water_logo)
                    .error(R.drawable.ic_your_water_logo)
                )
                .into(binding.ivPromo)
            binding.ivPromo.setOnClickListener {
                onClickListener.onBannerItemClicked(banner)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): PromoBannerHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPromoBinding.inflate(layoutInflater, parent, false)
                return PromoBannerHolder(binding)
            }
        }
    }


    companion object PromoBannerDiffCallback: DiffUtil.ItemCallback<PromoBanner>() {
        override fun areItemsTheSame(oldItem: PromoBanner, newItem: PromoBanner): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PromoBanner, newItem: PromoBanner): Boolean {
            return oldItem.id == newItem.id
        }
    }

    interface OnBannerItemClickListener {
        fun onBannerItemClicked(banner: PromoBanner)
    }
}