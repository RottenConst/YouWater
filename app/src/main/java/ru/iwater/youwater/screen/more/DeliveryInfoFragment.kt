package ru.iwater.youwater.screen.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentDeliveryInfoBinding
import ru.iwater.youwater.screen.adapters.AdapterDeliveryInfo

class DeliveryInfoFragment : BaseFragment() {
    private lateinit var adapter: AdapterDeliveryInfo
    private val binding: FragmentDeliveryInfoBinding by lazy { initBind(LayoutInflater.from(this.context)) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdapterDeliveryInfo(this)
        binding.pager.adapter = adapter
        TabLayoutMediator(binding.tabDelivery, binding.pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "В этот же день"
                }
                1 -> {
                    tab.text = "На следующий день"
                }
                2 -> {
                    tab.text = "В пригороды"
                }
            }
        }.attach()
    }

    private fun initBind(inflater: LayoutInflater) = FragmentDeliveryInfoBinding.inflate(inflater)

    companion object {
        @JvmStatic
        fun newInstance() =
            DeliveryInfoFragment()
    }
}