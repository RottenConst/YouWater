package ru.iwater.youwater.screen.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentContactBinding
import ru.iwater.youwater.theme.YourWaterTheme

class ContactFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentContactBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.composeViewContactScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    ContactScreen(this@ContactFragment)
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContactFragment()
    }
}