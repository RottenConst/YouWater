package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.iwater.youwater.databinding.BannerInfoSheetFragmentBinding
import ru.iwater.youwater.theme.YourWaterTheme

class BannerInfoBottomSheetFragment: BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BannerInfoSheetFragmentBinding.inflate(inflater)
        val namePromo = BannerInfoBottomSheetFragmentArgs.fromBundle(requireArguments()).namePromo
        val promoDescription = BannerInfoBottomSheetFragmentArgs.fromBundle(requireArguments()).promoDescription

        binding.composeViewBannerInfoScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    BannerinfoScreen(namePromo = namePromo, promoDescription = promoDescription)
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BottomSheetDialogFragment()
    }
}