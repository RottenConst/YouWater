package ru.iwater.youwater.screen.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.RawAddress
import ru.iwater.youwater.databinding.ItemAddressBinding
import ru.iwater.youwater.screen.adapters.AdapterAddresses.*

class AdapterAddresses(private val onAddressItemListener: OnAddressItemListener) : ListAdapter<RawAddress, AddressHolder>(AddressDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        return AddressHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = getItem(position)
        holder.bindingAddress(address, onAddressItemListener)

    }


    class AddressHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindingAddress(address: RawAddress, onAddressItemListener: OnAddressItemListener) {
            binding.address = address
            binding.onAddressItemClick = onAddressItemListener
            binding.ivDeleteAddress.setBackgroundColor(Color.TRANSPARENT)
            if (address.notice.isNullOrEmpty()) {
                binding.greyLine.visibility = View.GONE
                binding.tvAddressNoticeLabel.visibility = View.GONE
            } else {
                binding.greyLine.visibility = View.VISIBLE
                binding.tvAddressNoticeLabel.visibility = View.VISIBLE
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from (parent: ViewGroup): AddressHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAddressBinding.inflate(layoutInflater, parent, false)
                return AddressHolder(binding)
            }
        }
    }


    companion object AddressDiffUtilCallback : DiffUtil.ItemCallback<RawAddress>() {
        override fun areItemsTheSame(oldItem: RawAddress, newItem: RawAddress): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RawAddress, newItem: RawAddress): Boolean {
            return oldItem == newItem
        }
    }

    interface OnAddressItemListener {
        fun onDeleteAddressClick(address: RawAddress)
    }
}