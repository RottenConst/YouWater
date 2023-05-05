package ru.iwater.youwater.screen.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentDeliveryInfoBinding
import ru.iwater.youwater.theme.YourWaterTheme

class DeliveryInfoFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentDeliveryInfoBinding.inflate(inflater)
        binding.composeViewDeliveryInfoScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    DeliveryInfoScreen()
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DeliveryInfoFragment()
    }
}