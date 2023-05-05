package ru.iwater.youwater.screen.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentAboutCompanyBinding
import ru.iwater.youwater.theme.YourWaterTheme

class AboutCompanyFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAboutCompanyBinding.inflate(inflater)
        binding.composeViewAboutCompanyScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    AboutCompanyScreen()
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AboutCompanyFragment()
    }
}