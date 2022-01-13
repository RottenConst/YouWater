package ru.iwater.youwater.screen.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.BankCard
import ru.iwater.youwater.databinding.ItemBankCardBinding

class BankCardAdapter(private val onCardItemListener: OnCardItemListener) : ListAdapter<BankCard, BankCardAdapter.BankCardHolder>(BankCardDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankCardHolder {
        return BankCardHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BankCardHolder, position: Int) {
        val card = getItem(position)
        holder.bindBankCard(card, onCardItemListener)
    }

    class BankCardHolder(val binding: ItemBankCardBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindBankCard(bankCard: BankCard, onCardItemListener: OnCardItemListener) {
            binding.bankCard = bankCard
            binding.onCardItemLister = onCardItemListener
            binding.ibDeleteCard.setBackgroundColor(Color.TRANSPARENT)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): BankCardHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBankCardBinding.inflate(layoutInflater, parent, false)
                return BankCardHolder(binding)
            }
        }
    }

    companion object BankCardDiffCallback: DiffUtil.ItemCallback<BankCard>() {
        override fun areItemsTheSame(oldItem: BankCard, newItem: BankCard): Boolean {
            return oldItem.numberCard == newItem.numberCard
        }

        override fun areContentsTheSame(oldItem: BankCard, newItem: BankCard): Boolean {
            return oldItem == newItem
        }
    }

    interface OnCardItemListener {
        fun onDeleteCardClick(bankCard: BankCard)
    }
}