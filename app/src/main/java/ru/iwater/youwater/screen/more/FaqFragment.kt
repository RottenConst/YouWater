package ru.iwater.youwater.screen.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import ru.iwater.youwater.databinding.FragmentFaqBinding
import ru.iwater.youwater.theme.YourWaterTheme

class FaqFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFaqBinding.inflate(inflater)
        binding.composeViewFaqScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    FaqScreen()
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FaqFragment()
    }
}