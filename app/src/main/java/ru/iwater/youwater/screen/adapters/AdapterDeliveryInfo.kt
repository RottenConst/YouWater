package ru.iwater.youwater.screen.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.screen.more.DeliveryStdFragment
import ru.iwater.youwater.screen.more.DeliverySuburbsFragment
import ru.iwater.youwater.screen.more.DeliveryTwoHourFragment

class AdapterDeliveryInfo(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DeliveryTwoHourFragment()
            1 -> DeliveryStdFragment()
            2 -> DeliverySuburbsFragment()
            else -> {BaseFragment()}
        }
    }

}