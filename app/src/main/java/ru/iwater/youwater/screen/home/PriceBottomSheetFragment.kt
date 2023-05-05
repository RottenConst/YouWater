package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.iwater.youwater.databinding.BottomSheetPriceFragmentBinding
import ru.iwater.youwater.theme.YourWaterTheme

class PriceBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BottomSheetPriceFragmentBinding.inflate(inflater)
        val priceProduct = PriceBottomSheetFragmentArgs.fromBundle(requireArguments()).priceProduct
        val prices = priceProduct.removeSuffix(";")
        val priceList = prices.split(";")

        binding.composeViewPriceListScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    PriceListScreen(prices = priceList)
                }
            }
        }
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance() = PriceBottomSheetFragment()
    }
}