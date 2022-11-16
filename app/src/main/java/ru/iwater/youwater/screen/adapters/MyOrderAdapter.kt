package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.databinding.ItemOrderBinding
import timber.log.Timber

class MyOrderAdapter(
    private val onclickReplayButton: onReplayLastOrder
) : ListAdapter<MyOrder, MyOrderAdapter.MyOrderHolder>(OrderDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderHolder {
        return MyOrderHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyOrderHolder, position: Int) {
        val myOrder = getItem(position)
        holder.bindingMyOrder(myOrder, onclickReplayButton)
    }

    class MyOrderHolder(val binding: ItemOrderBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindingMyOrder(myOrder: MyOrder, onReplayLastOrder: onReplayLastOrder) {
            binding.myOrder = myOrder
            val adapter = OrderProductAdapter()
            binding.rvProductOrder.adapter = adapter

            binding.btnReplayOrder.setOnClickListener {
                Timber.d("Click ??????")
                onReplayLastOrder.onClickReplayButton(myOrder.id)
            }
            adapter.submitList(myOrder.products)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyOrderHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
                return MyOrderHolder(binding)
            }
        }
    }

    companion object OrderDiffCallback: DiffUtil.ItemCallback<MyOrder>() {
        override fun areItemsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
            return oldItem == newItem
        }
    }

    interface onReplayLastOrder {
        fun onClickReplayButton(idOrder: Int)
    }
}