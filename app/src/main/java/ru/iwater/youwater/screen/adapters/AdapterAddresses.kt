package ru.iwater.youwater.screen.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.Address
import ru.iwater.youwater.databinding.ItemAddressBinding
import ru.iwater.youwater.screen.adapters.AdapterAddresses.*

class AdapterAddresses(private val onAddressItemListener: OnAddressItemListener) : ListAdapter<Address, AddressHolder>(AddressDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        return AddressHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = getItem(position)
        holder.bindingAddress(address, onAddressItemListener)

    }


    class AddressHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindingAddress(address: Address, onAddressItemListener: OnAddressItemListener) {
            binding.address = address
            binding.onAddressItemClick = onAddressItemListener
            binding.ivDeleteAddress.setBackgroundColor(Color.TRANSPARENT)
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


    companion object AddressDiffUtilCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    interface OnAddressItemListener {
        fun onDeleteAddressClick(address: Address)
    }
}