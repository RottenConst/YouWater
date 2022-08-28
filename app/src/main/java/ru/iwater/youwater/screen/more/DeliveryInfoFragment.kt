package ru.iwater.youwater.screen.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentDeliveryInfoBinding
import ru.iwater.youwater.screen.adapters.AdapterDeliveryInfo

class DeliveryInfoFragment : BaseFragment() {
    private lateinit var adapter: AdapterDeliveryInfo
    private lateinit var pagerDelivery: ViewPager2
    private val binding: FragmentDeliveryInfoBinding by lazy { initBind(LayoutInflater.from(this.context)) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
                    tab.text = "Доставка на следующий день"
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
        fun newInstance(param1: String, param2: String) =
            DeliveryInfoFragment()
    }
}