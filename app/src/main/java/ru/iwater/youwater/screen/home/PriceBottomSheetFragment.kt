package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.iwater.youwater.databinding.BottomSheetPriceFragmentBinding
import ru.iwater.youwater.screen.adapters.PriceProductAdapter
import timber.log.Timber

class PriceBottomSheetFragment : BottomSheetDialogFragment() {


    private val binding : BottomSheetPriceFragmentBinding by lazy { BottomSheetPriceFragmentBinding.inflate(LayoutInflater.from(this.context)) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adapter = PriceProductAdapter()
        val priceProduct = PriceBottomSheetFragmentArgs.fromBundle(requireArguments()).priceProduct
        val prices = priceProduct.removeSuffix(";")
        Timber.d("PRICES $prices")
        val priceList = prices.split(";")
        Timber.d("PRICES LIST ${priceList[1]}")
        binding.rvPrice.adapter = adapter
        adapter.submitList(priceList)
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance() = PriceBottomSheetFragment()
    }
}