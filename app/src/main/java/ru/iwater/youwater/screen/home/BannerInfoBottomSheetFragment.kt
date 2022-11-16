package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.iwater.youwater.databinding.BannerInfoSheetFragmentBinding

class BannerInfoBottomSheetFragment: BottomSheetDialogFragment() {

    private val binding : BannerInfoSheetFragmentBinding by lazy { BannerInfoSheetFragmentBinding.inflate(
        LayoutInflater.from(this.context)) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val namePromo = BannerInfoBottomSheetFragmentArgs.fromBundle(requireArguments()).namePromo
        val promoDescription = BannerInfoBottomSheetFragmentArgs.fromBundle(requireArguments()).promoDescription
        binding.tvNamePromo.text = namePromo
        binding.tvPromoDescription.text = promoDescription
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BottomSheetDialogFragment()
    }
}